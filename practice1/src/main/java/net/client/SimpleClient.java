package net.client;

import car.CarServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

public class SimpleClient {
    static DataInputStream dis;

    static DataOutputStream dos;

    public static void main(String[] args) throws Exception{
        String address = "185.188.181.184"; //185.188.181.184
        Socket socket = new Socket(address, 8080);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        boolean result;
        result = executeCommand("SETNAME", "Mike");
        //System.out.println("result = " + result);

        CarServer.Direction direction = CarServer.Direction.DOWN;
        Random random = new Random();

        while(true){
            boolean success = executeCommand(direction.name(), "1");
            if(!success) direction = CarServer.Direction.values()[random.nextInt(4)];
        }

        //System.out.println("result = " + result);
    }
    public static boolean executeCommand(String command, String parameter) throws Exception{
        dos.writeUTF(command);
        dos.writeUTF(parameter);
        boolean result = dis.readBoolean();
        System.out.println("result = " + result);
        return result;
    }
}
