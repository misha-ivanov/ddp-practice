package client;

import grpc.sessionservice.Attack;
import grpc.sessionservice.ServerMessage;
import grpc.sessionservice.SessionServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class gRPCClient implements ClientHandler {
    private String serverHost;
    private int serverPort;
    private Client client;

    private ManagedChannel channel;
    private SessionServiceGrpc.SessionServiceStub stub;
    private StreamObserver<Attack> requestObserver;

    private volatile boolean running = false;

    gRPCClient(String host, int port) {
        serverHost = host;
        serverPort = port;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void connect() {
        if (running) {
            return;
        }

        channel = ManagedChannelBuilder.forAddress(serverHost, serverPort)
                .usePlaintext()
                .build();

        stub = SessionServiceGrpc.newStub(channel);

        StreamObserver<ServerMessage> responseObserver = new StreamObserver<ServerMessage>() {
            @Override
            public void onNext(ServerMessage serverMessage) {
                String response = parseServerMessageToString(serverMessage);

                client.onMessage(response);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("gRPC responseObserver.onError: " + throwable.getMessage());
                running = false;
                client.onDisconnect();
            }

            @Override
            public void onCompleted() {
                System.out.println("gRPC responseObserver.onCompleted: completed stream");
                running = false;
                client.onDisconnect();
            }
        };

        requestObserver = stub.session(responseObserver);
        running = true;
    }

    @Override
    public void onSend(int row, int col) {
        Attack attack = Attack.newBuilder()
                .setRow(row)
                .setCol(col)
                .build();
        try {
            requestObserver.onNext(attack);
        }
        catch (Exception e) {
            System.out.println("gRPCClient.onSend requestObserver.onSend: " + e.getMessage());
        }
    }

    @Override
    public void onDisconnect() {
        running = false;
        client.onDisconnect();
    }

    private String parseServerMessageToString(ServerMessage message) {
        switch (message.getBodyCase()) {
            case ROLE:
                return "ROLE " + message.getRole().getRole();
            case TURN:
                return "TURN " + message.getTurn().getTurn();
            case REJECT:
                return "REJECT";
            case UPDATE:
                grpc.sessionservice.Update u = message.getUpdate();
                return "UPDATE " + u.getRow() + " " + u.getCol() + " " + u.getState().name();
            case WIN:
                return "WIN " + message.getWin().getWin();
            case BODY_NOT_SET:
            default:
                return null;
        }
    }
}
