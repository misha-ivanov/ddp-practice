package client;

public interface ClientHandler {
    void onSend(String message);
    void setClient(Client client);
    void onDisconnect();
}
