package client;

import webservice.service.Direction;
import webservice.service.Server;
import webservice.service.ServerService;

import java.net.URL;
import java.util.Random;

public class Client {
    public static void main(String[] args) throws Exception{
        ServerService service = new ServerService(new URL("http://185.188.181.184:8080/CarServer?wsdl"));
        Server server = service.getServerPort();

        int carIndex = server.createCar();
        server.setCarName(carIndex, "Mike");

        Random rand = new Random();
        Direction direction = Direction.DOWN;

        while(true) {
            boolean result = server.moveCarTo(carIndex, direction);
            if (!result) {
                direction = Direction.values()[rand.nextInt(4)];
            }
        }
    }
}
