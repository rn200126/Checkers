package main.internals;

/**
 *
 * Moves class that determines the dialog that will pop up when the players make certain moves
 * @author 220882
 * @version V.1
 *
 */

public enum Moves {
    UNKNOWN_INVALID("That wasn't a valid move!"),
    NO_FREE_SPACE ("You cannot move on top of another piece!"),
    NOT_DIAGONAL ("You can only move diagonally!"),
    ONLY_SINGLE_DIAGONALS ("You can only make one move!"),
    PIECE_BLOCKED ("This piece cannot move diagonally!"),
    NO_BACKWARD_MOVES_FOR_SINGLES ("Only kings can move backwards!"),
    FORCED_JUMP ("You're forced to make a move."),
    NOT_ON_BOARD(""),
    SUCCESS ("Success");

    private final String name;

    Moves(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
