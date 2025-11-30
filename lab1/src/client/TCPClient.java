package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {

    DataInputStream dis;
    DataOutputStream dos;

    private int serverPort;
    private String serverHost;

    public TCPClient(String _serverHost, int _serverPort) {
        serverHost = _serverHost;
        serverPort = _serverPort;
    }

    public void start() {
        try {
            Socket socket = new Socket(serverHost, serverPort);
            System.out.println("Connected to "+ serverHost + ":" + serverPort);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

        }
        catch (IOException e) {
            System.out.println("TCPClient: " + e.getMessage());
        }
    }

    public static void main (String []args) {
        TCPClient client = new TCPClient("localhost", 8080);
        client.start();
    }
}
