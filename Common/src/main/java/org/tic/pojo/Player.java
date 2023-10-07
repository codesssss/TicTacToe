package org.tic.pojo;

import org.tic.ENUM.PlayerStatus;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

import org.tic.ClientCallback;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 10:00 pm
 */
public class Player implements Serializable {
    private String username;
    private PlayerStatus status;
    private ScheduledFuture<?> disconnectFuture;
    private Integer score;
    private Integer rank;
    private String symbol;
    private ClientCallback clientCallback;

    public Player(String username, ClientCallback clientCallback) {
        this.clientCallback = clientCallback;
        this.username = username;
        this.score = 0;
        this.status = PlayerStatus.ACTIVE;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void addScore(int delta) {
        this.score += delta;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public ScheduledFuture<?> getDisconnectFuture() {
        return disconnectFuture;
    }

    public void setDisconnectFuture(ScheduledFuture<?> disconnectFuture) {
        this.disconnectFuture = disconnectFuture;
    }

    public ClientCallback getClientCallback() {
        return clientCallback;
    }

    public void setClientCallback(ClientCallback clientCallback) {
        this.clientCallback = clientCallback;
    }
}
