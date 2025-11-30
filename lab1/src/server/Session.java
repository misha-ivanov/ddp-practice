package server;

import logic.CellState;
import logic.Field;

public class Session implements ClientConnectionHandler{
    // c1 plays by 'X', c2 by 'O'
    private final ClientConnection c1;
    private final ClientConnection c2;
    private final Field field;

    private final int n = 4;

    private volatile char currentPlayer = 'X';
    private volatile boolean running = true;

    private int movesLeftX;
    private int movesLeftO;

    private int movesDoneX;
    private int movesDoneO;

    public Session(ClientConnection _c1, ClientConnection _c2) {
        c1 = _c1;
        c2 = _c2;

        c1.setHandler(this);
        c2.setHandler(this);

        c1.send(NetworkMessage.ROLE.name() + " X");
        c2.send(NetworkMessage.ROLE.name() + " O");

        field = new Field(n);

        movesLeftX = 2;
        movesLeftO = 0;

        movesDoneX = 0;
        movesDoneO = 0;
    }

    @Override
    public synchronized void handle(ClientConnection connection, String message) {
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
                System.out.println("requestPlayer = 'X'");
            }
            else if (connection == c2) {
                requestPlayer = 'O';
                System.out.println("requestPlayer = 'X'");
            }
            else return;

            if(requestPlayer != currentPlayer) {
                connection.send(NetworkMessage.REJECT.name());
                return;
            }

            // which cell
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);

            // if can't place
            if(!field.allowPlace(currentPlayer, row, col)){
                connection.send(NetworkMessage.REJECT.name());
                return;
            }

            // place new cell
            System.out.println("Before new cell");
            CellState state = field.getCellState(row, col);
            System.out.println(field.getCellState(row, col).name());
            System.out.println(currentPlayer);

            if(currentPlayer == 'X') {
                if(state == CellState.EMPTY){
                    System.out.println("EMPTY ATTACK");

                    field.setCellState(row, col, CellState.X_LIVE);
                }
                else {
                    field.setCellState(row, col, CellState.O_DEAD);
                }
                movesLeftX--;
                movesDoneX++;
            }
            else {
                if(state == CellState.EMPTY){
                    field.setCellState(row, col, CellState.O_LIVE);
                }
                else {
                    field.setCellState(row, col, CellState.X_DEAD);
                }
                movesLeftO--;
                movesDoneO++;
            }

            // send new updated
            broadcast(NetworkMessage.UPDATE.name() + " " + row + " " + col + " " + field.getCellState(row, col));

            // if someone wins
            String winner = isSomeoneWins(movesDoneX, movesDoneO);
            if (winner != null) {
                broadcast(NetworkMessage.WIN.name() + " " + winner);
            }

            // turn current player
            if (currentPlayer == 'X' && movesLeftX == 0) {
                currentPlayer = 'O';
                if(movesDoneO == 0) {
                    movesLeftO = 2;
                }
                else {
                    movesLeftO = 3;
                }
            }
            else if (currentPlayer == 'O' && movesLeftO == 0) {
                currentPlayer = 'X';
                movesLeftX = 3;
            }
            broadcast(NetworkMessage.TURN.name() + " " + currentPlayer);
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

    public String isSomeoneWins(int movesDoneX, int movesDoneO) {
        //check if X wins
        int cellsChecked = 0;
        Xbreaker:
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                // if X can attack from X_LIVE
                if (field.getCellState(i, j) == CellState.X_LIVE &&
                        (field.hasStateInNeighbourhood(i, j, CellState.EMPTY) ||
                        field.hasStateInNeighbourhood(i, j, CellState.O_LIVE))) {
                    break Xbreaker;
                }
                // if X can attack from O_DEAD
                else if (field.getCellState(i, j) == CellState.O_DEAD &&
                        (field.hasStateInNeighbourhood(i, j, CellState.EMPTY) ||
                        field.hasStateInNeighbourhood(i, j, CellState.O_LIVE)) &&
                        field.hasActiveChainInNeighbourhood(i, j, CellState.O_DEAD, CellState.X_LIVE)) {
                    break Xbreaker;
                }
                else {
                    cellsChecked++;
                }
                if (cellsChecked == movesDoneX)
                    return "O";
            }
        }

        //check if O wins
        cellsChecked = 0;
        Obreaker:
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                // if O can attack from O_LIVE
                if (field.getCellState(i, j) == CellState.O_LIVE &&
                        (field.hasStateInNeighbourhood(i, j, CellState.EMPTY) ||
                                field.hasStateInNeighbourhood(i, j, CellState.X_LIVE))) {
                    break Obreaker;
                }
                // if O can attack from X_DEAD
                else if (field.getCellState(i, j) == CellState.X_DEAD &&
                        (field.hasStateInNeighbourhood(i, j, CellState.EMPTY) ||
                                field.hasStateInNeighbourhood(i, j, CellState.X_LIVE)) &&
                        field.hasActiveChainInNeighbourhood(i, j, CellState.X_DEAD, CellState.O_LIVE)) {
                    break Obreaker;
                }
                else {
                    cellsChecked++;
                }
                if (cellsChecked == movesDoneO)
                    return "X";
            }
        }
        return null;
    }
}
