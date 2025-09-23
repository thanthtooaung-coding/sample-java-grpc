package com.sample.greeting.grpc;


import com.sample.farewell.grpc.FarewellRequest;
import com.sample.farewell.grpc.FarewellResponse;
import com.sample.farewell.grpc.FarewellServiceGrpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class FarewellServiceImpl extends FarewellServiceGrpc.FarewellServiceImplBase {

    @Override
    public void farewell(FarewellRequest request, StreamObserver<FarewellResponse> responseObserver) {
        FarewellResponse response = FarewellResponse.newBuilder()
                .setMessage("Goodbye, " + request.getName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
