package main.gui;
import main.gui.Settings;
import main.internals.Game;
import main.internals.Piece;
import main.internals.Player;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * GUI Component for the Black & White checker pieces
 *  @author 220882
 *  @version V.1
 */

public class CheckerPiece extends JButton {

    private Piece piece;
    private Game game;
    private int position;
    int X;
    int Y;
    int screenX = 0;
    int screenY = 0;

    //Checker piece on the board and add drag and drop feature
    public CheckerPiece(int position, Piece piece, GUI gui) {
        super();
        this.position = position;
        this.piece = piece;
        this.game = game;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon(piece);
        if (piece.getPlayer() == Player.HUMAN && Settings.dragDrop){
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent mouseEvent) {
                    screenX = mouseEvent.getXOnScreen();
                    screenY = mouseEvent.getYOnScreen();
                    X = getX();
                    Y = getY();
                }

                public void mouseReleased(MouseEvent mouseEvent){
                    int deltaX = mouseEvent.getXOnScreen() - screenX;
                    int deltaY = mouseEvent.getYOnScreen() - screenY;
                    int dx = (int) Math.round((double)deltaX / (double) Settings.squareSize);
                    int dy = (int) Math.round((double)deltaY / (double) Settings.squareSize);
                    gui.onMouseRelease(position, dx, dy);
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent mouseEvent) {
                    int deltaX = mouseEvent.getXOnScreen() - screenX;
                    int deltaY = mouseEvent.getYOnScreen() - screenY;
                    setLocation(X + deltaX, Y + deltaY);
                }
            });
        }
    }


    //Gets position
    public int getPosition() {
        return position;
    }

    public Piece getPiece() {
        return piece;
    }

    //Sets icon for the game features (checkers and king)
    private void setIcon(Piece piece){
        BufferedImage buttonIcon = null;
        Colour colour = Settings.getColour(piece.getPlayer());
        try {
            if (colour == Colour.BLACK) {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(new File("images/blackking.png"));
                } else {
                    buttonIcon = ImageIO.read(new File("images/blackchecker.gif"));
                }
            }
            else {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(new File("images/whiteking.png"));
                }
                else {
                    buttonIcon = ImageIO.read(new File("images/whitechecker.gif"));
                }
            }
        }
        catch(IOException e){
            System.out.println(e.toString());
        }

        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(Settings.checkerWidth,Settings.checkerHeight,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }

}
