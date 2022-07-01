package main.gui;
import main.internals.Board;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Button representing a possible move for a player
 * @author 220882
 * @version V.1
 */

public class GhostButton extends JButton{

    private Board boardstate;

    public GhostButton(Board state){
        super();
        this.boardstate = state;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon();
    }

    private void setIcon(){
        BufferedImage buttonIcon = null;
        try{
            if(Settings.helpMode){
                buttonIcon = ImageIO.read(new File("images/dottedcircle.png"));

            }
            else{
                buttonIcon = ImageIO.read(new File("images/dottedcirclepink.png"));
            }
        }
        catch (IOException e){
            System.out.println(e.toString());
        }
        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(Settings.ghostButtonWidth,Settings.ghostButtonHeight,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }


    public Board getBoardstate() {

     return boardstate;
    }
}
