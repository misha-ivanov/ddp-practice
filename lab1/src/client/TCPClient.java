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
    private String message;
    private String response;
    private boolean isFirstPlayer;
    private String status;

    public TCPClient(String _serverHost, int _serverPort, String _message) {
        serverHost = _serverHost;
        serverPort = _serverPort;
        message = _message;
    }

    public void start() {
        try {
            Socket socket = new Socket(serverHost, serverPort);
            System.out.println("Connected to "+ serverHost + ":" + serverPort);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            response = dis.readUTF();
            System.out.println("Server response: " + response);

            // identify is first or second player
            if(response.equals("X")){
                isFirstPlayer = true;
            }
            else if(response.equals("O")) {
                isFirstPlayer = false;
            }
            status = "Act X";

            while (true) {

            }
        }
        catch (IOException e) {
            System.out.println("TCPClient: " + e.getMessage());
        }
    }

    public static void main (String []args) {
        TCPClient client = new TCPClient("localhost", 8080, "Hello from TCPClient!");
        client.start();
    }
}
