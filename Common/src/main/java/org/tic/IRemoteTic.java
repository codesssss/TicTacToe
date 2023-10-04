package org.tic;

import org.tic.pojo.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:14 pm
 */
public interface IRemoteTic extends Remote {
    boolean makeMove(int x, int y, String username) throws RemoteException;

    String[][] getBoard() throws RemoteException;

    void joinQueue(Player player) throws RemoteException;

    GameStatus checkMatchStatus(String username) throws RemoteException;

    String[][] getBoard(String username) throws RemoteException;

    void connect(String username) throws RemoteException;
    //New Move
    //Board
}
