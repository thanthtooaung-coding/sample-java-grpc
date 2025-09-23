package com.sample.gateway.controller;

import com.sample.farewell.grpc.FarewellRequest;
import com.sample.farewell.grpc.FarewellServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FarewellController {

    @GrpcClient("greeting-service")
    private FarewellServiceGrpc.FarewellServiceBlockingStub farewellServiceBlockingStub;

    @GetMapping("/farewell")
    public String farewell(@RequestParam(name = "name", defaultValue = "World") String name) {
        FarewellRequest farewellRequest = FarewellRequest.newBuilder()
                .setName(name)
                .build();
        return farewellServiceBlockingStub.farewell(farewellRequest).getMessage();
    }
}
