package server;

public interface ClientConnectionHandler {
    void handle(ClientConnection connection, String message);
    void onDisconnection(ClientConnection connection);
}
