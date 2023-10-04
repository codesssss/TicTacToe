package org.tic.pojo;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:34 pm
 */
public class GameSession {
    private String player1;
    private String player2;
    private String[][] board = new String[3][3];
    private String currentPlayer;

    public GameSession(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = Math.random() > 0.5 ? player1 : player2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    public boolean makeMove(int x, int y, String username) {
        if (board[x][y].isEmpty() && currentPlayer.equals(username)) {
            board[x][y] = currentPlayer.equals(player1) ? "X" : "O";
            switchPlayer();
            return true;
        }
        return false;
    }

    public String[][] getBoard() {
        return board;
    }

    public String getOtherPlayer(String username) {
        return username.equals(player1) ? player2 : player1;
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
    }

    public boolean isWinner(String username) {
        String symbol = (username.equals(player1)) ? "X" : "O";
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

}



