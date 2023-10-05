package org.tic.pojo;

import java.io.Serializable;

/**
 * @author Xuhang Shi
 * @date 5/10/2023 4:31 pm
 */
public class Message implements Serializable {
    String username;
    String message;
    Integer rank;

    public Message(String username, String message, Integer rank) {
        this.username = username;
        this.message = message;
        this.rank = rank;
    }

    public Message(String username, String message) {
        this.username = username;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
