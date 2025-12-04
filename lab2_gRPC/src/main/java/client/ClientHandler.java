package client;

public interface ClientHandler {
    void onSend(int row, int col);
    void setClient(Client client);
    void onDisconnect();
}
