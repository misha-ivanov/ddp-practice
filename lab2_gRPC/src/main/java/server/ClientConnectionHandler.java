package server;

import javax.annotation.Nullable;

public interface ClientConnectionHandler {
    void handle(ClientConnectionObject connection, @Nullable grpc.sessionservice.Attack message);
    void onDisconnection(ClientConnectionObject connection);
}
