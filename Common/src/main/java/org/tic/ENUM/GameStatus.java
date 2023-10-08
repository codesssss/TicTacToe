package org.tic.ENUM;

/**
 * @author Xuhang Shi
 * @date 4/10/2023 11:10 pm
 */
public enum GameStatus {
    IN_GAME("In Game"),
    ERROR("Error"),

    FINISHED("Finished");

    private final String description;

    GameStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
