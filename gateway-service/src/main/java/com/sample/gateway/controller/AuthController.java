package com.sample.gateway.controller;

import com.sample.auth.grpc.AuthServiceGrpc;
import com.sample.auth.grpc.LoginRequest;
import com.sample.auth.grpc.RegisterRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GrpcClient("auth-service")
    private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

    @PostMapping("/login")
    public com.sample.gateway.dto.LoginResponse login(@RequestBody com.sample.gateway.dto.LoginRequest request) {
        LoginRequest loginRequest = LoginRequest.newBuilder()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .build();
        com.sample.auth.grpc.LoginResponse grpcResponse = authServiceBlockingStub.login(loginRequest);

        return new com.sample.gateway.dto.LoginResponse(
            grpcResponse.getStatus(), grpcResponse.getMessage()
        );
    }

    @PostMapping("/register")
    public String register(@RequestBody com.sample.gateway.dto.RegisterRequest request) {
        RegisterRequest registerRequest = RegisterRequest.newBuilder()
                .setUsername(request.getUsername())
                .setPassword(request.getPassword())
                .build();
        return authServiceBlockingStub.register(registerRequest).getMessage();
    }
}
