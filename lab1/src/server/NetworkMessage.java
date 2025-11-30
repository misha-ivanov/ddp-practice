package server;

public enum NetworkMessage {
    ROLE, // assign role (X or O)
    TURN, // assign current player (X or Y)
    ATTACK, // try change cell (i, j)
    REJECT, // if attack is failed (no params)
    UPDATE, // update state of cell(i, j)
    WIN, // assign winner (X or Y)
}
