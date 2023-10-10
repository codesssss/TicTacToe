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
    private Integer time;
    private ClientCallback clientCallback;

    public Player(String username, ClientCallback clientCallback) {
        this.clientCallback = clientCallback;
        this.username = username;
        this.score = 0;
        this.status = PlayerStatus.ACTIVE;
    }

    public synchronized String getSymbol() {
        return symbol;
    }

    public synchronized void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public synchronized Integer getRank() {
        return rank;
    }

    public synchronized void setRank(Integer rank) {
        this.rank = rank;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public synchronized Integer getScore() {
        return score;
    }

    public synchronized void setScore(Integer score) {
        this.score = score;
    }

    public synchronized void addScore(int delta) {
        this.score += delta;
    }

    public synchronized Integer getTime() {
        return time;
    }

    public synchronized void setTime(Integer time) {
        this.time = time;
    }

    public synchronized void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public synchronized PlayerStatus getStatus() {
        return status;
    }

    public synchronized ScheduledFuture<?> getDisconnectFuture() {
        return disconnectFuture;
    }

    public synchronized void setDisconnectFuture(ScheduledFuture<?> disconnectFuture) {
        this.disconnectFuture = disconnectFuture;
    }

    public synchronized ClientCallback getClientCallback() {
        return clientCallback;
    }

    public synchronized void setClientCallback(ClientCallback clientCallback) {
        this.clientCallback = clientCallback;
    }
}
