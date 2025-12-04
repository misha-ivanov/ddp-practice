package server;

import grpc.sessionservice.Attack;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

public class Lobby implements  ClientConnectionHandler {
    private final Queue<ClientConnectionObject> queue = new LinkedList<>();

    @Override
    public synchronized void handle(ClientConnectionObject connection, @Nullable grpc.sessionservice.Attack message){
        queue.add(connection);
        System.out.println("Added to lobby: " + connection.getID() + " (position = " + queue.size() + ")");
        if(queue.size() >= 2) {
            ClientConnectionObject connection1 = queue.poll();
            ClientConnectionObject connection2 = queue.poll();
            System.out.println("Start Session for: " + connection1.getID() + " and " + connection2.getID());
            new Session(connection1, connection2);
        }
    }

    @Override
    public synchronized void onDisconnection(ClientConnectionObject connection) {
        queue.remove(connection);
        connection.close();

        System.out.println("Connection was removed from Lobby: "  + connection.getID());
    }
}
