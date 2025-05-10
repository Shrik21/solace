package com.example.solace.service;

import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ApiPublisherService {

    private final JCSMPSession session;

    @Value("${solace.topic}")
    private String topicName;

    public ApiPublisherService(JCSMPSession session) {
        this.session = session;
    }

    @PostConstruct
    public void fetchAndPublish() {
        try {
            session.connect();
            Topic topic = JCSMPFactory.onlyInstance().createTopic(topicName);
            XMLMessageProducer producer = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
                @Override
                public void responseReceived(String messageID) {
                    System.out.println("Publish succeeded for message ID: " + messageID);
                }

                @Override
                public void handleError(String messageID, JCSMPException exception, long timestamp) {
                    System.err.println("Publish failed for message ID: " + messageID +
                            ", Error: " + (exception != null ? exception.getMessage() : "Unknown error"));
                }
            });
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            TextMessage message = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
            message.setText(json);
            producer.send(message, topic);
            System.out.println("Published message to topic.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}