package server;

import grpc.sessionservice.Attack;
import grpc.sessionservice.ServerMessage;
import grpc.sessionservice.SessionServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class SessionServiceImpl extends SessionServiceGrpc.SessionServiceImplBase {
    private final ClientConnectionHandler handler;

    public SessionServiceImpl(ClientConnectionHandler handler) {
        this.handler = handler;
    }

    @Override
    public StreamObserver<Attack> session(StreamObserver<ServerMessage> responseObserver) {
        String clientID = UUID.randomUUID().toString();
        ClientConnectionObject connectionObject = new ClientConnectionObject(responseObserver, clientID);

        connectionObject.setHandler(handler);
        handler.handle(connectionObject, null);

        return new StreamObserver<Attack>() {
            @Override
            public void onNext(Attack message) {
                connectionObject.getHandler().handle(connectionObject, message);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("SessionServiceImpl.session.onError: client: " + clientID + " Error: " + throwable.getMessage());
                handler.onDisconnection(connectionObject);
            }

            @Override
            public void onCompleted() {
                System.out.println("SessionServiceImpl.session.onCompleted: " + clientID);
                handler.onDisconnection(connectionObject);
            }
        };
    }
}
