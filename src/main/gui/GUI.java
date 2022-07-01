package main.gui;
import main.internals.*;
import main.internals.Settings;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Main GUI class containing all important features
 * @author 220882
 * @version V.1
 */

public class GUI extends JFrame {
    //Declare important variables
    private Game game;
    private ArrayList<Board> possibleMoves;
    private Squares[] squares;
    private JPanel checkerboardPanel;
    private JPanel contentPane;
    private JTextArea textBox;
    private Board hintMove;
    private List<Integer> helpMoves;
    private HashMap<Integer, Integer> difficultyMapping;
    private JButton button1;

    //public static void main(String[] args) {
        //JFrame frame = new GUI;
        //frame.setVisible(true);
    //}


    private void start(){
        settingsPopup();
        game = new Game();
        possibleMoves = new ArrayList<>();
        hintMove = null;
        setup();
        if (main.gui.Settings.hintMode){
            onHintClick();
        }
    }

    public GUI(){
        difficultyMapping = new HashMap<>();
        difficultyMapping.put(1,1);
        difficultyMapping.put(2, 5);
        difficultyMapping.put(3, 8);
        difficultyMapping.put(4, 12);
        start();
    }

    /**
     * Window that lets the player chose desired settings e.g the difficulty of the game, who starts the game
     *
     */

    private void settingsPopup(){
        //Initialise panel with all the features
        JPanel panel = new JPanel(new GridLayout(5,1));
        //Set difficulty
        JLabel newtext = new JLabel("Welcome :) Chose your desired settings!", 10);
        panel.add(newtext);
        JLabel text1 = new JLabel("Set Difficulty", 10);
        panel.add(text1);
        JSlider slider = new JSlider();
        panel.add(slider);
        slider.setSnapToTicks(true);
        slider.setMaximum(3);
        slider.setMinimum(1);
        slider.setMajorTickSpacing(1);
        Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        labels.put(1, new JLabel("Easy"));
        labels.put(2, new JLabel("Medium"));
        labels.put(3, new JLabel("Hard"));
        slider.setLabelTable(labels);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPreferredSize(new Dimension(200,50));
        slider.setValue(3);;
        //Option on who gets to start
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton humanFirstRadioButton = new JRadioButton("Human plays first");
        JRadioButton aiRadioButton = new JRadioButton("AI plays first");
        buttonGroup.add(humanFirstRadioButton);
        buttonGroup.add(aiRadioButton);
        aiRadioButton.setSelected(Settings.FIRSTMOVE== Player.AI);
        humanFirstRadioButton.setSelected(Settings.FIRSTMOVE== Player.HUMAN);
        panel.add(humanFirstRadioButton);
        panel.add(aiRadioButton);
        int result = JOptionPane.showConfirmDialog(null, panel, "Game Settings",
                JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION){
            Settings.AI_DEPTH =  difficultyMapping.get(slider.getValue());
            System.out.println("AI depth = " + Settings.AI_DEPTH);
            Settings.FIRSTMOVE = humanFirstRadioButton.isSelected() ? Player.HUMAN : Player.AI;
        }
    }

    /**
     * First set up initial GUI
     */

    public void setup()
    {
        switch (Settings.FIRSTMOVE){
            case AI:
                main.gui.Settings.aiColour = Colour.WHITE;
                break;
            case HUMAN:
                main.gui.Settings.aiColour = Colour.BLACK;
                break;
        }

        //Set up the main board menu
        setupMenuBar();
        contentPane = new JPanel();
        checkerboardPanel = new JPanel(new GridBagLayout());
        JPanel textPanel = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        this.setContentPane(contentPane);
        contentPane.add(checkerboardPanel);
        contentPane.add(textPanel);
        textBox = new JTextArea();
        textBox.setEditable(false);
        textBox.setLineWrap(false);
        textBox.setWrapStyleWord(true);
        textBox.setAutoscrolls(true);
        textPanel.add(textBox);


        updateCheckerBoard();
        updateText("");
        this.pack();
        this.setVisible(true);
        if (Settings.FIRSTMOVE == Player.AI){
            aiMove();
        }
    }

    private void updateText(String text){
        textBox.setText(text);
    }

    /**
     * Update the game interface according to how the game is going
     */

    private void updateCheckerBoard(){
        checkerboardPanel.removeAll();
        addPieces();
        addSquares();
        addGhostButtons();
        checkerboardPanel.setVisible(true);
        checkerboardPanel.repaint();
        this.pack();
        this.setVisible(true);
    }

    //Add squares to the board including its highlight for hint mode
    private void addSquares(){
        squares = new Squares[game.getState().NO_SQUARES];
        int fromPos = -1;
        int toPos = -1;
        if(hintMove != null){
            fromPos = hintMove.getFromPos();
            toPos = hintMove.getToPos();
        }
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < game.getState().NO_SQUARES; i++){
            c.gridx = i % game.getState().SIDE_LENGTH;
            c.gridy = i / game.getState().SIDE_LENGTH;
            squares[i] = new Squares(c.gridx, c.gridy);
            if (i == fromPos){
                squares[i].setHighlighted();
            }
            if(i == toPos){
                squares[i].setHighlighted();
            }
            if (helpMoves != null){
                if (helpMoves.contains(i)){
                    squares[i].setHighlighted();
                }
            }
            checkerboardPanel.add(squares[i], c);
        }
    }


    /**
     * Add checker pieces according to the actions that have been performed during the game
     */

    private void addPieces(){
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < game.getState().NO_SQUARES; i++){
            c.gridx = i % game.getState().SIDE_LENGTH;
            c.gridy = i / game.getState().SIDE_LENGTH;
            if(game.getState().getPiece(i) != null){
                Piece piece = game.getState().getPiece(i);
                CheckerPiece button = new CheckerPiece(i, piece, this);
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        onPieceClick(actionEvent);
                    }
                });
                checkerboardPanel.add(button, c);
            }
        }
    }


    /**
     * Shows in which directions the checker could move to
     */


    private void addGhostButtons(){
        for (Board state : possibleMoves){
            int newPos = state.getToPos();
            GhostButton button = new GhostButton(state);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    onGhostButtonClick(actionEvent);
                }
            });
            squares[newPos].add(button);
        }
    }

    /**
     * Sets up the menu bar
     */

    private void setupMenuBar(){
        //Initialise variables
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Main");
        JMenuItem restartItem = new JMenuItem("Restart");
        JMenuItem undoItem = new JMenuItem("Undo move!");
        JMenuItem quitItem = new JMenuItem("Quit");
        //JMenu editMenu = new JMenu("Edit");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem rulesItem = new JMenuItem("Rules");
        JMenuItem helpItemHint = new JMenuItem("Click for a hint!");
        JMenuItem helpItemMovables = new JMenuItem("See movable pieces");

        JMenu viewMenu = new JMenu("Change mode");
        JRadioButtonMenuItem viewItemHelpMode = new JRadioButtonMenuItem("Help mode");
        JRadioButtonMenuItem viewItemHintMode = new JRadioButtonMenuItem("Hint mode");
        viewItemHelpMode.setSelected(main.gui.Settings.helpMode);
        viewItemHintMode.setSelected(main.gui.Settings.hintMode);


        //When exit menu opens it asks to confirm whether player actually wants to exit
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        onExitClick();
                    }
                }
        );

        // add action listeners
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onExitClick();
            }
        });
        restartItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onRestartClick();
            }
        });
        rulesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onRulesClick();
            }
        });
        undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onUndoClick();
            }
        });
        viewItemHelpMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onHelpModeClick();
            }
        });
        viewItemHintMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onHintModeClick();
            }
        });
        helpItemHint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                onHintClick();
            }
        });
        helpItemMovables.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                onHelpMovablesClick();
            }
        });


        //Add elements to the menu so it shows on the interface
        fileMenu.add(restartItem);
        fileMenu.add(quitItem);
        fileMenu.add(undoItem);
        viewMenu.add(viewItemHelpMode);
        viewMenu.add(viewItemHintMode);
        helpMenu.add(helpItemHint);
        helpMenu.add(helpItemMovables);
        helpMenu.add(rulesItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(viewMenu);
        this.setJMenuBar(menuBar);
    }


    /**
     * Logic of methods after clicking on them
     */


    public void onMouseRelease(int position, int dx, int dy){
        Moves feedback = game.playerMove(position, dx, dy);
        if (feedback == Moves.SUCCESS){
            updateCheckerBoard();
            aiMove();
        }
        else{
            updateCheckerBoard();
            System.out.println(feedback.toString());
        }
    }

    private void onHintClick(){
        if (!game.isGameOver()){
            AI ai = new AI(10, Player.HUMAN);
            helpMoves = null;
            hintMove = ai.move(this.game.getState(), Player.HUMAN);
            updateCheckerBoard();
        }
    }

    /**
     * Method for when player clicks on the movable pieces
     */

    private void onHelpMovablesClick(){
        hintMove = null;
        helpMoves = game.getState().getSuccessors().stream().map(x -> x.getFromPos()).collect(Collectors.toList());
        updateCheckerBoard();
    }

    /**
     * Method for when help mode is clicked on
     */

    private void onHelpModeClick(){
        main.gui.Settings.helpMode = !main.gui.Settings.helpMode;
        System.out.println("help mode: " + main.gui.Settings.helpMode);
    }

    /**
     * Method for when hint mode is clicked on
     */

    private void onHintModeClick(){
        main.gui.Settings.hintMode = !main.gui.Settings.hintMode;
        System.out.println("hint mode: " + main.gui.Settings.hintMode);
        onHintClick();
    }

    /**
     * Method for when a checker piece is clicked on
     *
     */

    private void onPieceClick(ActionEvent actionEvent){
        if(game.getTurn() == Player.HUMAN ){
            CheckerPiece button = (CheckerPiece) actionEvent.getSource();
            int pos = button.getPosition();
            if(button.getPiece().getPlayer() == Player.HUMAN){
                possibleMoves = game.getValidMoves(pos);
                updateCheckerBoard();
                if (possibleMoves.size() == 0){
                    Moves feedback = game.movesClick(pos);
                    updateText(feedback.toString());
                    if (feedback == Moves.FORCED_JUMP){
                        onHelpMovablesClick();
                    }
                }
                else{
                    updateText("");
                }
            }
        }
    }

    /**
     *  Method for when user clicks on checker piece and sees the possible options where to move the checker piece
     */

    private void onGhostButtonClick(ActionEvent actionEvent){
        if (!game.isGameOver() && game.getTurn() == Player.HUMAN){
            hintMove = null;
            helpMoves = null;
            GhostButton button = (GhostButton) actionEvent.getSource();
            game.playerMove(button.getBoardstate());
            possibleMoves = new ArrayList<>();
            updateCheckerBoard();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    aiMove();
                    if (game.isGameOver()){
                        gameOver();
                    }
                }
            });
        }
    }

    /**
    * Game over panel
    */

    //Fix!! Doesn't put space before message
    private void gameOver(){
        JOptionPane.showMessageDialog(this,
                game.getGameOverMessage(),
                "",
                JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * Performs AIMove and computes the time it takes
     *
     */

    private void aiMove(){
        long startTime = System.nanoTime();
        game.aiMove();
        long aiMoveDurationInMs = (System.nanoTime() - startTime)/1000000;
        long delayInMs = Math.max(0, main.gui.Settings.AiMinPauseDurationInMs - aiMoveDurationInMs);
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.schedule(new Runnable(){
            public void run(){
                invokeAiUpdate();
            }
        }, delayInMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Updates the game-board with relative executed moves
     */

    private void invokeAiUpdate(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateCheckerBoard();
                if (!game.isGameOver() && game.getTurn() == Player.AI){
                    aiMove();
                }
                else if (main.gui.Settings.hintMode){
                    onHintClick();
                }
            }
        });
    }

    /**
     * Window that opens when player clicks on restart
     */

    private void onRestartClick()
    {
        Object[] options = {"Yes",
                "No", };
        int n = JOptionPane.showOptionDialog(this, "Confirm that you want to restart the game.",
                "Restart game? ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == 0){
            start();
        }
    }

    /**
     * Window that opens when player clicks on exit
     */

    private void onExitClick(){
        Object[] options = {"Yes",
                "No", };
        int n = JOptionPane.showOptionDialog(this,
                "\nAre you sure you want to exit?",
                "Exit game? ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == 0){
            this.dispose();
            System.exit(0);
        }
    }

    /**
     *  Window that opens when player clicks on rules
     */

    private void onRulesClick(){

        String message =
                "1.  The checker pieces can only be moved on the black tiles and usually they can only be moved diagonally. In addition " +
                        "the disk pieces can only move forward from their initial position. <br /> <br /> " +

                        "2. The purpose of the game is to jump over the opponent's pieces and capture their disk pieces with a capturing move." +
                        "Non-capturing moves on the other hand simply allow the player to move the pieces but not capture any. <br /> <br />"+

                        "3. In a single capturing move only one piece can be captured but if the case allows it multiple jumps are allowed. <br /> <br />" +

                        "4. The jumped pieces get removed from the board" +
                        "In addition,you cannot jump your own piece or jump the same piece twice in the same move. <br /> <br />" +

                        "5. If a player has the possibility to jump and capture a piece then they MUST and will be forced to do so" +
                        "but if there is more than one capture available then the player can chose which one to prioritise. <br /> <br />" +

                        "6. When a piece reaches the last row (the King Row), it gets crowned and becomes a King. " +
                        "A piece, whether it is a king or not, can still jump a king. <br /> <br />" +

                        "7. Kings can move diagonally but also forward and backward. <br /> <br />" +

                        "8. A piece that has just been crowned king cannot continue jumping moves and must wait for the next move. <br /> <br />" +

                        "9. The players take turns moving. You can make only one move per turn. You must move and if you cannot move, you lose. <br /> <br />" +

                        "If you'd like to read the rules in depth go to: https://a4games.company/checkers-rules-and-layout/ ";

        JOptionPane.showMessageDialog(this,
                "<html><body><p style='width: 400px;'>"+message+"</p></body></html>",
                "",
                JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * Undo the last move
     */

    private void onUndoClick(){
        game.undo();
        updateCheckerBoard();
        if (main.gui.Settings.hintMode){
            onHintClick();
        }
    }
}

