package grpc.hello;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HelloClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("185.188.181.184", 8080).usePlaintext().build();
        HelloServiceGrpc.HelloServiceBlockingStub client = HelloServiceGrpc.newBlockingStub(channel);

        HelloRequest request = HelloRequest.newBuilder().setName("Mike").build();
        HelloResponce hello = client.hello(request);
        System.out.println("hello = " + hello.getName());
    }
}
