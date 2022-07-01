package main.gui;
import javax.swing.*;
import java.awt.*;

/**
 * Different colours used for the square panels on the board (black, white and gray)
 * @author 220882
 * @version V.1
 */


public class Squares extends JPanel {

    private Color colour;

    //Black and white board panel colours with their associated size
    public Squares(int i, int j){
        this.setPreferredSize(new Dimension(Settings.squareSize,Settings.squareSize));
        if( ((i % 2) + (j % 2)) % 2 == 0){
            colour = Color.WHITE;
        }
        else{
            colour = Color.BLACK;
        }
    }

    //Gray highlighted panel to be used for hints
    public void setHighlighted(){
        colour = Color.GRAY;
    }


    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(colour);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}