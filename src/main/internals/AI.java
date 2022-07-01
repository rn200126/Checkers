package main.internals;
import java.util.Random;
import java.util.ArrayList;

/**
 *
 * Computer class which represents the AI logic that the player interacts with
 * Contains the MiniMax algorithm with Alpha-Beta pruning
 * @author 220882
 * @version V.1
 *
 */

public class AI {

    private int depth;
    private Player player;

    public AI(){
        depth = Settings.AI_DEPTH;  // Specifies the depth until where the AI will search in minimax
        player = Player.AI;  // Specifies the player for which the AI searches
    }

    public AI(int depth, Player player){
        this.depth = depth;
        this.player = player;
    }

    //Need to change up Board successors
    public Board move(Board state, Player player){
        if (state.getTurn() == player){
            ArrayList<Board> successors = state.getSuccessors();
            return minimaxMove(successors);
        }
        else{
            throw new RuntimeException("Cannot generate moves for player if it's not their turn");
        }
    }

    /**
     * Selects the next best move from a list of collected best moves by using the minimax algorithm
     * @param nextBest
     * @return
     */

    private Board minimaxMove(ArrayList<Board> nextBest){
        if (nextBest.size() == 1){
            return nextBest.get(0);
        }
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Board> equalBests = new ArrayList<>();
        for (Board next : nextBest){
            int val = minimax(next, this.depth);
            if (val > bestScore){
                bestScore = val;
                equalBests.clear();
            }
            if (val == bestScore){
                equalBests.add(next);
            }
        }
        if(equalBests.size() > 1){
            System.out.println(player.toString() + " is selecting the best move");
        }
        return randomMove(equalBests);
    }

    /**
     * Selects the next state randomly
     * @param nextBest
     * @return
     */

    private Board randomMove(ArrayList<Board> nextBest){
        if (nextBest.size() < 1){
            //Make sure that the list isn't empty so player can actually make the random choice
            throw new RuntimeException("Can't choose from empty list.");
        }
        Random rand = new Random();
        int i = rand.nextInt(nextBest.size());
        return nextBest.get(i);
    }


    /**
     * Initialise Alpha and Beta
     * @param node
     * @param depth
     * @return minimax score with its associated variables
     */

    private int minimax(Board node, int depth){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        return minimax(node, depth, alpha, beta);
    }

    /**
     * Makes use of the Minimax algorithm with alpha-beta pruning
     * MAX player is the player (tries to maximise value)
     * MIN player is the opponent (tries to minimise value)
     * @param node
     * @param depth
     * @param alpha
     * @param beta
     * @return v value
     */

    // in BOARD change getSuccessor to getNextBest?

    private int minimax(Board node, int depth, int alpha, int beta){
        if (depth == 0 || node.isGameOver()){
            return node.computeHeuristic(this.player);
        }
        if (node.getTurn() == player){
            int v = Integer.MIN_VALUE;
            for (Board child : node.getSuccessors()){
                v = Math.max(v, minimax(child, depth-1, alpha, beta));
                alpha = Math.max(alpha, v);
                if (alpha >= beta){
                    break;
                }
            }
            return v;
        }
        if (node.getTurn() == player.getOpposite()){
            int v = Integer.MAX_VALUE;
            for (Board child : node.getSuccessors()){
                v = Math.min(v,minimax(child, depth-1, alpha, beta));
                beta = Math.min(beta, v);
                if (alpha >= beta){
                    break;
                }
            }
            return v;
        }
        throw new RuntimeException("There has been an error!");
    }
}
