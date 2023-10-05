package org.tic;

import org.tic.ClientCallback;
import org.tic.ENUM.GameStatus;
import org.tic.ENUM.PlayerStatus;
import org.tic.IRemoteTic;
import org.tic.pojo.GameSession;
import org.tic.pojo.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:19 pm
 */
public class IRemoteTicImpl extends UnicastRemoteObject implements IRemoteTic {

    private static volatile List<Player> waitingPlayers = new Vector<>();
    private static volatile Map<Player, GameSession> activeGames = new HashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected IRemoteTicImpl() throws RemoteException {
    }


    @Override
    public void joinQueue(Player player) throws RemoteException {
        synchronized (waitingPlayers) {
            waitingPlayers.add(player);
            if (waitingPlayers.size() >= 2) {
                Player player1 = waitingPlayers.remove(0);
                Player player2 = waitingPlayers.remove(0);
                GameSession session = new GameSession(player1, player2);
                activeGames.put(player1, session);
                activeGames.put(player2, session);
                player1.getClientCallback().notifyMatchStarted(player1.getUsername(), player1.getSymbol());
                player2.getClientCallback().notifyMatchStarted(player2.getUsername(), player2.getSymbol());
            }
        }
    }

    @Override
    public boolean makeMove(int x, int y, String username) {
        //TODO: DISCONNECT AT SAME TIME
        GameSession session = findGameSessionByPlayer(username);
        if (session != null) {
            try {
                return session.makeMove(x, y, username);
            } catch (RemoteException e) {
                handlePlayerDisconnected(session.getPlayer(username));
            }
        }
        return false;
    }

    @Override
    public String[][] getBoard(String username) throws RemoteException {
        GameSession session = findGameSessionByPlayer(username);
        if (session != null) {
            return session.getBoard();
        }
        return new String[3][3];  // or return null or an error status
    }

    @Override
    public void sendMessage(String username, String message) throws RemoteException {
        GameSession gameSession = findGameSessionByPlayer(username);
        try {
            gameSession.sendMessage(username, message);
        } catch (RemoteException e) {
            handlePlayerDisconnected(gameSession.getPlayer(username));
        }
        //TODO: RANK
    }

    @Override
    public boolean connect(String username, ClientCallback clientCallback) throws RemoteException {
        Player player = findPlayerByUsername(username);

        if (player != null && player.getStatus() == PlayerStatus.WAITING_FOR_RECONNECT) {
            // Player is re-connecting
            try {
                player.getDisconnectFuture().cancel(false);
                player.setStatus(PlayerStatus.IN_GAME);
                player.getClientCallback().updateBoard(getBoard(username));
                GameSession gameSession = findGameSessionByPlayer(username);
                player.getClientCallback().updateMessages(gameSession.getMessages());
                // Reconnecting requires: updating the board, restoring chat, resetting the timer.
                return true;
            } catch (Exception e) {
                return false; // Failed to reconnect the player for some reason.
            }
        } else if (player == null) {
            // New player
            try {
                player = new Player(username, clientCallback);
                joinQueue(player);
                return true;
            } catch (Exception e) {
                return false; // Failed to add a new player for some reason.
            }
        }

        return false; // Default return in case no conditions above are met.
    }


//    public String retrieveMessage(GameSession gameSession) throws RemoteException {
//        List<String> messages = gameSession.getMessages();
//        if (!messages.isEmpty()) {
//            return messages.get(messages.size() - 1);
//        } else {
//            return null;
//        }
//    }

    private GameSession findGameSessionByPlayer(String username) {
        for (Map.Entry<Player, GameSession> entry : activeGames.entrySet()) {
            if (entry.getKey().getUsername().equals(username)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Player findPlayerByUsername(String username) {
        for (Player player : activeGames.keySet()) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        for (Player player : waitingPlayers) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    private void startHeartbeatCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            List<Player> disconnectedPlayers = new ArrayList<>();
            for (Player player : activeGames.keySet()) {
                try {
                    player.getClientCallback().ping();  // A simple method on the client's side to check if it's alive.
                } catch (RemoteException e) {
                    disconnectedPlayers.add(player);
                }
            }

            for (Player player : disconnectedPlayers) {
                handlePlayerDisconnected(player);
            }
        }, 0, 10, TimeUnit.SECONDS);  // Run every 10 seconds
    }


    public void handlePlayerDisconnected(Player player) {
        player.setStatus(PlayerStatus.WAITING_FOR_RECONNECT);

        Runnable task = new Runnable() {
            public void run() {
                if (player.getStatus() == PlayerStatus.WAITING_FOR_RECONNECT) {
                    // Handle game termination logic here
                    GameSession session = activeGames.remove(player);
                    if (session != null) {
                        // Notify the other player of victory and remove game session
                        Player otherPlayer = session.getOtherPlayer(player.getUsername());
                        if (otherPlayer != null) {
                            otherPlayer.setStatus(PlayerStatus.ACTIVE); // or some other status indicating the win
                        }
                    }
                }
            }
        };

        ScheduledFuture<?> scheduledFuture = scheduler.schedule(task, 30, TimeUnit.SECONDS);
        player.setDisconnectFuture(scheduledFuture);
    }


}