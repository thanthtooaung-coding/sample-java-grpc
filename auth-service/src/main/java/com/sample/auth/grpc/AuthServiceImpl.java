package com.sample.auth.grpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.auth.dto.AuthEvent;
import com.sample.auth.model.User;
import com.sample.auth.service.AuthEventService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@GrpcService
public class AuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuthEventService authEventService;

    public AuthServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, AuthEventService authEventService) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.authEventService = authEventService;
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        User user = new User(request.getUsername(), request.getPassword());
        boolean loginSuccess = authEventService.loginUser(user);

        sendMessage("login", createAuthEvent(request.getUsername(), request.getPassword()));

        LoginResponse response;
        if (loginSuccess) {
            response = LoginResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .setMessage("Login successful.")
                    .build();
        } else {
            response = LoginResponse.newBuilder()
                    .setStatus("FAILURE")
                    .setMessage("Invalid username or password.")
                    .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        User user = new User(request.getUsername(), request.getPassword());
        boolean registerSuccess = authEventService.registerUser(user);

        // Send a simple "register" event to Kafka for async processing/auditing
        sendMessage("register", createAuthEvent(request.getUsername(), request.getPassword()));

        RegisterResponse response;
        if (registerSuccess) {
            response = RegisterResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .setMessage("User registered successfully.")
                    .build();
        } else {
            response = RegisterResponse.newBuilder()
                    .setStatus("FAILURE")
                    .setMessage("Username '" + request.getUsername() + "' already exists.")
                    .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private AuthEvent createAuthEvent(String username, String password) {
        return new AuthEvent(username, password);
    }

    private void sendMessage(String key, AuthEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("auth-topic", key, message);
        } catch (JsonProcessingException e) {
            logger.error("Could not serialize auth event for user {}", event.username(), e);
        }
    }
}
