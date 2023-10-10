package org.tic;

import org.tic.ENUM.GameStatus;
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
    private static volatile List<Player> inactivePlayers = new Vector<>();
    private static volatile Map<Player, GameSession> activeGames = new HashMap<>();
    private static volatile List<Player> disconnectedButNotHandledPlayers = new Vector<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected IRemoteTicImpl() throws RemoteException {
        startHeartbeatCheck();
    }


    @Override
    public void joinQueue(String username) throws RemoteException {
        synchronized (waitingPlayers) {
            Player player = findPlayerByUsername(username);
            inactivePlayers.remove(player);
            waitingPlayers.add(player);
            if (waitingPlayers.size() >= 2) {
                Player player1 = waitingPlayers.remove(0);
                Player player2 = waitingPlayers.remove(0);
                GameSession session = new GameSession(player1, player2);
                activeGames.put(player1, session);
                activeGames.put(player2, session);
                session.startGame();
            }
        }
    }

    @Override
    public boolean makeMove(int x, int y, String username) {
        GameSession session = findGameSessionByPlayer(username);
        if (session != null) {
            try {
                GameStatus status = session.makeMove(x, y, username);
                if (status.equals(GameStatus.FINISHED)) {
                    inactivePlayers.add(session.getPlayer(username));
                    inactivePlayers.add(session.getOtherPlayer(username));

                    // Remove the game session
                    activeGames.remove(session.getPlayer(username));
                    activeGames.remove(session.getOtherPlayer(username));

                } else if (status.equals(GameStatus.IN_GAME)) {

                }
            } catch (RemoteException e) {
                handlePlayerDisconnected(session.getPlayer(username));
            }
        }
        return false;
    }

    @Override
    public void sendMessage(String username, String message) throws RemoteException {
        GameSession gameSession = findGameSessionByPlayer(username);
        try {
            gameSession.sendMessage(username, message);
        } catch (RemoteException e) {
            handlePlayerDisconnected(gameSession.getPlayer(username));
        }
    }

    @Override
    public void quitGame(String username) throws RemoteException {
        GameSession session = findGameSessionByPlayer(username);
        Player loser = findPlayerByUsername(username);
        if (session == null) {
            waitingPlayers.remove(loser);
            if (loser != null && !inactivePlayers.contains(loser)) {
                inactivePlayers.add(loser);
            }
            return;
        }
        Player winner = session.getOtherPlayer(username);
        winner.getClientCallback().notifyQuit();
        LeaderboardManager.updateScore(winner, 5);
        LeaderboardManager.updateScore(loser, -5);
        winner.setRank(LeaderboardManager.getRank(winner.getUsername()));
        loser.setRank(LeaderboardManager.getRank(loser.getUsername()));
        inactivePlayers.add(session.getPlayer(username));
        inactivePlayers.add(session.getOtherPlayer(username));

        // Remove the game session
        activeGames.remove(session.getPlayer(username));
        activeGames.remove(session.getOtherPlayer(username));
    }

    @Override
    public void ping() throws RemoteException {

    }

    @Override
    public void sendTime(String username, int time) throws RemoteException {
        findGameSessionByPlayer(username).setTime(time);
    }

    @Override
    public boolean connect(String username, ClientCallback clientCallback) throws RemoteException {
        Player player = findPlayerByUsername(username);
        GameSession gameSession = findGameSessionByPlayer(username);

        if (player != null) {
            // Player is re-connecting
            if (gameSession != null) {
                if(!disconnectedButNotHandledPlayers.contains(player)){
                    clientCallback.notifyDuplicateUsername(username);
                    return false;
                }
                try {
                    Player otherPlayer = gameSession.getOtherPlayer(username);
                    Player currentPlayer = gameSession.getCurrentPlayer();
                    Thread.sleep(1000);
                    Integer time = gameSession.getTime();  // Get the time of the other player (the one that didn't disconnect)
                    if (time == null) {
                        terminateGameSession(gameSession);
                        player.getDisconnectFuture().cancel(false);
                        return false;
                    }
                    boolean isTurn = currentPlayer.getUsername().equals(username);
                    player.setClientCallback(clientCallback);
                    player.getDisconnectFuture().cancel(false);

                    player.getClientCallback().updateReconnect(currentPlayer.getRank(), currentPlayer.getUsername(),
                            currentPlayer.getSymbol(), gameSession.getMessages(), gameSession.getBoard(), isTurn, time, player.getRank(), player.getSymbol());

                    otherPlayer.getClientCallback().notifyReconnected();  // Notify the other player that this player has reconnected

                    return true;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    terminateGameSession(gameSession);
                    player.getDisconnectFuture().cancel(false);
                    return false; // Failed to reconnect the player for some reason.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                player.setClientCallback(clientCallback);
                inactivePlayers.remove(player);
                joinQueue(player);
            }
        } else {
            // New player
            try {
                player = new Player(username, clientCallback);
                LeaderboardManager.addPlayer(player);
                joinQueue(player);
                return true;
            } catch (Exception e) {
                return false; // Failed to add a new player for some reason.
            }
        }

        return false; // Default return in case no conditions above are met.
    }


    public void joinQueue(Player player) throws RemoteException {
        synchronized (waitingPlayers) {
            waitingPlayers.add(player);
            if (waitingPlayers.size() >= 2) {
                Player player1 = waitingPlayers.remove(0);
                Player player2 = waitingPlayers.remove(0);
                GameSession session = new GameSession(player1, player2);
                activeGames.put(player1, session);
                activeGames.put(player2, session);
                session.startGame();
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
        for (Player player : inactivePlayers) {
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
                if (!disconnectedButNotHandledPlayers.contains(player)) {
                    disconnectedButNotHandledPlayers.add(player);
                    handlePlayerDisconnected(player);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }


    public void handlePlayerDisconnected(Player player) {
        GameSession session = findGameSessionByPlayer(player.getUsername());
        if (session != null) {
            Player otherPlayer = session.getOtherPlayer(player.getUsername());
            boolean otherPlayerDisconnected = false;
            if (otherPlayer != null) {
                try {
                    otherPlayer.getClientCallback().ping();  // Check if the other player is also disconnected.
                } catch (RemoteException e) {
                    otherPlayerDisconnected = true;
                }
            }

            if (otherPlayerDisconnected) {
                // Both players are disconnected. Terminate the game session.
                terminateGameSession(session);
            } else {
                // Only one player is disconnected. Notify the other player and schedule a task to handle game termination logic later.
                try {
                    if (otherPlayer != null) {
                        otherPlayer.getClientCallback().notifyCrash();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

                Runnable task = new Runnable() {
                    public void run() {
                        disconnectedButNotHandledPlayers.remove(player);
                        // Handle game termination logic here

                        GameSession session = activeGames.remove(player);
                        if (session != null) {
                            // Notify the other player of victory and remove game session
                            Player otherPlayer = session.getOtherPlayer(player.getUsername());
                            if (otherPlayer != null) {
                                activeGames.remove(otherPlayer);
                                LeaderboardManager.updateScore(player, -2);  // Assuming a penalty for disconnecting
                                LeaderboardManager.updateScore(otherPlayer, 2);  // Assuming a reward for the other player
                                player.setRank(LeaderboardManager.getRank(player.getUsername()));
                                otherPlayer.setRank(LeaderboardManager.getRank(otherPlayer.getUsername()));
                                inactivePlayers.add(player);
                                inactivePlayers.add(otherPlayer);
                            }
                        }
                    }
                };

                ScheduledFuture<?> scheduledFuture = scheduler.schedule(task, 30, TimeUnit.SECONDS);
                player.setDisconnectFuture(scheduledFuture);
            }
        }
    }


    private void terminateGameSession(GameSession gameSession) {
        Player player1 = gameSession.getCurrentPlayer();
        Player player2 = gameSession.getOtherPlayer(player1.getUsername());

        inactivePlayers.add(player1);
        inactivePlayers.add(player2);

        activeGames.remove(player1);
        activeGames.remove(player2);
    }

}