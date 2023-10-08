package org.tic;

import org.tic.pojo.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Xuhang Shi
 * @date 5/10/2023 12:01 pm
 */
public interface ClientCallback extends Remote {
    void notifyMatchStarted(String opponentName, String yourSymbol,int rank) throws RemoteException;
    void notifyTurn() throws RemoteException;
    void updateOpponentMove(int x, int y) throws RemoteException;
    void notifyWinner(String winnerName) throws RemoteException;
    void notifyDraw() throws RemoteException;
    void receiveChatMessage(Message message) throws RemoteException;
    void updateBoard(String[][] board) throws RemoteException;
    void updateMessages(List<Message> messages) throws RemoteException;
    void ping() throws RemoteException;
    void resetPlayerLabelAndTime(String rank, String name, String symbol) throws RemoteException;
    void changeLabel(int rank, String name, String symbol) throws RemoteException;



}

