package main.gui;

/**
 * GUI enum Component representing the two colours of the pieces
 * @author 220882
 * @version V.1
 */

public enum Colour {
    WHITE,
    BLACK;

    public Colour getOpposite(){
        Colour result = null;
        if (this == WHITE){
            result = BLACK;
        }
        else if (this == BLACK){
            result = WHITE;
        }
        if(result == null){
            throw new RuntimeException("Null piece doesn't have any opposites");
        }
        return result;
    }
}

