package server;

import grpc.sessionservice.ServerMessage;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnectionObject implements ClientConnection{
    private final StreamObserver<ServerMessage> responseObserver;
    private final String id;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private ClientConnectionHandler handler;

    public ClientConnectionObject(StreamObserver<ServerMessage> responseObserver, String id) {
        this.responseObserver = responseObserver;
        this.id = id;
    }

    @Override
    public void send(ServerMessage msg) {
        if(closed.get()) return;

        try {
            responseObserver.onNext(msg);
        } catch (Exception e) {
            close();
        }
    }

    @Override
    public void close() {
        if(!closed.get()) {
            closed.set(true);
            responseObserver.onCompleted();
        }
    }

    @Override
    public String getID() {
        return id;
    }

    public void setHandler(ClientConnectionHandler handler) {
        this.handler = handler;
    }

    public ClientConnectionHandler getHandler() {
        return handler;
    }
}
