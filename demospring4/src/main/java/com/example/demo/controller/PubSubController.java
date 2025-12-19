package com.example.demo.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/pubsub")
public class PubSubController {

	private final Publisher publisher;

	PubSubController(Publisher publisher) {
		this.publisher = publisher;
	}

	@PostMapping("/publish")
	public ResponseEntity<PublishResponse> publish(@RequestBody PublishRequest request) {
		try {
			PubsubMessage message = PubsubMessage.newBuilder()
					.setData(ByteString.copyFromUtf8(request.message()))
					.build();

			ApiFuture<String> messageIdFuture = publisher.publish(message);
			String messageId = messageIdFuture.get();

			return ResponseEntity.ok(new PublishResponse(messageId, "Message published successfully"));
		} catch (InterruptedException e) {
			return ResponseEntity.internalServerError()
					.body(new PublishResponse(null, "Failed to publish message: " + e.getMessage()));
		} catch (ExecutionException e) {
			return ResponseEntity.internalServerError()
					.body(new PublishResponse(null, "Failed to publish message: " + e.getMessage()));
		}
	}

	public record PublishRequest(String message) {
	}

	public record PublishResponse(String messageId, String status) {
	}
}
