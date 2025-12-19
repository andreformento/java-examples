package com.example.demo.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
class PubSubConfig {

	@Value("${gcp.project-id}")
	private String projectId;

	@Value("${gcp.pubsub.topic}")
	private String topicName;

	@Value("${gcp.pubsub.endpoint:}")
	private String endpoint;

	@Bean
	public TopicAdminClient topicAdminClient() throws IOException {
		if (endpoint != null && !endpoint.isBlank()) {
			ManagedChannel channel = ManagedChannelBuilder.forTarget(endpoint)
					.usePlaintext()
					.build();
			TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(
					GrpcTransportChannel.create(channel));
			CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
			return TopicAdminClient.create(
					TopicAdminSettings.newBuilder()
							.setTransportChannelProvider(channelProvider)
							.setCredentialsProvider(credentialsProvider)
							.build());
		}
		return TopicAdminClient.create();
	}

	@Bean
	public Publisher publisher() throws IOException {
		TopicName topic = TopicName.of(projectId, topicName);
		if (endpoint != null && !endpoint.isBlank()) {
			ManagedChannel channel = ManagedChannelBuilder.forTarget(endpoint)
					.usePlaintext()
					.build();
			TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(
					GrpcTransportChannel.create(channel));
			CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
			return Publisher.newBuilder(topic)
					.setChannelProvider(channelProvider)
					.setCredentialsProvider(credentialsProvider)
					.build();
		}
		return Publisher.newBuilder(topic).build();
	}

	@Bean
	public SubscriptionAdminClient subscriptionAdminClient() throws IOException {
		if (endpoint != null && !endpoint.isBlank()) {
			ManagedChannel channel = ManagedChannelBuilder.forTarget(endpoint)
					.usePlaintext()
					.build();
			TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(
					GrpcTransportChannel.create(channel));
			CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
			return SubscriptionAdminClient.create(
					SubscriptionAdminSettings.newBuilder()
							.setTransportChannelProvider(channelProvider)
							.setCredentialsProvider(credentialsProvider)
							.build());
		}
		return SubscriptionAdminClient.create();
	}

}
