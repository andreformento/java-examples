package com.example.demo;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PubSubTestSetup {

	private final TopicAdminClient topicAdminClient;
	private final SubscriptionAdminClient subscriptionAdminClient;
	private final String projectId;
	private final String topicName;
	private final String subscriptionName;

	public PubSubTestSetup(
			TopicAdminClient topicAdminClient,
			SubscriptionAdminClient subscriptionAdminClient,
			@Value("${gcp.project-id}") String projectId,
			@Value("${gcp.pubsub.topic}") String topicName,
			@Value("${gcp.pubsub.subscription}") String subscriptionName) {
		this.topicAdminClient = topicAdminClient;
		this.subscriptionAdminClient = subscriptionAdminClient;
		this.projectId = projectId;
		this.topicName = topicName;
		this.subscriptionName = subscriptionName;
	}

	public void setupTopicAndSubscription() throws IOException {
		TopicName topicNameObj = TopicName.of(projectId, topicName);
		SubscriptionName subscriptionNameObj = SubscriptionName.of(projectId, subscriptionName);

		// Create topic if it doesn't exist
		try {
			topicAdminClient.getTopic(topicNameObj);
		} catch (Exception e) {
			// Topic doesn't exist, create it
			topicAdminClient.createTopic(topicNameObj);
		}

		// Create subscription if it doesn't exist
		try {
			subscriptionAdminClient.getSubscription(subscriptionNameObj);
		} catch (Exception e) {
			// Subscription doesn't exist, create it
			Subscription subscription = Subscription.newBuilder()
					.setName(subscriptionNameObj.toString())
					.setTopic(topicNameObj.toString())
					.build();
			subscriptionAdminClient.createSubscription(subscription);
		}
	}
}
