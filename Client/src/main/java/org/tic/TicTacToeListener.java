package org.tic;

import org.tic.pojo.Message;

import java.util.List;

/**
 * @author Xuhang Shi
 * @date 5/10/2023 9:41 pm
 */
public interface TicTacToeListener {
    void onMatchStarted(String opponentName, String yourSymbol);
    void onTurnNotified();
    void onOpponentMoved(int x, int y);
    void onWinnerDeclared(String winnerName);
    void onMatchDraw();
    void onChatMessageReceived(Message message);
    void onBoardUpdated(String[][] board);
    void onMessagesUpdated(List<Message> messages);
    void onDisplayWaiting();
    // ... any other callback methods you need
}
