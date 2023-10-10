package org.tic;

import org.tic.ENUM.GameStatus;
import org.tic.pojo.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:14 pm
 */
public interface IRemoteTic extends Remote {
    boolean makeMove(int x, int y, String username) throws RemoteException;

    void joinQueue(String username) throws RemoteException;

    String[][] getBoard(String username) throws RemoteException;

    boolean connect(String username, ClientCallback callback) throws RemoteException;

    void sendMessage(String username, String message) throws RemoteException;

    void quitGame(String username) throws RemoteException;

    void ping() throws RemoteException;

    void sendTime(String username,int time) throws RemoteException;
    //New Move
    //Board
}
