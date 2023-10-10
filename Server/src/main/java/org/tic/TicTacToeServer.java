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

    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 2) {
            System.err.println("Usage: java -jar server.jar <ip> <port>");
            System.exit(1);
        }

        String ip = args[0];
        int port;

        // Parse the port number
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Port number must be an integer.");
            System.exit(1);
            return;
        }

        try {
            // Set the java.rmi.server.hostname property to the IP address
            System.setProperty("java.rmi.server.hostname", ip);

            // Construct the URL for RMI
            String url = "rmi://" + ip + ":" + port + "/TicTacToeService";

            // Create or get the registry
            Registry registry = LocateRegistry.createRegistry(port);

            IRemoteTic ticService = new IRemoteTicImpl();

            // Bind the service to the URL
            registry.bind(url, ticService);

            System.out.println("Tic Tac Toe server is running on " + url);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}