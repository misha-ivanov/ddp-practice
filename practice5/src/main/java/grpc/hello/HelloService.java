package grpc.hello;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

public class HelloService extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void hello(grpc.hello.HelloRequest request,
                      io.grpc.stub.StreamObserver<grpc.hello.HelloResponce> responseObserver) {

        String parameter = request.getName();
        System.out.println("parameter = " + parameter);
        HelloResponce responce = HelloResponce.newBuilder().setName("Hello," + parameter).build();
        responseObserver.onNext(responce);
        responseObserver.onCompleted();
    }

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(8080).addService(new HelloService()).build();
        server.start();
        server.awaitTermination();
    }
}



