package org.tic;

import org.tic.pojo.Message;
import org.tic.pojo.Player;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 8:46 pm
 */
public class TicTacToeClient extends UnicastRemoteObject implements ClientCallback {

    private IRemoteTic server;
    private String username;
    private String symbol;
    private TicTacToeGUI ticTacToeGUI;

    public TicTacToeClient(String username, String serverIP, int serverPort) throws RemoteException, NotBoundException {
        this.username = username;
        Registry registry = LocateRegistry.getRegistry(serverIP, serverPort);
        server = (IRemoteTic) registry.lookup("TicTacToeService");
    }

    @Override
    public void notifyMatchStarted(String opponentName, String yourSymbol) throws RemoteException {
        this.symbol = yourSymbol;
        // Update GUI to show the match has started and your symbol
        ticTacToeGUI.updateMatchStarted(opponentName, yourSymbol);
    }

    @Override
    public void notifyTurn() throws RemoteException {
        ticTacToeGUI.displayTurn();
    }

    @Override
    public void updateOpponentMove(int x, int y) throws RemoteException {
        // Update GUI to show the opponent's move
        ticTacToeGUI.updateOpponentMove(x, y);
    }

    @Override
    public void notifyWinner(String winnerName) throws RemoteException {
        // Update GUI to show the winner
        ticTacToeGUI.displayWinner(winnerName);
    }

    @Override
    public void notifyDraw() throws RemoteException {
        // Update GUI to show the match is a draw
        ticTacToeGUI.displayDraw();
    }

    @Override
    public void receiveChatMessage(Message message) throws RemoteException {
        // Update GUI to display the received chat message
        ticTacToeGUI.updateChat(message);
    }

    @Override
    public void ping() throws RemoteException {
        // Nothing to do here. Just to check if the client is alive.
    }

    @Override
    public void updateBoard(String[][] board) {
        ticTacToeGUI.updateBoard(board);
    }

    @Override
    public void updateMessages(List<Message> messages) {
        ticTacToeGUI.updateMessages(messages);
    }


    public void connect() throws RemoteException {
        if(server.connect(this.username, this)){
            ticTacToeGUI.displayWaiting();
        }else {

        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void sendChatMessage(String message) throws RemoteException {
        server.sendMessage(username, message);
    }

    public void setServer(IRemoteTic server) {
        this.server = server;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public IRemoteTic getServer() {
        return server;
    }

    public String getUsername() {
        return username;
    }

    public TicTacToeGUI getTicTacToeGUI() {
        return ticTacToeGUI;
    }

    public void setTicTacToeGUI(TicTacToeGUI ticTacToeGUI) {
        this.ticTacToeGUI = ticTacToeGUI;
    }

    public static void main(String[] args) throws NotBoundException, RemoteException {
//        if (args.length < 3) {
//            System.out.println("Usage: java Client <username> <server_ip> <server_port>");
//            return;
//        }
//
//        String username = args[0];
//        String serverIP = args[1];
//        int serverPort = Integer.parseInt(args[2]);

//        TicTacToeClient ticTacToeClient = new TicTacToeClient(username, serverIP, serverPort);
        TicTacToeClient ticTacToeClient = new TicTacToeClient("jack", "localhost", 1099);
        TicTacToeGUI ticTacToeGUI = new TicTacToeGUI(ticTacToeClient);
        ticTacToeClient.setTicTacToeGUI(ticTacToeGUI);
        ticTacToeClient.connect();
    }
}
