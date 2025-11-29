package server;

import logic.Field;

public class Session implements MessageHandler{
    // c1 plays by 'X', c2 by 'O'
    private final ClientConnection c1;
    private final ClientConnection c2;

    public Session(ClientConnection _c1, ClientConnection _c2) {
        c1 = _c1;
        c2 = _c2;

        c1.setHandler(this);
        c2.setHandler(this);

        c1.send("X");
        c2.send("O");

        Field field = new Field(4);
    }

    @Override
    public void handle(ClientConnection connection, String message) {
        System.out.println("GameSession got from " + connection.getRemoteAddress() + " : " + message);
    }

    @Override
    public void onDisconnection(ClientConnection connection) {
        System.out.println("GameSession ended due to disconnection");
        c1.close();
        c2.close();
    }
}
