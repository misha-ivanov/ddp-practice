package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket clientSocket;
    public volatile boolean running = true;
    private ClientConnectionHandler handler;

    public ClientConnection(Socket _clientSocket, ClientConnectionHandler _handler) {
        try {
            clientSocket = _clientSocket;
            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());
            handler = _handler;
            this.start();
        }
        catch (IOException e){
            System.out.println("ClientConnection: " + e.getMessage());
        }
    }

    public void run () {
        try {
            while (running && !clientSocket.isClosed()){
                String message = dis.readUTF();
                System.out.println("From: " + clientSocket.getRemoteSocketAddress() + ": " + message);
                ClientConnectionHandler h = handler;
                h.handle(this, message);
            }
            handler.onDisconnection(this);
            //clientSocket.close();
        }
        catch(IOException e) {
            System.out.println("ClientConnection.run: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setHandler(ClientConnectionHandler _handler) {
        this.handler = _handler;
    }

    public void send (String message) {
        try {
            dos.writeUTF(message);
        }
        catch (IOException e) {
            System.out.println("ClientConnection.send: " + e.getMessage());
        }
    }

    public void close(){
        running = false;
        try {
            clientSocket.close();
        }
        catch (IOException e) {
            System.out.println("ClientConnection.close: " + e.getMessage());
        }
    }

    public String getRemoteAddress() {
        return clientSocket.getRemoteSocketAddress().toString();
    }

}
