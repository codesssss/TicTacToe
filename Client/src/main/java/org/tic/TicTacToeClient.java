package org.tic;

import org.tic.pojo.Message;

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
    private TicTacToeListener listener;

    public TicTacToeClient(String username, String serverIP, int serverPort) throws RemoteException, NotBoundException {
        this.username = username;
        Registry registry = LocateRegistry.getRegistry(serverIP, serverPort);
        server = (IRemoteTic) registry.lookup("TicTacToeService");
    }

    public void setListener(TicTacToeListener listener) {
        this.listener = listener;
    }

    @Override
    public void notifyMatchStarted(String opponentName, String yourSymbol) throws RemoteException {
        this.symbol = yourSymbol;
        if (listener != null) {
            listener.onMatchStarted(opponentName, yourSymbol);
        }
    }

    @Override
    public void notifyTurn() throws RemoteException {
        if (listener != null) {
            listener.onTurnNotified();
        }
    }

    @Override
    public void updateOpponentMove(int x, int y) throws RemoteException {
        if (listener != null) {
            listener.onOpponentMoved(x, y);
        }
    }

    @Override
    public void notifyWinner(String winnerName) throws RemoteException {
        if (listener != null) {
            listener.onWinnerDeclared(winnerName);
        }
    }

    @Override
    public void notifyDraw() throws RemoteException {
        if (listener != null) {
            listener.onMatchDraw();
        }
    }

    @Override
    public void receiveChatMessage(Message message) throws RemoteException {
        if (listener != null) {
            listener.onChatMessageReceived(message);
        }
    }

    @Override
    public void ping() throws RemoteException {
        // Do nothing here.
    }

    @Override
    public void updateBoard(String[][] board) {
        if (listener != null) {
            listener.onBoardUpdated(board);
        }
    }

    @Override
    public void updateMessages(List<Message> messages) {
        if (listener != null) {
            listener.onMessagesUpdated(messages);
        }
    }

    public void connectServer() throws RemoteException {
        server.connect(this.username, this);
        listener.onDisplayWaiting();
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
        ticTacToeClient.connectServer();
    }
}
