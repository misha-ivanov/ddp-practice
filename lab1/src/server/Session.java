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

    private int aliveX;
    private int aliveO;

    public Session(ClientConnection _c1, ClientConnection _c2) {
        c1 = _c1;
        c2 = _c2;

        c1.setHandler(this);
        c2.setHandler(this);

        c1.send(NetworkMessage.ROLE.name() + " X");
        c2.send(NetworkMessage.ROLE.name() + " O");

        broadcast(NetworkMessage.TURN.name() + " " + currentPlayer);

        field = new Field(n);

        movesLeftX = 2;
        movesLeftO = 0;

        movesDoneX = 1;
        movesDoneO = 1;

        aliveX = 1;
        aliveO = 1;
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
            }
            else if (connection == c2) {
                requestPlayer = 'O';
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
            CellState state = field.getCellState(row, col);

            if(currentPlayer == 'X') {
                if(state == CellState.EMPTY){
                    field.setCellState(row, col, CellState.X_LIVE);
                    aliveX++;
                }
                else {
                    field.setCellState(row, col, CellState.O_DEAD);
                    aliveX++;
                    aliveO--;
                }
                movesLeftX--;
                movesDoneX++;
            }
            else {
                if(state == CellState.EMPTY){
                    field.setCellState(row, col, CellState.O_LIVE);
                    aliveO++;
                }
                else {
                    field.setCellState(row, col, CellState.X_DEAD);
                    aliveO++;
                    aliveX--;
                }
                movesLeftO--;
                movesDoneO++;
            }

            // send new updated
            broadcast(NetworkMessage.UPDATE.name() + " " + row + " " + col + " " + field.getCellState(row, col));

            // if someone wins
            String winner = isSomeoneWins();
            if (winner != null) {
                broadcast(NetworkMessage.WIN.name() + " " + winner);
            }

            // turn current player
            if (currentPlayer == 'X' && movesLeftX == 0) {
                currentPlayer = 'O';
                if(movesDoneO == 1) {
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

    public String isSomeoneWins() {
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
                if (field.getCellState(i, j) == CellState.O_DEAD &&
                        (field.hasStateInNeighbourhood(i, j, CellState.EMPTY) ||
                        field.hasStateInNeighbourhood(i, j, CellState.O_LIVE)) &&
                        field.hasActiveChainInNeighbourhood(i, j, CellState.O_DEAD, CellState.X_LIVE)) {
                    break Xbreaker;
                }

                if (field.getCellState(i, j) == CellState.X_LIVE || field.getCellState(i, j) == CellState.O_DEAD) {
                    cellsChecked++;
                }

                if (cellsChecked == aliveX)
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
                if (field.getCellState(i, j) == CellState.X_DEAD &&
                        (field.hasStateInNeighbourhood(i, j, CellState.EMPTY) ||
                                field.hasStateInNeighbourhood(i, j, CellState.X_LIVE)) &&
                        field.hasActiveChainInNeighbourhood(i, j, CellState.X_DEAD, CellState.O_LIVE)) {
                    break Obreaker;
                }

                if (field.getCellState(i, j) == CellState.O_LIVE || field.getCellState(i, j) == CellState.X_DEAD) {
                    cellsChecked++;
                }

                if (cellsChecked == aliveO)
                    return "X";
            }
        }
        return null;
    }
}
