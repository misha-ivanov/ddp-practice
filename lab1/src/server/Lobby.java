package server;

import java.util.LinkedList;
import java.util.Queue;

public class Lobby implements  ClientConnectionHandler {
    private final Queue<ClientConnection> queue = new LinkedList<>();

    public synchronized void addConnection(ClientConnection connection) {
        queue.add(connection);
        System.out.println("Added to lobby: " + connection.getRemoteAddress() + " (position = " + queue.size() + ")");
        if(queue.size() >= 2) {
            ClientConnection connection1 = queue.poll();
            ClientConnection connection2 = queue.poll();
            System.out.println("Start Session for: " + connection1.getRemoteAddress() + " and " + connection2.getRemoteAddress());
            new Session(connection1, connection2);
        }
    }

    @Override
    public void handle(ClientConnection connection, String message) {
        System.out.println("Lobby has handled" + connection.getRemoteAddress());
    }

    @Override
    public synchronized void onDisconnection(ClientConnection connection) {
        queue.remove(connection);
        connection.close();

        System.out.println("Connection was removed from Lobby: "  + connection.getRemoteAddress());
    }
}
