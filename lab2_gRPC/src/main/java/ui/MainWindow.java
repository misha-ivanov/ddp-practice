package ui;

import client.Client;
import logic.CellState;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame{
    private final int n;

    private final JButton[][] field;
    private final JLabel roleLabel = new JLabel("Role: -");
    private final JLabel turnLabel = new JLabel("Turn: -");
    private final JLabel winnerLabel = new JLabel("Winner: -");

    Client client;

    public MainWindow(int n) {
        super("Virus War");
        this.n = n;
        field = new JButton[n][n];
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        // Labels
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.PAGE_AXIS));
        top.add(roleLabel);
        top.add(turnLabel);
        top.add(winnerLabel);

        add(top, BorderLayout.NORTH);

        // Field
        JPanel grid = new JPanel(new GridLayout(n, n, 1, 1));
        grid.setPreferredSize(new Dimension(400,400));
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                JButton button = new JButton();
                button.setFont(button.getFont().deriveFont(Font.BOLD, 18f));
                button.setBackground(Color.WHITE);
                final int rr = row, cc = col;
                button.addActionListener(ev -> onCellClick(rr, cc));
                field[row][col] = button;
                grid.add(button);
            }
        }

        // Start cells of X and O
        JButton button = field[0][0];
        button.setText("X");
        button.setBackground(Color.WHITE);

        button = field[n-1][n-1];
        button.setText("O");
        button.setBackground(Color.WHITE);

        add(grid, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onCellClick(int row, int col) {
        client.sendAttack(row, col);
    }

    public void setCell(int row, int col, CellState state) {
        JButton button = field[row][col];
        switch (state) {
            case X_LIVE:
                button.setText("X");
                button.setBackground(Color.WHITE);
                break;
            case X_DEAD:
                button.setText("X");
                button.setBackground(Color.GREEN);
                break;
            case O_LIVE:
                button.setText("O");
                button.setBackground(Color.WHITE);
                break;
            case O_DEAD:
                button.setText("O");
                button.setBackground(Color.RED);
                break;
            default:
                break;
        }
    }

    public void changeRole(String text){
        roleLabel.setText("Role: " + text);
    }

    public void changeTurn(String text){
        turnLabel.setText("Turn: " + text);
    }

    public void changeWinner(String text){
        winnerLabel.setText("Winner: " + text);
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
