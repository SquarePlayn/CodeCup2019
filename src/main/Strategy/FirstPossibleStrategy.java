package main.Strategy;

import main.Board;
import main.Spot;

/**
 * A strategy that picks the first possible spot to place its  Flippo
 */
public class FirstPossibleStrategy extends Strategy {

    private Board board;

    public FirstPossibleStrategy(Board board) {
        this.board = board;
    }

    @Override
    public Spot getMove() {
        return board.getValidPlacementSpots().iterator().next();
    }
}
