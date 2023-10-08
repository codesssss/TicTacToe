package org.tic;

import org.tic.pojo.Message;

import javax.swing.*;
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
    private Integer rank;
    private String username;
    private String symbol;
    private TicTacToeGUI ticTacToeGUI;

    public TicTacToeClient(String username, String serverIP, int serverPort) throws RemoteException, NotBoundException {
        this.username = username;
        Registry registry = LocateRegistry.getRegistry(serverIP, serverPort);
        server = (IRemoteTic) registry.lookup("TicTacToeService");
    }

    public void setTicTacToeGUI(TicTacToeGUI ticTacToeGUI) {
        this.ticTacToeGUI = ticTacToeGUI;
    }

    @Override
    public void notifyMatchStarted(String opponentName, String yourSymbol,int rank) throws RemoteException {
        this.symbol = yourSymbol;
        this.rank=rank;
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.updateMatchStarted(opponentName, yourSymbol));
        }
    }

    @Override
    public void notifyTurn() throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.displayTurn());
        }
    }

    @Override
    public void updateOpponentMove(int x, int y) throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.updateOpponentMove(x, y));
        }
    }

    @Override
    public void notifyWinner(String winnerName) throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.displayWinner(winnerName));
        }
    }

    @Override
    public void notifyDraw() throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.displayDraw());
        }
    }

    @Override
    public void receiveChatMessage(Message message) throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.updateChat(message));
        }
    }

    @Override
    public void ping() throws RemoteException {
        // Do nothing here.
    }

    @Override
    public void resetPlayerLabelAndTime(String rank, String name, String symbol) throws RemoteException {
        SwingUtilities.invokeLater(() -> ticTacToeGUI.changePlayerLabel(rank,name,symbol));
        SwingUtilities.invokeLater(() -> ticTacToeGUI.resetAndStartTurnTimer());
    }

    @Override
    public void changeLabel(int rank, String name, String symbol) throws RemoteException {
        SwingUtilities.invokeLater(() -> ticTacToeGUI.changePlayerLabel(String.valueOf(rank),name,symbol));
    }

    @Override
    public void updateBoard(String[][] board) {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.updateBoard(board));
        }
    }

    @Override
    public void updateMessages(List<Message> messages) {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.updateMessages(messages));
        }
    }


    public void connectServer() throws RemoteException {
        //TODO: handle exception
        SwingUtilities.invokeLater(() -> {
            try {
                server.connect(this.username, this);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            ticTacToeGUI.displayWaiting();
        });

    }

    public String getSymbol() {
        return symbol;
    }

    public void sendChatMessage(String message) throws RemoteException {
        server.sendMessage(username, message);
    }

    public IRemoteTic getServer() {
        return server;
    }

    public String getUsername() {
        return username;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }


    public static void main(String[] args) throws NotBoundException, RemoteException {
        if (args.length < 3) {
            System.out.println("Usage: java Client <username> <server_ip> <server_port>");
            return;
        }

        String username = args[0];
        String serverIP = args[1];
        int serverPort = Integer.parseInt(args[2]);

        TicTacToeClient ticTacToeClient = new TicTacToeClient(username, serverIP, serverPort);
//        TicTacToeClient ticTacToeClient = new TicTacToeClient("jack", "localhost", 1099);
        TicTacToeGUI ticTacToeGUI = new TicTacToeGUI(ticTacToeClient);
    }
}
