package org.tic.service;

import org.tic.ENUM.GameStatus;
import org.tic.ENUM.PlayerStatus;
import org.tic.IRemoteTic;
import org.tic.pojo.GameSession;
import org.tic.pojo.Player;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 9:19 pm
 */
public class IRemoteTicImpl implements IRemoteTic {

    private static volatile List<Player> waitingPlayers = new Vector<>();
    private static volatile Map<Player, GameSession> activeGames = new HashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @Override
    public void joinQueue(Player player) throws RemoteException {
        synchronized(waitingPlayers) {
            waitingPlayers.add(player);
            if (waitingPlayers.size() >= 2) {
                Player player1 = waitingPlayers.remove(0);
                Player player2 = waitingPlayers.remove(0);
                GameSession session = new GameSession(player1.getUsername(), player2.getUsername());
                activeGames.put(player1, session);
                activeGames.put(player2, session);
            }
        }
    }

    private GameSession findGameSessionByPlayer(String username) {
        for (Map.Entry<Player, GameSession> entry : activeGames.entrySet()) {
            if (entry.getKey().getUsername().equals(username)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean makeMove(int x, int y, String username) throws RemoteException {
        GameSession session = findGameSessionByPlayer(username);
        if (session != null) {
            return session.makeMove(x, y, username);
        }
        return false;
    }

    @Override
    public GameStatus checkMatchStatus(String username) throws RemoteException {
        return null;
    }

    @Override
    public String[][] getBoard(String username) throws RemoteException {
        GameSession session = findGameSessionByPlayer(username);
        if (session != null) {
            return session.getBoard();
        }
        return new String[3][3];  // or return null or an error status
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

    public void handlePlayerDisconnected(String username) {
        Player player = findPlayerByUsername(username);
        if (player == null) return;

        player.setStatus(PlayerStatus.WAITING_FOR_RECONNECT);

        Runnable task = new Runnable() {
            public void run() {
                if (player.getStatus() == PlayerStatus.WAITING_FOR_RECONNECT) {
                    // Handle game termination logic here
                    GameSession session = activeGames.remove(player);
                    if (session != null) {
                        // Notify the other player of victory and remove game session
                        String otherPlayerName = session.getOtherPlayer(username);
                        Player otherPlayer = findPlayerByUsername(otherPlayerName);
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

    public void connect(String username) {
        Player player = findPlayerByUsername(username);

        if (player != null && player.getStatus() == PlayerStatus.WAITING_FOR_RECONNECT) {
            // Player is re-connecting
            player.getDisconnectFuture().cancel(false);
            player.setStatus(PlayerStatus.IN_GAME);
        } else if (player == null) {
            // New player
            player = new Player(username);
            waitingPlayers.add(player);
        }
    }
}


