package main.internals;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

//Need to put in the author, version etc
/**
 *
 * Board class that represents the board on which the game is being played
 * It also includes the logic for the pieces that the players will use to play the game
 * It is a 8x8 grid with 32 black squares belonging to each player's area
 * @author 220882
 * @version V.1
 *
 */

public class Board {

    //First declare needed variables for the different methods being used later
    //Change Pieces
    //Makes use of HashMaps to update and count the player's pieces (checkers and king pieces) in the game
    public HashMap<Player, Integer> checksCount;
    private HashMap<Player, Integer> kingCount;

    //8x8 Board representation
    public static final int SIDE_LENGTH = 8;
    public static final int NO_SQUARES = SIDE_LENGTH*SIDE_LENGTH;
    //Might have to edit Piece and then edit Piece here !!
    Piece[] state;

    //Records the player's turns
    private Player turn;
    //Position from which the most recent or current move started
    private int fromPos = -1;
    //Position where the most recent or current move ended
    private int toPos = -1;
    //Position from where the Double Jump move started
    private int doubleJumpPos = -1;

    /**
     * Initialises the board state.
     *
     */

    public Board(){
        state = new Piece[Board.NO_SQUARES];
    }

    //Initial board state
    //Makes sure that checkers pieces are placed on the black squares
    //Puts the AI checker pieces in the first 3 rows and the player's in the last 3 rows
    public static Board StateOne(){
        Board bstate = new Board();
        bstate.turn = Settings.FIRSTMOVE; //Change this or delete !!!
        for (int i = 0; i < bstate.state.length; i++){
            int y = i/SIDE_LENGTH;
            int x = i % SIDE_LENGTH;
            //Puts checker pieces on black squares!
            if ((x + y) % 2 == 1 ){
                //Puts AI's checker pieces in first 3 rows
                //Try changing this up and put it other way around
                if (y < 3){
                    bstate.state[i] = new Piece(Player.AI, false);
                }
                //Puts Human player's checker pieces in first 3 rows
                else if (y  > 4){
                    bstate.state[i] = new Piece(Player.HUMAN, false);
                }
            }
        }

        //Counters for the declared variables until now
        //Note to self, change and reduce lines
        int computerCount = (int) Arrays.stream(bstate.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.AI).count();
        int humanCount = (int) Arrays.stream(bstate.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.HUMAN).count();
        bstate.checksCount = new HashMap<>();
        bstate.checksCount.put(Player.AI, computerCount);
        bstate.checksCount.put(Player.HUMAN,humanCount);
        bstate.kingCount = new HashMap<>();
        bstate.kingCount.put(Player.AI, 0);
        bstate.kingCount.put(Player.HUMAN, 0);
        return bstate;
    }

    //Deep copy of board since original board will be subject to change while playing
    private Board deepCopy(){
        Board bstate = new Board();
        System.arraycopy(this.state, 0, bstate.state, 0, bstate.state.length);
        return bstate;
    }

    /**
     * Compute heuristic to indicate best possible successor states for the players
     * @param player
     * @return
     */

    public int computeHeuristic(Player player){
        switch (Settings.HEURISTIC){
            case 1:
                return heuristic1(player);
            case 2:
                return heuristic2(player);
        }
        throw new RuntimeException("Invalid heuristics");
    }

    private int heuristic1(Player player){
        //Maximum value for winning
        if (this.checksCount.get(player.getOpposite()) == 0){
            return Integer.MAX_VALUE;
        }
        //Minimum value for losing
        if (this.checksCount.get(player) == 0){
            return Integer.MIN_VALUE;
        }
        //Returns the difference between checkers piece counts with the kings
        return checksScore(player) - checksScore(player.getOpposite());
    }


    private int heuristic2(Player player){
        if (this.checksCount.get(player.getOpposite()) == 0){
            return Integer.MAX_VALUE;
        }
        else if (this.checksCount.get(player) == 0){
            return Integer.MIN_VALUE;
        }
        else{
            return checksScore(player)/checksScore(player.getOpposite());
        }
    }

    //Score of each player including the number of checker pieces and king pieces
    private int checksScore(Player player){

        return this.checksCount.get(player) + this.kingCount.get(player);
    }


    /**
     * Check if this is right???
     * Makes sure that there are only valid successor states for each player
     * @return successors
     */

    //Successor function to generate moves

    public ArrayList<Board> getSuccessors(){
        //Compute jump successors and check if there are any compulsory moves
        ArrayList<Board> successors = getSuccessors(true);
        if (Settings.FORCEDMOVES){
            if (successors.size() > 0){
                //Return forced moves if available
                return  successors;
            }
            else{
                //If there's no jumps available then return that there are no jumps
                return getSuccessors(false);
            }
        }
        else{
            //Return all successors
            successors.addAll(getSuccessors(false));
            return successors;
        }
    }


    /**
     * Get valid jump or non-jump successor states for a player
     * @param jump
     * @return result
     */

    public ArrayList<Board> getSuccessors(boolean jump){
        ArrayList<Board> result = new ArrayList<>();
        for (int i = 0; i < this.state.length; i++){
            if (state[i] != null){
                if(state[i].getPlayer() == turn){
                    result.addAll(getSuccessors(i, jump));
                }
            }
        }
        return result;
    }

    /**
     *
     * Gets valid successor states for a specific position on the board
     * @param position
     * @return result
     */

    public ArrayList<Board> getSuccessors(int position){
        if (Settings.FORCEDMOVES){
            //Computes jump successors globally
            ArrayList<Board> jumps = getSuccessors(true);
            if (jumps.size() > 0){
                //Returns forced jumps at a certain position
                return getSuccessors(position, true);
            }
            else{
                //Returns non-jumps successors at a certain position - if there are no jumps available
                return getSuccessors(position, false);
            }
        }
        else{
            //Returns all successors
            ArrayList<Board> result = new ArrayList<>();
            result.addAll(getSuccessors(position, true));
            result.addAll(getSuccessors(position, false));
            return result;
        }
    }

    /**
     *
     * Get valid jump or non-jump successor states for a specific piece on the board.
     *
     * @param position
     * @return successors
     */

    public ArrayList<Board> getSuccessors(int position, boolean jump){

        if (this.getPiece(position).getPlayer() != turn){
            throw new IllegalArgumentException("The chosen piece is not in that position");
        }
        Piece piece = this.state[position];
        if(jump){
            //Piece variable is to specify which piece is needed and position is for the specific position in which the specific piece will be
            return jumpSuccessors(piece, position);
        }
        else{
            return nonJumpSuccessors(piece, position);
        }
    }

    /**
     * Gets valid non-jump moves at a given position for a given piece
     * @param piece
     * @param position
     * @return result
     */

    private ArrayList<Board> nonJumpSuccessors(Piece piece, int position){
        ArrayList<Board> result = new ArrayList<>();
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        //For loop to check in which directions it is possible to move
        for (int dx : piece.getXMovements()){
            for (int dy : piece.getYMovements()){
                int newX = x + dx;
                int newY = y + dy;
                //Check if the new position is valid
                if (isValid(newY, newX)) {
                    //Check if the new position is available
                    if (getPiece(newY, newX) == null) {
                        int newPos = SIDE_LENGTH*newY + newX;
                        result.add(createNewState(position, newPos, piece, false, dy,dx));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets valid jump moves at a given position for a given piece
     * @param piece
     * @param position
     * @return result
     */

    private ArrayList<Board> jumpSuccessors(Piece piece, int position){
        ArrayList<Board> result = new ArrayList<>();
        if (doubleJumpPos > 0 && position != doubleJumpPos){
            return result;
        }
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        //For loop to check in which directions it is possible to move
        for (int dx : piece.getXMovements()){
            for (int dy : piece.getYMovements()){
                int newX = x + dx;
                int newY = y + dy;
                //Check if new position is valid
                if (isValid(newY, newX)) {
                    //Check if the new position has the rival player
                    if (getPiece(newY,newX) != null && getPiece(newY, newX).getPlayer() == piece.getPlayer().getOpposite()){
                        newX = newX + dx; newY = newY + dy;
                        //Check if the new position player want to jump to is valid
                        if (isValid(newY, newX)){
                            //Check if the new position is available
                            if (getPiece(newY,newX) == null) {
                                int newPos = SIDE_LENGTH*newY + newX;
                                result.add(createNewState(position, newPos, piece, true, dy, dx));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * Update board with the new state and its updated elements
     * Removes captured checkers
     * @param oldPos old position
     * @param newPos new position
     * @param piece piece in the game
     * @param jumped piece has jumped
     * @param dy dy
     * @param dx dx
     * @return result
     */

    private Board createNewState(int oldPos, int newPos, Piece piece, boolean jumped, int dy, int dx){
        Board result = this.deepCopy();
        result.checksCount = new HashMap<>(checksCount);
        result.kingCount = new HashMap<>(kingCount);
        //Check if there is a king
        boolean kingConversion = false;
        if (isKingPosition(newPos, piece.getPlayer())){
            piece = new Piece(piece.getPlayer(), true);
            kingConversion = true;
            //Add amount of king to the king count
            result.kingCount.replace(piece.getPlayer(), result.kingCount.get(piece.getPlayer()) + 1);
        }
        //Move pieces and store data
        result.state[oldPos] = null;
        result.state[newPos] = piece;
        result.fromPos = oldPos;
        result.toPos = newPos;
        Player oppPlayer = piece.getPlayer().getOpposite();
        result.turn = oppPlayer;

        if (jumped){
            //Remove captured piece
            result.state[newPos - SIDE_LENGTH*dy - dx] = null;
            result.checksCount.replace(oppPlayer, result.checksCount.get(oppPlayer) - 1);
            //If it is possible to jump then allow to jump unless checker piece just became a king
            if (result.jumpSuccessors(piece, newPos).size() > 0 && kingConversion == false){
                //The turn doesn't change in this case
                result.turn = piece.getPlayer();
                //Double Jump option
                result.doubleJumpPos = newPos;
            }
        }
        return result;
    }

    //Check if and which player is in the position to become king
    private boolean isKingPosition(int pos, Player player){
        int y = pos / SIDE_LENGTH;
        if (y == 0 && player == Player.HUMAN){
            return true;
        }
        else return y == SIDE_LENGTH - 1 && player == Player.AI;
    }


    /**
     *
     * Gets the position from where checker piece made its move
     * @return
     */

    public int getFromPos(){

        return this.fromPos;
    }

    /**
     *
     * Gets the position of where the checker moved to
     * @return
     */

    public int getToPos(){

        return this.toPos;
    }

    /**
     * Gets which player's turn it is
     * @return
     */

    public Player getTurn() {

        return turn;
    }

    /**
     *
     * Checks the state of the game to see who won the game between the two players
     * @return
     */

    //Checks the final scores - can change this name
    public boolean isGameOver(){

        return (checksCount.get(Player.AI) == 0 || checksCount.get(Player.HUMAN) == 0);
    }

    /**
     *
     * Gets player's checker piece at a given position
     * @param i position on the board
     * @return state
     */

    public Piece getPiece(int i){

        return state[i];
    }

    /**
     * Get piece at a certain position based on the coordinates of the board
     * @param y
     * @param x
     * @return
     */
    private Piece getPiece(int y, int x){

        return getPiece(SIDE_LENGTH*y + x);
    }

    //Unsure how to make this better
    /**
     * Check if the indices in the board are valid
     * @param y
     * @param x
     * @return
     */
    private boolean isValid(int y, int x){

        return (0 <= y) && (y < SIDE_LENGTH) && (0 <= x) && (x < SIDE_LENGTH);
    }

}
