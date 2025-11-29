package server;

import logic.CellState;
import logic.Field;

public class Session implements MessageHandler{
    // c1 plays by 'X', c2 by 'O'
    private final ClientConnection c1;
    private final ClientConnection c2;
    private final Field field;

    private volatile char currentPlayer = 'X';
    private volatile boolean running = true;

    private int movesLeftX;
    private int movesLeftO;

    public Session(ClientConnection _c1, ClientConnection _c2) {
        c1 = _c1;
        c2 = _c2;

        c1.setHandler(this);
        c2.setHandler(this);

        c1.send("X");
        c2.send("O");

        field = new Field(4);
    }

    @Override
    public void handle(ClientConnection connection, String message) {
        if(!running)
            return;

        if(message == null) return;

        String[] parts = message.split("\\s+");
        if (parts.length == 0) return;

        String cmd = parts[0].toUpperCase();

        if(cmd.equals(NetworkMessage.ATTACK.name())){
            // whom request
            char requestPlayer;
            if(connection == c1) {
                requestPlayer = 'X';
            }
            else if (connection == c2) {
                requestPlayer = 'Y';
            }
            else return;

            if(requestPlayer != currentPlayer) {
                connection.send(NetworkMessage.REJECT.name());
                return;
            }

            // which cell
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[1]);

            // can't place
            if(!field.allowPlace(currentPlayer, row, col)){
                connection.send(NetworkMessage.REJECT.name());
                return;
            }

            // place new cell
            CellState state = field.getCellState(row, col);
            if(currentPlayer == 'X') {
                if(state == CellState.EMPTY){
                    field.setCellState(row, col, CellState.X_LIVE);
                }
                else {
                    field.setCellState(row, col, CellState.O_DEAD);
                }
                movesLeftX--;
            }
            else {
                if(state == CellState.EMPTY){
                    field.setCellState(row, col, CellState.O_LIVE);
                }
                else {
                    field.setCellState(row, col, CellState.X_DEAD);
                }
                movesLeftO--;
            }

            // send new updated
            broadcast(NetworkMessage.UPDATE.name() + " " + row + " " + col + " " + field.getCellState(row, col));

            if (currentPlayer == 'X' && movesLeftX == 0) {
                broadcast(NetworkMessage.TURN.name() + " O");
            }
            else if (currentPlayer == 'O' && movesLeftO == 0) {
                broadcast(NetworkMessage.TURN.name() + " X");
            }
        }

    }

    @Override
    public synchronized void onDisconnection(ClientConnection connection) {
        if (!running) return;
        running = false;

        System.out.println("GameSession ended due to disconnection");

        c1.close();
        c2.close();
    }

    public void broadcast(String msg) {
        c1.send(msg);
        c2.send(msg);
    }
}
