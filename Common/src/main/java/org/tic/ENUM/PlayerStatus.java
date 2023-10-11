package org.tic.ENUM;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 11:01 pm
 */
public enum PlayerStatus {
    ACTIVE("Active"),  // Player is active but not yet in a game
    IN_GAME("In Game"),  // Player is currently in a game
    WAITING_FOR_RECONNECT("Waiting for Reconnect"),  // Player has disconnected and has a window to reconnect
    DISCONNECTED("Disconnected");  // Player has disconnected and did not reconnect in the allotted time

    private final String description;

    PlayerStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}

