package org.tic;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 7:05 pm
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TicTacToeGUI {

    private JFrame frame;
    private JButton[][] boardButtons;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JPanel timerPanel;
    private JButton quitButton;
    private JLabel currentPlayerLabel;

    private TicTacToeClient ticTacToeClient;

    public TicTacToeGUI(String username, String serverIP, int serverPort) throws NotBoundException, RemoteException {
        this.ticTacToeClient = new TicTacToeClient(username, serverIP, serverPort);

        frame = new JFrame("Distributed Tic-Tac-Toe");
        frame.setSize(700, 450);  // Adjusted frame size for better appearance
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Left side
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Timer in a square box
        timerPanel = new JPanel();
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));
        JLabel timerText = new JLabel("Timer");
        timerText.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerPanel.add(timerText);

        JLabel timerValue = new JLabel("17");
        timerValue.setFont(new Font("Arial", Font.BOLD, 24));
        timerValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        timerPanel.setPreferredSize(new Dimension(80, 100));
        timerPanel.setMaximumSize(new Dimension(80, 100));
        timerPanel.add(timerValue);
        leftPanel.add(timerPanel);

        leftPanel.add(Box.createVerticalGlue());

        // Title
        JLabel titleLabel = new JLabel("Distributed Tic-Tac-Toe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleLabel);

        leftPanel.add(Box.createVerticalGlue());

        // Quit Button
        quitButton = new JButton("QUIT");
        quitButton.addActionListener(e -> System.exit(0));
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(quitButton);

        frame.add(leftPanel, BorderLayout.WEST);

        // Tic-Tac-Toe Board & Chat
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        JPanel boardContainer = new JPanel(new BorderLayout());
        currentPlayerLabel = new JLabel("Rank#50 Raj's turn (X)");
        currentPlayerLabel.setHorizontalAlignment(JLabel.CENTER);
        boardContainer.add(currentPlayerLabel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardButtons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j] = new JButton("") {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(60, 60); // Adjusted to maintain square shape
                    }
                };
                boardPanel.add(boardButtons[i][j]);
            }
        }
        boardContainer.add(boardPanel, BorderLayout.CENTER);
        centerPanel.add(boardContainer);

        // Chat
        JPanel rightPanel = new JPanel(new BorderLayout());

        JLabel chatLabel = new JLabel("Player Chat");
        rightPanel.add(chatLabel, BorderLayout.NORTH);

        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        chatInput = new JTextField();
        chatInput.setText("Type your message here");
        chatInput.setForeground(Color.GRAY);
        chatInput.setFont(new Font("Arial", Font.ITALIC, 12));
        chatInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (chatInput.getText().equals("Type your message here")) {
                    chatInput.setText("");
                    chatInput.setForeground(Color.BLACK);
                    chatInput.setFont(new Font("Arial", Font.PLAIN, 12));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (chatInput.getText().isEmpty()) {
                    chatInput.setText("Type your message here");
                    chatInput.setForeground(Color.GRAY);
                    chatInput.setFont(new Font("Arial", Font.ITALIC, 12));
                }
            }
        });
        rightPanel.add(chatInput, BorderLayout.SOUTH);

        centerPanel.add(rightPanel);

        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Client <username> <server_ip> <server_port>");
            return;
        }

        String username = args[0];
        String serverIP = args[1];
        int serverPort = Integer.parseInt(args[2]);

        try {
            TicTacToeGUI ticTacToeGUI = new TicTacToeGUI(username, serverIP, serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

