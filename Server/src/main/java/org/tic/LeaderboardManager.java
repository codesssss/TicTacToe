package org.tic;

import org.tic.pojo.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Xuhang Shi
 * @date 7/10/2023 8:16 pm
 */
public class LeaderboardManager {

    private static volatile ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();

    private LeaderboardManager() {
    }

    public static synchronized void addPlayer(Player player) {
        players.put(player.getUsername(), player);
        sortPlayers();
    }

    public static synchronized void updateScore(Player player, int delta) {
        player.addScore(delta);
        sortPlayers();
    }

    private static void sortPlayers() {
        LinkedHashMap<String, Player> sortedPlayers = players.entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    int scoreComparison = Integer.compare(e2.getValue().getScore(), e1.getValue().getScore());
                    if (scoreComparison == 0) {
                        return e1.getKey().compareTo(e2.getKey());
                    }
                    return scoreComparison;
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        players.clear();
        players.putAll(sortedPlayers);
    }

    public static synchronized int getRank(String username) {
        int rank = 1;
        for (String user : players.keySet()) {
            if (user.equals(username)) {
                return rank;
            }
            rank++;
        }
        return -1;
    }
}
