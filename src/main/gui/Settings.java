package main.gui;
import main.internals.Player;

/**
 * GUI Settings for board anc its components
 * @author 220882
 * @version V.1
 */

public class Settings{
    //Declare variables
    public static Colour aiColour = Colour.BLACK;
    public static int squareSize = 90;
    public static int checkerWidth = 6* squareSize/6;
    public static int checkerHeight = 6* squareSize/6;
    public static int ghostButtonWidth = 30* squareSize/29;
    public static int ghostButtonHeight = 5* squareSize/6;
    public static int AiMinPauseDurationInMs = 600;
    public static boolean dragDrop = false;
    public static boolean helpMode = true;
    public static boolean hintMode = false;

    /**
     * Specifies the checker pieces colour for each player
     * @param player game players
     * @return result result
     */
    public static Colour getColour(Player player){
        Colour result = null;
        if (player == Player.AI){
            result = Settings.aiColour;
        }
        else if (player == Player.HUMAN){
            result = Settings.aiColour.getOpposite();
        }
        if(result == null){
            throw new RuntimeException("Null player has no piece.");
        }
        return result;
    }
}

