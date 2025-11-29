package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private static final int serverPort = 8080;

    public static void main(String args[]) {
        Lobby lobby = new Lobby();
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientConnection connection = new ClientConnection(clientSocket, lobby);
                lobby.addConnection(connection);
            }
        }
        catch (IOException e) {
            System.out.println("Listen socket: " + e.getMessage());
        }
    }
}
