package net.rmi.client;

import car.CarServer;
import net.command.SerializableCommand;
import net.rmi.RemoteCarServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

/**
 * @author : Alex
 * @created : 10.04.2021, суббота
 **/
public class Client {

    static final String host = "185.188.181.184";//"132.145.228.39";
    static final int port = 8080;
    static final String name = "RMICarServer";

    public static class CarClient extends Thread{
        public final String name;

        public final RemoteCarServer server;

        public CarClient(String name, RemoteCarServer server){
            this.name = name;
            this.server = server;
        }

        @Override
        public void run(){
            try {
                SerializableCommand command = new SerializableCommand(0, "CREATECAR", "");
                int carIndex = server.executeCommand(command);
                System.out.println("carIndex=" + carIndex);

                command = new SerializableCommand(carIndex, "SETNAME", name);
                server.executeCommand(command);

                boolean ret;
                Random rand = new Random();
                CarServer.Direction direction = CarServer.Direction.DOWN;

                while (true) {
                    command = new SerializableCommand(carIndex, direction.name(), "1");
                    ret = server.executeCommand(command);
                    if (!ret)
                        direction = CarServer.Direction.values()[rand.nextInt(4)];
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        //1. Connect to name server;
        //2. lookup by name
        //3. call methods of proxy-object

        Registry registry = LocateRegistry.getRegistry(host,port);
        RemoteCarServer server = (RemoteCarServer) registry.lookup(name);
        System.out.println("server="+server);

        new CarClient("M1", server).start();
        new CarClient("M2", server).start();
        new CarClient("M3", server).start();
        new CarClient("M4", server).start();
        new CarClient("M5", server).start();
    }

}
