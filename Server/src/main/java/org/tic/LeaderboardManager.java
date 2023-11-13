package org.tic;

import org.tic.pojo.Player;

import java.util.*;


/**
 * @author Xuhang Shi
 * @date 7/10/2023 8:16 pm
 */
public class LeaderboardManager {

    private static final List<Player> players = Collections.synchronizedList(new ArrayList<>());

    private LeaderboardManager() {
    }

    public static void addPlayer(Player player) {
        synchronized (players) {
            players.add(player);
            sortAndRankPlayers();
        }
    }

    public static void updateScore(Player player, int delta) {
        synchronized (players) {
            player.addScore(delta);
            sortAndRankPlayers();
        }
    }

    private static void sortAndRankPlayers() {
        synchronized (players) {
            players.sort((p1, p2) -> {
                int scoreComparison = Integer.compare(p2.getScore(), p1.getScore());
                if (scoreComparison == 0) {
                    return p1.getUsername().compareTo(p2.getUsername());
                }
                return scoreComparison;
            });

            int rank = 1;
            for (Player player : players) {
                player.setRank(rank++);
            }
        }
    }

    public static int getRank(String username) {
        synchronized (players) {
            for (Player player : players) {
                if (player.getUsername().equals(username)) {
                    return player.getRank();
                }
            }
            return -1; // Player not found
        }
    }
}