package main.Strategy;

import main.Board;
import main.Flippo;
import main.Spot;

/**
 * A strategy that picks the first possible spot to place its  Flippo
 */
public class FirstPossibleStrategy extends Strategy {

    public FirstPossibleStrategy(Board board) {
        super(board);
    }

    @Override
    public Spot getMove(Flippo color) {
        return board.getValidPlacementSpots(color).iterator().next();
    }
}
