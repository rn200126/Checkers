package main.internals;

/**
 *
 * Game rule class which holds static values
 * @author 220882
 * @version V.1
 *
 */


public class Settings {
    public static boolean FORCEDMOVES = true;
    public static Player FIRSTMOVE = Player.HUMAN;
    public static int AI_DEPTH = 7;
    public static final int UNDO_MEMORY = 20;
    public static int HEURISTIC = 1;
}