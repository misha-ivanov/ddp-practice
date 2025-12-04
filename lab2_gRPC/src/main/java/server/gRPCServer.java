package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class gRPCServer {
    private static final int serverPort = 8080;

    public static void main(String[] args) throws Exception {
        Lobby lobby = new Lobby();
        Server server = ServerBuilder.forPort(serverPort)
                .addService(new SessionServiceImpl(lobby))
                .build()
                .start();

        System.out.println("gRPCServer.main: Started on port " + serverPort);

        server.awaitTermination();
    }
}
