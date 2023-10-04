package org.tic;

import org.tic.pojo.Player;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 8:46 pm
 */
public class TicTacToeClient {

    private IRemoteTic server;
    private String username;

    public TicTacToeClient(String username, String serverIP, int serverPort) throws RemoteException, NotBoundException {
        this.username = username;

        Registry registry = LocateRegistry.getRegistry(serverIP, serverPort);
        server = (IRemoteTic) registry.lookup("TicTacToeService");

    }

    public void joinGame() throws RemoteException {
        server.joinQueue(new Player(username));
    }

    public boolean makeMove(int x, int y, String username) throws RemoteException {
        // Handle the move received from server (for syncing purposes, if needed)
        return server.makeMove(x, y, username);
    }

    //... other methods of IRemoteTic ...
}
