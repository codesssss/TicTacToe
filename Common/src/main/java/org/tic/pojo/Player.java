package org.tic.pojo;

import org.tic.ENUM.PlayerStatus;

import java.util.Timer;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 10:00 pm
 */
public class Player {
    private String username;
    private PlayerStatus status;
    private ScheduledFuture<?> disconnectFuture;
    private Integer score;

    public Player(String username) {
        this.username = username;
        this.score=0;
        this.status=PlayerStatus.ACTIVE;
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
}
