package com.example.solace.subscriber;

import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ApiSubscriber {

    private final JCSMPSession session;

    @Value("${solace.topic}")
    private String topicName;

    public ApiSubscriber(JCSMPSession session) {
        this.session = session;
    }

    @PostConstruct
    public void startListener() {
        try {
            session.connect();
            session.addSubscription(JCSMPFactory.onlyInstance().createTopic(topicName));

            // Retrieve the message consumer and set the listener
            XMLMessageConsumer consumer = session.getMessageConsumer(new XMLMessageListener() {
                public void onReceive(BytesXMLMessage msg) {
                    if (msg instanceof TextMessage) {
                        System.out.println("Received from Solace: " + ((TextMessage) msg).getText());
                    }
                }

                public void onException(JCSMPException e) {
                    System.err.println("Consumer error: " + e.getMessage());
                }
            });

            // Start the consumer
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}