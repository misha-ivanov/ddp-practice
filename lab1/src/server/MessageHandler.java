package server;

public interface MessageHandler {
    void handle(ClientConnection connection, String message);
    void onDisconnection(ClientConnection connection);
}
