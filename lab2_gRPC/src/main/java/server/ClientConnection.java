package server;

import grpc.sessionservice.ServerMessage;

public interface ClientConnection {
    void send(ServerMessage msg);

    void close();

    String getID();
}
