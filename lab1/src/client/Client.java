package client;

import logic.CellState;
import server.NetworkMessage;
import ui.MainWindow;

public class Client {
    private final MainWindow ui;
    private final ClientHandler handler;

    private static final String host = "localhost";
    private static final int port = 8080;
    private static final int n = 4;

    private String role;
    private String turn;

    public static void main(String[] args) {
        TCPClient tcpClient = new TCPClient(host, port);
        MainWindow ui = new MainWindow(n);

        Client client = new Client(tcpClient, ui);

        tcpClient.setClient(client);
        ui.setClient(client);

        new Thread(() -> {
            try {
                tcpClient.connect();
            }
            catch (Exception e) {
                e.printStackTrace();
                client.onDisconnect();
            }
        }, "Connect-Thread").start();
    }


    Client(ClientHandler handler, MainWindow ui) {
        this.handler = handler;
        this.ui = ui;
    }

    public void sendAttack(int row, int col) {
        handler.onSend(NetworkMessage.ATTACK.name() + " " + row + " " + col);
    }

    public void onMessage(String message) {
        if(message == null) return;

        String[] parts = message.split("\\s+");
        if (parts.length == 0) return;
        javax.swing.SwingUtilities.invokeLater(() -> {
            switch (parts[0]) {
                case "ROLE":
                    role = parts[1];
                    ui.changeRole(role);
                    break;
                case "TURN":
                    turn = parts[1];
                    ui.changeTurn(turn);
                    break;
                case "UPDATE":
                    ui.setCell(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), CellState.valueOf(parts[3]));
                    break;
                case "WIN":
                    ui.changeWinner(parts[1]);
                    break;
                default:
                    break;
            }
        });
    }

    public void onDisconnect() {
        handler.onDisconnect();
    }
}
