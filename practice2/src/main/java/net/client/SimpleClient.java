package net.client;

import car.CarServer;
import net.command.SerializableCommand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class SimpleClient {
    static DatagramSocket socket;
    static String host = "localhost";

    public static void main(String[] args)throws Exception{
        socket = new DatagramSocket();
        SerializableCommand command = new SerializableCommand(0, "CREATECAR", "");

        int carIndex = executeCommand(command);
        System.out.println("carIndex = " + carIndex);

        command = new SerializableCommand(carIndex, "SETNAME", "Mike");
        boolean ret = executeCommand(command);
        System.out.println("ret = " + ret);

        CarServer.Direction direction = CarServer.Direction.DOWN;
        Random random = new Random();

        while(true){
            command = new SerializableCommand(carIndex, direction.name(), "1");
            boolean success = executeCommand(command);
            System.out.println("result = " + success);
            if(!success) direction = CarServer.Direction.values()[random.nextInt(4)];
        }
    }

    public static <T> T executeCommand(SerializableCommand command) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(command);
        oos.close();
        byte[] data = baos.toByteArray();

        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), 8080);
        socket.send(packet);

        DatagramPacket recv = new DatagramPacket(new byte[1000], 1000);
        socket.receive(recv);

        ByteArrayInputStream bais = new ByteArrayInputStream(recv.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object ret = ois.readObject();

        return (T) ret;
    }
}
