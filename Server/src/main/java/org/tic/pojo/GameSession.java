package org.tic.pojo;

import org.tic.ENUM.GameStatus;
import org.tic.LeaderboardManager;

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
        } else {
            this.currentPlayer = player2;
            player1.setSymbol("O");
            player2.setSymbol("X");
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
        player1.setRank(LeaderboardManager.getRank(player1.getUsername()));
        player2.setRank(LeaderboardManager.getRank(player2.getUsername()));
    }

    public GameStatus makeMove(int x, int y, String username) throws RemoteException {
        if (board[x][y].isEmpty() && currentPlayer.getUsername().equals(username)) {

            board[x][y] = currentPlayer.getSymbol();

            // After move, check for win or draw
            if (isWinner(username)) {
                Player winner = getPlayer(username);
                Player loser = getOtherPlayer(username);
                updatePlayerAfterMove(username, x, y);
                winner.getClientCallback().notifyWinner(username);
                loser.getClientCallback().notifyWinner(username);
                LeaderboardManager.updateScore(winner, 5);
                LeaderboardManager.updateScore(loser, -5);

                winner.setRank(LeaderboardManager.getRank(winner.getUsername()));
                loser.setRank(LeaderboardManager.getRank(loser.getUsername()));
                return GameStatus.FINISHED;
            } else if (isDraw()) {
                player1.getClientCallback().notifyDraw();
                player2.getClientCallback().notifyDraw();
                LeaderboardManager.updateScore(player1, 2);
                LeaderboardManager.updateScore(player2, 2);
                player1.setRank(LeaderboardManager.getRank(player1.getUsername()));
                player2.setRank(LeaderboardManager.getRank(player2.getUsername()));
                return GameStatus.FINISHED;
            } else {
                updatePlayerAfterMove(username, x, y);
            }
            return GameStatus.IN_GAME;
        }
        return GameStatus.ERROR;
    }

    public void sendMessage(String username, String message) throws RemoteException {
        if (player1.getUsername().equals(username)) {
            username = "Rank#" + getPlayer(username).getRank() + " " + username;
            Message mes=new Message(username, message);
            messages.add(mes);
            player2.getClientCallback().receiveChatMessage(mes);
        } else {
            username = "Rank#" + getPlayer(username).getRank() + " " + username;
            Message mes=new Message(username, message);
            messages.add(mes);
            player1.getClientCallback().receiveChatMessage(mes);
        }
    }

    public void startGame() throws RemoteException {
        currentPlayer.getClientCallback().notifyTurn();
        Player otherPlayer = getOtherPlayer(currentPlayer.getUsername());
        int rank = currentPlayer.getRank();
        otherPlayer.getClientCallback().resetPlayerLabelAndTime(String.valueOf(rank), currentPlayer.getUsername(), currentPlayer.getSymbol());
    }

    public void updatePlayerAfterMove(String username, int x, int y) throws RemoteException {
        getOtherPlayer(username).getClientCallback().updateOpponentMove(x, y);
        switchPlayer();
        Player currentPlayer = getCurrentPlayer();
        Player otherPlayer = getOtherPlayer(currentPlayer.getUsername());
        int rank = LeaderboardManager.getRank(currentPlayer.getUsername());
        currentPlayer.getClientCallback().notifyTurn();
        currentPlayer.getClientCallback().changeLabel(currentPlayer.getRank(), currentPlayer.getUsername(), currentPlayer.getSymbol());
        otherPlayer.getClientCallback().resetPlayerLabelAndTime(String.valueOf(rank), currentPlayer.getUsername(), currentPlayer.getSymbol());
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
        String symbol = getPlayer(username).getSymbol();
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



