package main.internals;

/**
 *
 * @author 220882
 * @version V.1
 *
 */

public class Piece {

    private Player player;
    private boolean king;

    public Piece(Player player, boolean king){
        this.player = player;
        this.king = king;
    }


    //Returns the king
    public boolean isKing() {

        return king;
    }

    //Gets player
    public Player getPlayer() {

        return player;
    }

    /**
     * Get possible y-direction movements
     * @return
     */
    public int[] getYMovements(){
        int[] result = new int[]{};
        if (king){
            result = new int[]{-1,1};
        }
        else{
            switch (player){
                case AI:
                    result = new int[]{1};
                    break;
                case HUMAN:
                    result = new int[]{-1};
                    break;
            }
        }
        return result;
    }

    /**
     * Get possible x-direction movements
     * @return
     */
    public int[] getXMovements(){
        return new int[]{-1,1};
    }

}
