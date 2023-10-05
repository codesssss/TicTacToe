package org.tic;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 11:32 pm
 */

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TicTacToeServer {

    public static final String BINDING_NAME = "TicTacToeService";
    public static final int PORT = 1099;

    public static void main(String[] args) {
        try {


            Registry registry = LocateRegistry.createRegistry(PORT);

            IRemoteTic ticService = new IRemoteTicImpl();

            registry.bind(BINDING_NAME, ticService);

            System.out.println("Tic Tac Toe server is running...");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}