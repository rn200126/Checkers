package main.internals;
import java.util.Stack;
import java.util.ArrayList;


/**
 *
 * Game class to check for the available moves forced and non-forced moves and moves the pieces accordingly
 * @author 220882
 * @version V.1
 *
 */

public class Game{

    private Stack<Board> state;
    private AI ai;
    private int memory;
    private boolean humanWon;

    //Starts game from beginning
    public Game(){
        memory = Settings.UNDO_MEMORY;
        state = new Stack<>();
        state.push(Board.StateOne());
        ai = new AI();
    }

    public void playerMove(Board newState){
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN){
            updateState(newState);
        }
    }

    /**
     *
     * Checks for valid moves that the player can make
     * Checks capturing and non-capturing moves
     * @param fromPos original position
     * @param dx dx coordinate
     * @param dy dy coordinate
     */

    public Moves playerMove(int fromPos, int dx, int dy){
        int toPos = fromPos + dx + Board.SIDE_LENGTH*dy;
        if (toPos > getState().state.length){
            return Moves.NOT_ON_BOARD;
        }

        //Checks forced capture movements
        ArrayList<Board> jumpSuccessors = this.state.peek().getSuccessors(true);
        boolean jumps = jumpSuccessors.size() > 0;
        if (jumps){
            for (Board succ : jumpSuccessors){
                if (succ.getFromPos() == fromPos && succ.getToPos() == toPos){
                    updateState(succ);
                    return Moves.SUCCESS;
                }
            }
            return Moves.FORCED_JUMP;
        }
        //Checks for diagonal moves
        if (Math.abs(dx) != Math.abs(dy)){
            return Moves.NOT_DIAGONAL;
        }
        //Checks if piece is trying to move on another piece
        if (this.getState().state[toPos] != null){
            return Moves.NO_FREE_SPACE;
        }
        //Checks for non-capturing moves
        ArrayList<Board> nonJumpSuccessors = this.state.peek().getSuccessors(fromPos, false);
        for (Board succ : nonJumpSuccessors){
            if (succ.getFromPos() == fromPos && succ.getToPos() == toPos){
                updateState(succ);
                return Moves.SUCCESS;
            }
        }    if (dy > 1){
            //Reminder that only kings can move backwards
            return Moves.NO_BACKWARD_MOVES_FOR_SINGLES;
        }
        if (Math.abs(dx)== 2){
            //Reminder that you can only make one move unless double move
            return Moves.ONLY_SINGLE_DIAGONALS;
        }
        //The move was not valid
        return Moves.UNKNOWN_INVALID;
    }


    public Moves movesClick(int pos){
        ArrayList<Board> jumpSuccessors = this.state.peek().getSuccessors(true);
        if (jumpSuccessors.size() > 0){
            return Moves.FORCED_JUMP;
        }
        else{
            return Moves.PIECE_BLOCKED;
        }

    }


    /**
     *
     * Get valid moves from the Successors list
     * @param pos position
     *
     */

    public ArrayList<Board> getValidMoves(int pos) {

        return state.peek().getSuccessors(pos);
    }

    /**
     *
     * Changes the state of the board according to the AI moves
     *
     */

    public void aiMove(){
        if (!isGameOver() && state.peek().getTurn() == Player.AI){
            Board newState = ai.move(this.state.peek(), Player.AI);
            updateState(newState);
        }
    }

    /**
     *
     * Updates state of the board
     * @param newState new state
     *
     */

    private void updateState(Board newState){
        state.push(newState);
        if(state.size() > memory){
            state.remove(0);
        }
    }

    //Gets State
    public Board getState() {

        return state.peek();
    }


    //Gets Turn
    public Player getTurn() {

        return state.peek().getTurn();
    }

    //Checks if the game over by checking scores of AI and Human player
    public boolean isGameOver(){
        boolean isOver = state.peek().isGameOver();
        if (isOver){
            // get win / lose status
            humanWon = state.peek().checksCount.get(Player.AI) == 0;
        }
        return isOver;
    }

    //Prints out message when the game is won or lost
    public String getGameOverMessage(){
        String result = "Game Over ";
        if (humanWon == true){
            result += "Congrats! You have won!";
        }
        else{
            result += "Sorry, you lost! ";
        }
        return result;
    }

    //Lets the player undo movements
    public void undo(){
        if (state.size() > 2){
            state.pop();
            while(state.peek().getTurn() == Player.AI){
                state.pop();
            }
        }
    }

}
