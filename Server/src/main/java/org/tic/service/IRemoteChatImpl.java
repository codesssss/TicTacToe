package org.tic.service;

import org.tic.IRemoteChat;

import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:15 pm
 */
public class IRemoteChatImpl implements IRemoteChat {
    private LinkedList<String> messages = new LinkedList<>();

    public IRemoteChatImpl() throws RemoteException {
    }

    @Override
    public void sendMessage(String username, String message) throws RemoteException {
        if (messages.size() >= 10) messages.poll();  // remove the oldest if more than 10
        messages.add(username + ": " + message);
    }

    @Override
    public String[] retrieveMessages() throws RemoteException {
        return messages.toArray(new String[0]);
    }
}
