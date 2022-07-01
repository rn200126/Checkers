package main.internals;

/**
 *
 * Represents the players of the game: human and AI
 * @author 220882
 * @version V.1
 *
 */

public enum Player {
    AI,
    HUMAN;

    public Player getOpposite() {
        Player result = null;
        if (this == AI) {
            result = HUMAN;
        }
        else if (this == HUMAN) {
            result = AI;
        }
        if (result == null){
            throw new RuntimeException("Player doesn't have opposite");
        }
        return result;
    }
}
