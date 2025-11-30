package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient implements ClientHandler {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private String serverHost;
    private int serverPort;

    private Thread readerThread;
    private volatile boolean running = false;

    private Client client;

    public TCPClient(String _serverHost, int _serverPort) {
        this.serverHost = _serverHost;
        this.serverPort = _serverPort;
    }

    public synchronized void connect() throws IOException{
        if (running) return;

        socket = new Socket(serverHost, serverPort);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        running = true;
        startReader();
    }

    public void onSend(String message) {
        try{
            dos.writeUTF(message);
            dos.flush();
        } catch (Exception e) {
            System.out.println("TCPClient.onSend: " + e.getMessage());
            onDisconnect();
        }
    }

    @Override
    public synchronized void setClient(Client client) {
        this.client = client;
    }

    public void onDisconnect() {
        running = false;
        client.onDisconnect();
    }

    private void startReader() {
        readerThread = new Thread(() -> {
            try {
                while (running) {
                    String message;
                    try {
                        message = dis.readUTF();
                    } catch (IOException e) {
                        System.out.println("TCPClient.startReader: " + e.getMessage());
                        onDisconnect();
                        break;
                    }
                    client.onMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onDisconnect();
            } finally {
                onDisconnect();
            }
        }, "TCPClient-Reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public boolean isConnected() {
        return (running && socket != null && socket.isConnected());
    }
}
