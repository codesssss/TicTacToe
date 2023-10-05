package org.tic;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 7:05 pm
 */

import org.tic.pojo.Message;

import java.util.List;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TicTacToeGUI implements TicTacToeListener{

    private JFrame frame;
    private JButton[][] boardButtons;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JPanel timerPanel;
    private JButton quitButton;
    private JLabel currentPlayerLabel;
    private JDialog waitingDialog;
    private TicTacToeClient ticTacToeClient;
    private boolean isMyTurn = false;

    public TicTacToeGUI(TicTacToeClient ticTacToeClient) {
        ticTacToeClient.setListener(this);
        this.ticTacToeClient = ticTacToeClient;
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

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int finalI = i;
                int finalJ = j;
                boardButtons[i][j].addActionListener(e -> {
                    JButton clickedButton = (JButton) e.getSource();

                    if (!clickedButton.getText().isEmpty()) {
                        return;
                    }
                    if (isMyTurn) {
                        try {
                            boardButtons[finalI][finalJ].setText(ticTacToeClient.getSymbol().equals("X") ? "X" : "O");
                            ticTacToeClient.getServer().makeMove(finalI, finalJ, ticTacToeClient.getUsername());
                            isMyTurn = false;
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
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

        chatInput.addActionListener(e -> {
            String message = chatInput.getText();
            if (!message.isEmpty()) {
                // Send chat message to server (assuming the server has a method to handle chat)
                // e.g., client.sendMessageToServer(message);
                chatArea.append("You: " + message + "\n");
                chatInput.setText("");
                try {
                    ticTacToeClient.getServer().sendMessage(ticTacToeClient.getUsername(), message);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
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

    public void updateBoard(String[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setText(board[i][j]);
            }
        }
    }


    public void updateChat(Message message) {
        String formattedMessage;
        if (message.getRank() != null) {
            formattedMessage = "Rank#" + message.getRank() + " " + message.getUsername() + ": " + message.getMessage();
        } else {
            formattedMessage = message.getUsername() + ": " + message.getMessage();
        }

        // Check the number of lines and remove the oldest message if necessary
        int numLines = chatArea.getLineCount();
        if (numLines >= 10) {
            try {
                int end = chatArea.getLineEndOffset(0); // end offset of the first line
                chatArea.replaceRange("", 0, end); // remove the first line
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

        // Add the new message
        chatArea.append(formattedMessage + "\n");
    }


    public void displayDraw() {
        JOptionPane.showMessageDialog(frame, "The game ended in a draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWinnerDialog(String winnerName) {
        JDialog dialog = new JDialog(frame, "Game Over", true);
        dialog.setLayout(new BorderLayout());

        JLabel label = new JLabel(winnerName + " wins the game!");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));  // Optionally set a bold font
        dialog.add(label, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(frame);  // Center the dialog w.r.t. main frame
        dialog.setVisible(true);
    }

    public void displayWinner(String winnerName) {
        JOptionPane.showMessageDialog(frame, winnerName + " wins the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
//        showWinnerDialog(winnerName);
    }

    public void updateOpponentMove(int x, int y) {
        // Assuming that the opponent has the opposite symbol of the client
        String opponentSymbol = ticTacToeClient.getSymbol().equals("X") ? "O" : "X";
        boardButtons[x][y].setText(opponentSymbol);
    }

    public void updateMatchStarted(String opponentName, String yourSymbol) {
        currentPlayerLabel.setText("Match started! Opponent: " + opponentName + ". Your symbol: " + yourSymbol);
    }

    public void displayTurn() {
        isMyTurn = true;
        JOptionPane.showMessageDialog(frame, "It's your turn!", "Your Move", JOptionPane.INFORMATION_MESSAGE);
    }

    public void displayWaiting() {
        currentPlayerLabel.setText("Waiting for game...");
    }

    public void displayConnectionFailed() {
        isMyTurn = true;
        JOptionPane.showMessageDialog(frame, "Failed", "Your Move", JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateMessages(List<Message> messages) {
        chatArea.setText("");  // Clear the existing chat

        int start = Math.max(0, messages.size() - 10);  // Get the starting index to fetch the last 10 messages

        for (int i = start; i < messages.size(); i++) {
            Message msg = messages.get(i);
            String formattedMessage;
            if (msg.getRank() != null) {
                formattedMessage = "Rank#" + msg.getRank() + " " + msg.getUsername() + ": " + msg.getMessage();
            } else {
                formattedMessage = msg.getUsername() + ": " + msg.getMessage();
            }
            chatArea.append(formattedMessage + "\n");
        }
    }


    @Override
    public void onMatchStarted(String opponentName, String yourSymbol) {
        updateMatchStarted(opponentName,yourSymbol);
    }

    @Override
    public void onTurnNotified() {
        displayTurn();
    }

    @Override
    public void onOpponentMoved(int x, int y) {
        updateOpponentMove(x, y);
    }

    @Override
    public void onWinnerDeclared(String winnerName) {
        displayWinner(winnerName);
    }

    @Override
    public void onMatchDraw() {
        displayDraw();
    }

    @Override
    public void onChatMessageReceived(Message message) {
        updateChat(message);
    }

    @Override
    public void onBoardUpdated(String[][] board) {
        updateBoard(board);
    }

    @Override
    public void onMessagesUpdated(List<Message> messages) {
        updateMessages(messages);
    }

    @Override
    public void onDisplayWaiting(){
        displayWaiting();
    }
}

