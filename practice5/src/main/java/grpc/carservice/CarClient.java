package grpc.carservice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Random;

public class CarClient {
    public static void main(String[] args){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("185.188.181.184", 8080).usePlaintext().build();
        CarServiceGrpc.CarServiceBlockingStub client = CarServiceGrpc.newBlockingStub(channel);

        AddNewCarRequest newCarRequest = AddNewCarRequest.newBuilder().setName("Mike").setColor("ORANGE").build();
        AddNewCarResponse newCarResponse = client.createCar(newCarRequest);

        int carIndex = newCarResponse.getCarIndex();

        Direction direction = Direction.DOWN;
        Random rand = new Random();

        while(true){
            MoveCarRequest moveCarRequest = MoveCarRequest.newBuilder()
                    .setCarIndex(carIndex)
                    .setDirection(direction)
                    .setCount(1)
                    .build();
            MoveCarResponse moveCarResponse = client.moveCar(moveCarRequest);
            System.out.println("moveCarResponse.getResult() = " + moveCarResponse.getResult());
            if(!moveCarResponse.getResult()){
                direction = Direction.values()[rand.nextInt(4)];
            }
        }

    }
}
