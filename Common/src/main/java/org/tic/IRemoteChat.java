package org.tic;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:13 pm
 */
public interface IRemoteChat extends Remote {
    void sendMessage(String username, String message) throws RemoteException;
    String[] retrieveMessages() throws RemoteException;
}
