package org.tic;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 7:05 pm
 */

import org.tic.pojo.Message;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.Random;

public class TicTacToeGUI {

    private JFrame frame;
    private JButton[][] boardButtons;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JPanel timerPanel;
    private JLabel timerValue;
    private JButton quitButton;
    private JLabel currentPlayerLabel;
    private TicTacToeClient ticTacToeClient;
    private boolean isMyTurn = false;
    private volatile static Timer turnTimer;
    private int turnTimeLeft = 20;
    private static final int DEFAULT_TURN_TIME = 20;
    private volatile static Timer crashTimer;
    private int crashTimeLeft = 30;
    private JOptionPane crashPane;
    private JDialog crashDialog;


    public TicTacToeGUI(TicTacToeClient ticTacToeClient) throws RemoteException {
        ticTacToeClient.setTicTacToeGUI(this);
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

        timerValue = new JLabel("20");
        timerValue.setFont(new Font("Arial", Font.BOLD, 24));
        timerValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        timerPanel.setPreferredSize(new Dimension(80, 100));
        timerPanel.setMaximumSize(new Dimension(80, 100));
        timerPanel.add(timerValue);
        leftPanel.add(timerPanel);

        leftPanel.add(Box.createVerticalGlue());

        turnTimer = new Timer(1000, e -> {
            turnTimeLeft--;
            timerValue.setText(String.valueOf(turnTimeLeft));
            if (turnTimeLeft <= 0) {
                executeRandomMove();
                turnTimer.stop();
            }
        });

        // Title
        JLabel titleLabel = new JLabel("Distributed Tic-Tac-Toe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleLabel);

        leftPanel.add(Box.createVerticalGlue());

        // Quit Button
        quitButton = new JButton("QUIT");
        quitButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to quit the game?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                try {
                    ticTacToeClient.getServer().quitGame(ticTacToeClient.getUsername());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    ticTacToeClient.handleRemoteException(ex);
                }
                System.exit(0);
            }
        });
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(quitButton);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to quit the game?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        ticTacToeClient.getServer().quitGame(ticTacToeClient.getUsername());
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                        ticTacToeClient.handleRemoteException(ex);
                    }
                    System.exit(0);
                }
            }
        });

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
                            turnTimer.stop();
                            isMyTurn = false;
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                            ticTacToeClient.handleRemoteException(ex);
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

        chatArea = new JTextArea(1, 20);
        chatArea.setEditable(false);

        JPanel chatPanel = new JPanel(new BorderLayout());

        chatPanel.add(chatArea, BorderLayout.PAGE_END);

        JScrollPane chatScroll = new JScrollPane(chatPanel);

        rightPanel.add(chatScroll, BorderLayout.CENTER);

        chatInput = new JTextField();
        chatInput.setText("Type your message here, max 30 chars");
        chatInput.setForeground(Color.GRAY);
        chatInput.setFont(new Font("Arial", Font.ITALIC, 12));

        chatInput.addActionListener(e -> {
            String message = chatInput.getText();
            if (!message.isEmpty()) {

                // Check the number of lines and remove the oldest message if necessary
                int numLines = chatArea.getLineCount();
                if (numLines > 10) {
                    try {
                        int end = chatArea.getLineEndOffset(0); // end offset of the first line
                        chatArea.replaceRange("", 0, end); // remove the first line
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
                if(ticTacToeClient.getRank()!=null){
                chatArea.append("Rank#"+ticTacToeClient.getRank()+" "+ticTacToeClient.getUsername()+": "+ message + "\n");}
                else
                // Add the new message
                {chatArea.append(ticTacToeClient.getUsername()+": "+ message + "\n");}
                chatInput.setText("");
                try {
                    ticTacToeClient.getServer().sendMessage(ticTacToeClient.getUsername(), message);
                } catch (RemoteException ex) {
                    ticTacToeClient.handleRemoteException(ex);
                }
            }
        });
        chatInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (chatInput.getText().length() >= 30) // limit text to 20 characters
                    e.consume();
            }
        });


        chatInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (chatInput.getText().equals("Type your message here, max 30 chars")) {
                    chatInput.setText("");
                    chatInput.setForeground(Color.BLACK);
                    chatInput.setForeground(Color.BLACK);
                    chatInput.setFont(new Font("Arial", Font.PLAIN, 12));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (chatInput.getText().isEmpty()) {
                    chatInput.setText("Type your message here, max 30 chars");
                    chatInput.setForeground(Color.GRAY);
                    chatInput.setFont(new Font("Arial", Font.ITALIC, 12));
                }
            }
        });
        rightPanel.add(chatInput, BorderLayout.SOUTH);

        centerPanel.add(rightPanel);

        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        ticTacToeClient.connectServer();
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
        if (numLines > 10) {
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


    public void changePlayerLabel(String rank, String name, String symbol) {
        currentPlayerLabel.setText("Rank#" + rank + " " + name + "'s turn. symbol: " + "(" + symbol + "）");
    }


    public void displayTurn() {
        isMyTurn = true;
        resetAndStartTurnTimer();
    }

    public void displayTurnWithTime(int time) {
        isMyTurn = true;
        resetAndStartTurnTimer(time);
    }

    public void resetAndStartTurnTimer() {
        turnTimeLeft = 20;
        timerValue.setText(String.valueOf(turnTimeLeft));
        turnTimer.restart();
    }

    public void resetAndStartTurnTimer(int time) {
        turnTimeLeft = time;
        timerValue.setText(String.valueOf(turnTimeLeft));
        turnTimer.restart();
    }

    private void executeRandomMove() {
        if (!isMyTurn) {
            return;
        }

        List<Point> availableMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardButtons[i][j].getText().isEmpty()) {
                    availableMoves.add(new Point(i, j));
                }
            }
        }

        if (!availableMoves.isEmpty()) {
            Random rand = new Random();
            Point randomMove = availableMoves.get(rand.nextInt(availableMoves.size()));
            boardButtons[randomMove.x][randomMove.y].setText(ticTacToeClient.getSymbol());
            try {
                ticTacToeClient.getServer().makeMove(randomMove.x, randomMove.y, ticTacToeClient.getUsername());
            } catch (RemoteException ex) {
                ex.printStackTrace();
                ticTacToeClient.handleRemoteException(ex);
            }
        }
        isMyTurn = false;
    }


    public void displayWinner(String winnerName) {
        turnTimer.stop();
        timerValue.setText("20");
        Object[] options = {"Find New Game", "Quit"};
        int n = JOptionPane.showOptionDialog(frame,
                winnerName + " wins the game!\nWhat would you like to do next?",
                "Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        switch (n) {
            case 0: // Find New Game
                clearBoard();
                clearChat();
                currentPlayerLabel.setText("Waiting for game..."); // Update the label
                try {
                    ticTacToeClient.getServer().joinQueue(ticTacToeClient.getUsername());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    ticTacToeClient.handleRemoteException(ex);
                }
                break;

            case 1: // Quit
                System.exit(0);
                break;
        }
    }

    public void displayDraw() {
        turnTimer.stop();
        timerValue.setText("20");
        Object[] options = {"Find New Game", "Quit"};
        int n = JOptionPane.showOptionDialog(frame,
                "The game is a draw!\nWhat would you like to do next?",
                "Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        switch (n) {
            case 0: // Find New Game
                clearBoard();
                clearChat();
                currentPlayerLabel.setText("Waiting for game..."); // Update the label
                try {
                    ticTacToeClient.getServer().joinQueue(ticTacToeClient.getUsername());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    ticTacToeClient.handleRemoteException(ex);
                }
                break;

            case 1: // Quit
                System.exit(0);
                break;
        }
    }

    public void displayQuit() {
        turnTimer.stop();
        timerValue.setText("20");
        Object[] options = {"Find New Game", "Quit"};
        int n = JOptionPane.showOptionDialog(frame,
                "The opponent quit the game! You win!\nWhat would you like to do next?",
                "Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        switch (n) {
            case 0: // Find New Game
                clearBoard();
                clearChat();
                currentPlayerLabel.setText("Waiting for game..."); // Update the label
                try {
                    ticTacToeClient.getServer().joinQueue(ticTacToeClient.getUsername());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    ticTacToeClient.handleRemoteException(ex);
                }
                break;

            case 1: // Quit
                System.exit(0);
                break;
        }
    }

    public synchronized void displayCrash() {
        // Initialize timer for crash countdown
        turnTimer.stop();
        try {
            ticTacToeClient.getServer().sendTime(ticTacToeClient.getUsername(), turnTimeLeft);
        } catch (RemoteException e) {
            ticTacToeClient.handleRemoteException(e);
        }

        if (crashTimer != null) {
            crashTimer.stop();
        }

        crashTimeLeft = 30;
        crashTimer = new Timer(1000, e -> {
            crashTimeLeft--;
            if (crashPane != null) {
                crashPane.setMessage("Opponent's client crashed. Waiting for reconnection... " + crashTimeLeft + " seconds left.");
            }
            if (crashTimeLeft <= 0) {
                crashTimer.stop();
                if (crashDialog != null) {
                    crashDialog.setVisible(false); // close the dialog
                }
                // Display draw and provide options
                displayDrawAfterCrash();
            }
        });

        // Show a dialog with countdown
        crashPane = new JOptionPane("Opponent's client crashed. Waiting for reconnection... " + crashTimeLeft + " seconds left.",
                JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        crashDialog = crashPane.createDialog(frame, "Connection Issue");
        crashDialog.setModal(false); // Allows users to interact with other parts of the application
        crashDialog.setVisible(true);

        crashTimer.start();
    }

    private void displayDrawAfterCrash() {
        Object[] options = {"Find New Game", "Quit"};
        int n = JOptionPane.showOptionDialog(frame,
                "The game is a draw due to a connection issue!\nWhat would you like to do next?",
                "Game Drawn",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        switch (n) {
            case 0: // Find New Game
                clearBoard();
                clearChat();
                currentPlayerLabel.setText("Waiting for game..."); // Update the label
                try {
                    ticTacToeClient.getServer().joinQueue(ticTacToeClient.getUsername());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    ticTacToeClient.handleRemoteException(ex);
                }
                break;
            case 1: // Quit
                System.exit(0);
                break;
        }
    }

    public void displayReconnected() {
        // Stop the crash timer and reset crash time
        if (crashTimer != null) {
            crashTimer.stop();
            crashTimeLeft = 30;
        }

        // Close the crash dialog if it's open
        if (crashDialog != null) {
            crashDialog.setVisible(false);
            crashDialog.dispose();
        }

        // Show a dialog informing that the opponent has reconnected
        // Create a JDialog
        JDialog dialog = new JDialog(frame, "Reconnection Successful", false);  // false makes it non-modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Create a JLabel to hold your message
        JLabel label = new JLabel("Opponent has successfully reconnected!");

        // Optionally create a JButton if you want the user to be able to close the dialog
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        // Create a JPanel to hold the label and button
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(button);

        // Add the panel to the dialog
        dialog.setContentPane(panel);

        // Pack the dialog to size it to fit the label and button
        dialog.pack();

        // Optionally set the location of the dialog
        dialog.setLocationRelativeTo(frame);

        // Show the dialog
        dialog.setVisible(true);

        // Start the timer
        turnTimer.start();
    }


    public void updateOpponentMove(int x, int y) {
        // Assuming that the opponent has the opposite symbol of the client
        String opponentSymbol = ticTacToeClient.getSymbol().equals("X") ? "O" : "X";
        boardButtons[x][y].setText(opponentSymbol);
        boardButtons[x][y].setBackground(Color.GRAY);
    }

    public void displayWaiting() {
        currentPlayerLabel.setText("Waiting for game...");
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

    private void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardButtons[i][j].setText("");
            }
        }
    }

    private void clearChat() {
        chatArea.setText("");
    }


    public void displayServerUnavailableMessage() {
        JDialog dialog = new JDialog(frame, "Server Unavailable", false);
        dialog.setLayout(new BorderLayout());
        JLabel label = new JLabel("<html>The server has crashed.<br>The program will exit in 5 seconds.</html>", JLabel.CENTER);
        dialog.add(label, BorderLayout.CENTER);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(frame);  // Center the dialog
        dialog.setVisible(true);

        // Create a timer to close the application after 5 seconds
        Timer exitTimer = new Timer(5000, e -> System.exit(0));

        // Start the timer
        exitTimer.setRepeats(false);
        exitTimer.start();
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public void notifyDuplicateUsername(String username) {
        JDialog dialog = new JDialog(frame, username+": "+"Username Already Used", false);
        dialog.setLayout(new BorderLayout());
        JLabel label = new JLabel("<html>This username is already in the game。<br>The program will exit in 5 seconds.</html>", JLabel.CENTER);
        dialog.add(label, BorderLayout.CENTER);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(frame);  // Center the dialog
        dialog.setVisible(true);

        // Create a timer to close the application after 5 seconds
        Timer exitTimer = new Timer(5000, e -> System.exit(0));

        // Start the timer
        exitTimer.setRepeats(false);
        exitTimer.start();
    }
}

