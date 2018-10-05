package main.Strategy;

import main.Board;
import main.Flippo;
import main.Spot;

/**
 * A strategy that picks the spot that results in the state with the highest score
 */
public class MaxImmediateScoreStrategy extends MinMaxStrategy {

    public MaxImmediateScoreStrategy(Board board) {
        super(board);
        super.setSearchDepth(1);
    }

    @Override
    public Spot getMove(Flippo color) {
        return super.getMove(color);
    }
}
