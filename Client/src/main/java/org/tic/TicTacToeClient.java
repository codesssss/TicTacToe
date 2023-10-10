package org.tic;

import org.tic.pojo.Message;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public TicTacToeClient(String username, String serverIP, int serverPort) throws RemoteException, NotBoundException {
        this.username = username;
        String url="rmi://"+serverIP+":"+serverPort+"/TicTacToeService";
        Registry registry = LocateRegistry.getRegistry(serverIP);
        server = (IRemoteTic) registry.lookup(url);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                server.ping(); // Assuming the server has a ping method. If not, use any lightweight method.
            } catch (RemoteException e) {
                handleServerUnavailability();
                scheduler.shutdown(); // Stop the scheduler once knows the server is unavailable
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void setTicTacToeGUI(TicTacToeGUI ticTacToeGUI) {
        this.ticTacToeGUI = ticTacToeGUI;
    }

    @Override
    public void notifyMatchStarted(int currentRank, String currentName, String currentSymbol, int rank, String symbol) throws RemoteException {
        this.symbol = symbol;
        this.rank = rank;
        if (ticTacToeGUI != null) {
            ticTacToeGUI.setMyTurn(false);
            SwingUtilities.invokeLater(() -> ticTacToeGUI.changePlayerLabel(String.valueOf(currentRank), currentName, currentSymbol));
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
    public void notifyQuit() throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.displayQuit());
        }
    }

    @Override
    public void notifyCrash() throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.displayCrash());
        }
    }

    @Override
    public void notifyReconnected() throws RemoteException {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> ticTacToeGUI.displayReconnected());
        }
    }

    public void updateReconnect(int rank, String name, String symbol, List<Message> messages, String[][] board, boolean isTurn, int time, int myRank,String mySymbol) throws RemoteException {
        if (ticTacToeGUI != null) {
            this.rank=myRank;
            this.symbol=mySymbol;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ticTacToeGUI.changePlayerLabel(String.valueOf(rank), name, symbol);
                    ticTacToeGUI.updateBoard(board);
                    ticTacToeGUI.updateMessages(messages);
                    if (isTurn) {
                        ticTacToeGUI.displayTurnWithTime(time);
                    } else {
                        ticTacToeGUI.resetAndStartTurnTimer(time);
                    }
                }
            });
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
        SwingUtilities.invokeLater(() -> ticTacToeGUI.changePlayerLabel(rank, name, symbol));
        SwingUtilities.invokeLater(() -> ticTacToeGUI.resetAndStartTurnTimer());
    }

    @Override
    public void changeLabel(int rank, String name, String symbol) throws RemoteException {
        SwingUtilities.invokeLater(() -> ticTacToeGUI.changePlayerLabel(String.valueOf(rank), name, symbol));
    }

    private void handleServerUnavailability() {
        if (ticTacToeGUI != null) {
            SwingUtilities.invokeLater(() -> {
                ticTacToeGUI.displayServerUnavailableMessage();
            });
        }
    }


    public void handleRemoteException(RemoteException e) {
        e.printStackTrace();
        handleServerUnavailability();
    }


    public void connectServer() {
        SwingUtilities.invokeLater(() -> {
            try {
                server.connect(this.username, this);
                ticTacToeGUI.displayWaiting();
            } catch (RemoteException e) {
                handleRemoteException(e);
            }
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
        TicTacToeGUI ticTacToeGUI = new TicTacToeGUI(ticTacToeClient);
    }
}
