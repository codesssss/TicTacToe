package org.tic.pojo;

import org.tic.ENUM.PlayerStatus;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:34 pm
 */
public class GameSession {
    private Player player1;
    private Player player2;
    private String[][] board = new String[3][3];
    private List<Message> messages = new Vector<>();
    private Player currentPlayer;

    public GameSession(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        if (Math.random() > 0.5) {
            this.currentPlayer = player1;
            player1.setSymbol("X");
            player2.setSymbol("O");
            this.currentPlayer = player1;
        } else {
            this.currentPlayer = player2;
            player1.setSymbol("O");
            player2.setSymbol("X");
            this.currentPlayer = player2;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    public boolean makeMove(int x, int y, String username) throws RemoteException {
        if (board[x][y].isEmpty() && currentPlayer.getUsername().equals(username)) {

            board[x][y]=currentPlayer.getSymbol();

            // After move, check for win or draw
            if (isWinner(username)) {
                Player winner = getPlayer(username);
                Player loser = getOtherPlayer(username);

                winner.getClientCallback().notifyWinner(username);
                loser.getClientCallback().notifyWinner(username);
//                player1.setStatus(PlayerStatus.ACTIVE);
//                player2.setStatus(PlayerStatus.ACTIVE);

                // TODO: End the game session
            } else if (isDraw()) {
                player1.getClientCallback().notifyDraw();
                player2.getClientCallback().notifyDraw();

                // TODO: End the game session
            } else {
                getOtherPlayer(username).getClientCallback().updateOpponentMove(x, y);
                getOtherPlayer(username).getClientCallback().notifyTurn();
            }

            switchPlayer();
            return true;
        }
        // TODO: Handle error situations and inform the player
        return false;
    }

    public void sendMessage(String username, String message) throws RemoteException {
        if (player1.getUsername().equals(username)) {
            player2.getClientCallback().receiveChatMessage(new Message(username,message));
        } else {
            player1.getClientCallback().receiveChatMessage(new Message(username,message));
        }
    }

    public String[][] getBoard() {
        return board;
    }

    public Player getOtherPlayer(String username) {
        return username.equals(player1.getUsername()) ? player2 : player1;
    }

    public Player getPlayer(String username) {
        return username.equals(player1.getUsername()) ? player1 : player2;
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
    }

    public boolean isWinner(String username) {
        String symbol=getPlayer(username).getSymbol();
        // Check rows, columns and diagonals
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(symbol) && board[i][1].equals(symbol) && board[i][2].equals(symbol)) {
                return true;
            }
            if (board[0][i].equals(symbol) && board[1][i].equals(symbol) && board[2][i].equals(symbol)) {
                return true;
            }
        }
        if (board[0][0].equals(symbol) && board[1][1].equals(symbol) && board[2][2].equals(symbol)) {
            return true;
        }
        if (board[0][2].equals(symbol) && board[1][1].equals(symbol) && board[2][0].equals(symbol)) {
            return true;
        }
        return false;
    }


    public boolean isDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}



