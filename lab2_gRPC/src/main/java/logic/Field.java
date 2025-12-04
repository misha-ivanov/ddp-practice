package logic;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Field {

    final CellState[][] field;
    private boolean[][] checked;

    final int n;

    public Field(int n) {
        this.n = n;
        field = new CellState[n][n];
        for(int row = 0; row < n; row++) {
            for(int col = 0; col < n; col++) {
                field[row][col] = CellState.EMPTY;
            }
        }
        field[0][0] = CellState.X_LIVE;
        field[n-1][n-1] = CellState.O_LIVE;
        checked = new boolean[n][n];
    }

    public CellState getCellState(int row, int col) {
        return field[row][col];
    }

    public void setCellState(int row, int col, CellState state) {
        field[row][col] = state;
    }

    public boolean hasStateInNeighbourhood(int row, int col, CellState state) {
        for (int i = max(row-1, 0); i <= min(row + 1, n-1); i++) {
            for (int j = max(col - 1, 0); j <= min(col + 1, n - 1); j++) {
                if(i == row && j == col) {
                    continue;
                }
                if (field[i][j] == state) {
                    return true;
                }
            }
        }
        return false;
    }

    public int[] coordStateInNeighbourhood(int row, int col, CellState state) {
        for (int i = max(row-1, 0); i <= min(row + 1, n-1); i++) {
            for (int j = max(col - 1, 0); j <= min(col + 1, n - 1); j++) {
                if(i == row && j == col) {
                    continue;
                }
                if (field[i][j] == state) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public boolean hasActiveChainInNeighbourhood(int row, int col, CellState state, CellState aim){
        checked[row][col] = true;
        for (int i = max(row-1, 0); i <= min(row + 1, n-1); i++) {
            for (int j = max(col - 1, 0); j <= min(col + 1, n - 1); j++) {
                if(i == row && j == col) {
                    continue;
                }
                if (field[i][j] == aim) {
                    return true;
                }
                if (field[i][j] == state && !checked[i][j]) {
                    return hasActiveChainInNeighbourhood(i, j, state, aim);
                }
            }
        }
        return false;
    }

    public boolean allowPlace(char player, int row, int col) {
        checked = new boolean[n][n];
        if(player == 'X' &&
                (field[row][col] == CellState.EMPTY ||
                        field[row][col] == CellState.O_LIVE)) {
            // check if has neighbour X_LIVE
            if(hasStateInNeighbourhood(row, col, CellState.X_LIVE)){
                return true;
            }

            // check if has active chain of O_DEAD
            if(hasStateInNeighbourhood(row, col, CellState.O_DEAD)){
                int[] cell = coordStateInNeighbourhood(row, col, CellState.O_DEAD);
                return hasActiveChainInNeighbourhood(cell[0], cell[1], CellState.O_DEAD, CellState.X_LIVE);
            }
        }
        else if(player == 'O' &&
                (field[row][col] == CellState.EMPTY ||
                        field[row][col] == CellState.X_LIVE)) {
            // check if has neighbour O_LIVE
            if(hasStateInNeighbourhood(row, col, CellState.O_LIVE)){
                return true;
            }

            // check if has active chain of X_DEAD
            if(hasStateInNeighbourhood(row, col, CellState.X_DEAD)) {
                return hasActiveChainInNeighbourhood(row, col, CellState.X_DEAD, CellState.O_LIVE);
            }
        }
        return false;
    }
}
