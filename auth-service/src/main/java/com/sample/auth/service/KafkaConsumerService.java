package com.sample.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.auth.dto.AuthEvent;
import com.sample.auth.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final AuthEventService authEventService;
    private final ObjectMapper objectMapper;


    public KafkaConsumerService(AuthEventService authEventService, ObjectMapper objectMapper) {
        this.authEventService = authEventService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "auth-topic", groupId = "auth-group")
    public void listenAuthTopic(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            AuthEvent event = objectMapper.readValue(message, AuthEvent.class);
            User user = new User(event.username(), event.password());

            if ("login".equals(key)) {
                authEventService.processLoginEvent(user);
            } else if ("register".equals(key)) {
                authEventService.processRegisterEvent(user);
            }
        } catch (JsonProcessingException e) {
            logger.error("Could not deserialize message: {}", message, e);
        }
    }
}
