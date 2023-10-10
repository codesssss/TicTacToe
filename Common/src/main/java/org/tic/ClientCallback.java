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
    void notifyMatchStarted(int currentRank, String currentName, String currentSymbol, int rank, String symbol) throws RemoteException;
    void notifyTurn() throws RemoteException;
    void updateOpponentMove(int x, int y) throws RemoteException;
    void notifyWinner(String winnerName) throws RemoteException;
    void notifyDraw() throws RemoteException;
    void notifyQuit() throws RemoteException;
    void notifyCrash() throws RemoteException;
    void notifyReconnected() throws RemoteException;
    void updateReconnect(int rank, String name, String symbol, List<Message> messages, String[][] board,boolean isTurn,int time,int myRank,String mySymbol) throws RemoteException;
    void receiveChatMessage(Message message) throws RemoteException;
    void ping() throws RemoteException;
    void resetPlayerLabelAndTime(String rank, String name, String symbol) throws RemoteException;
    void changeLabel(int rank, String name, String symbol) throws RemoteException;



}

