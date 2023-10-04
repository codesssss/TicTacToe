package org.tic.service;

import java.net.Socket;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:31 pm
 */
public class TicTacToeService {
    public String generatePlayerId(Socket playerSocket) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String playerAddress = playerSocket.getInetAddress().toString();
        int playerPort = playerSocket.getPort();
        return timestamp + "-" + playerAddress + "-" + playerPort;
    }
}
