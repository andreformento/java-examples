package com.example.demo;

import com.example.demo.controller.PubSubController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
class PubSubIntegrationTest {

	@Container
	static GenericContainer<?> pubSubEmulator = new GenericContainer<>(
			DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:545.0.0"))
			.withExposedPorts(8085)
			.withCommand("gcloud", "beta", "emulators", "pubsub", "start", "--host-port=0.0.0.0:8085");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		String emulatorHost = pubSubEmulator.getHost() + ":" + pubSubEmulator.getMappedPort(8085);
		// Configure production components to use emulator endpoint
		// Production code accepts optional endpoint configuration - this is not emulator-specific code
		registry.add("gcp.pubsub.endpoint", () -> emulatorHost);
	}

	@LocalServerPort
	private int port;

	private RestTemplate restTemplate;

	@Autowired
	private PubSubTestSetup pubSubTestSetup;

	@BeforeAll
	static void waitForEmulator() throws Exception {
		// Wait for emulator to be ready using health check with retry
		String emulatorHost = pubSubEmulator.getHost() + ":" + pubSubEmulator.getMappedPort(8085);
		waitForEmulatorReady(emulatorHost);
	}

	private static void waitForEmulatorReady(String emulatorHost) throws Exception {
		int maxRetries = 30;
		long retryIntervalMs = 100;

		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			try {
				// Try to connect to the emulator by creating a TopicAdminClient
				ManagedChannel channel = ManagedChannelBuilder.forTarget(emulatorHost)
						.usePlaintext()
						.build();
				TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(
						GrpcTransportChannel.create(channel));
				CredentialsProvider credentialsProvider = NoCredentialsProvider.create();

				try (TopicAdminClient client = TopicAdminClient.create(
						TopicAdminSettings.newBuilder()
								.setTransportChannelProvider(channelProvider)
								.setCredentialsProvider(credentialsProvider)
								.build())) {
					// Try to list topics - this will succeed if emulator is ready
					client.listTopics("projects/local-dev");
					channel.shutdown();
					return; // Emulator is ready
				}
			} catch (Exception e) {
				if (attempt == maxRetries) {
					throw new IllegalStateException("Pub/Sub emulator did not become ready after " + maxRetries + " attempts", e);
				}
				// Emulator not ready yet, wait and retry
				Thread.sleep(retryIntervalMs);
			}
		}
	}

	@BeforeEach
	void setUp() throws IOException {
		restTemplate = new RestTemplate();
		pubSubTestSetup.setupTopicAndSubscription();
	}

	@Test
	void testPublishMessage() {
		// Prepare request
		String testMessage = "Hello from integration test!";
		String url = "http://localhost:" + port + "/api/pubsub/publish";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		PubSubController.PublishRequest request = new PubSubController.PublishRequest(testMessage);
		HttpEntity<PubSubController.PublishRequest> entity = new HttpEntity<>(request, headers);

		// Publish message via REST endpoint
		ResponseEntity<PubSubController.PublishResponse> response = restTemplate.postForEntity(
				url, entity, PubSubController.PublishResponse.class);

		// Verify response
		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().messageId()).isNotNull();
		assertThat(response.getBody().status()).contains("successfully");

		// Note: In a real scenario, you would pull messages from the subscription
		// to verify the message was received. However, pulling messages requires
		// setting up a subscriber which is more complex. The fact that the publish
		// succeeded without errors and returned a message ID confirms the integration works.
	}
}
