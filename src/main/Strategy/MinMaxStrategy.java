package main.Strategy;

import main.*;

import java.util.LinkedHashSet;

/**
 * A strategy that uses MinMax to find the best spot
 */
public class MinMaxStrategy extends Strategy {

    private static final int DEFAULT_SEARCH_DEPTH = 3; // <= 3 if for all moves

    private int searchDepth;

    public MinMaxStrategy(Board board) {
        super(board);
        this.searchDepth = DEFAULT_SEARCH_DEPTH;
    }

    @Override
    public Spot getMove(Flippo color) {
        Spot bestSpot = null;
        int bestScore = Integer.MIN_VALUE;

        System.err.println("There are " + board.getValidPlacementSpots(color).size() + " possible spots");
        for (Spot spot : board.getValidPlacementSpots(color)) {
            Move move = new Move(board, spot, color);
            move.execute();
            int posScore = getRecursiveScore(color.opposite(), searchDepth - 1).get(color);
            if (posScore > bestScore) {
                bestScore = posScore;
                bestSpot = spot;
            }
            move.undo();
        }
        System.err.println("Found best spot with score " + bestScore);

        return bestSpot;
    }

    /**
     * Get the MinMax best score for this color, looking a certain depth from here
     */
    private Score getRecursiveScore(Flippo color, int depth) {
        if (depth <= 0) {
            return board.getScore();
        } else {
            LinkedHashSet<Spot> validPlacementSpots = board.getValidPlacementSpots(color);
            if (validPlacementSpots.size() <= 0) {
                return board.getScore();
            }

            Score bestScore = new Score(Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (Spot spot : validPlacementSpots) {
                Move move = new Move(board, spot, color);
                move.execute();
                Score posScore = getRecursiveScore(color.opposite(), depth - 1);
                if (posScore.get(color) > bestScore.get(color)) {
                    bestScore = posScore;
                }
                move.undo();
            }

            return bestScore;
        }
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
}
