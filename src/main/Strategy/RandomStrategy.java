package main.Strategy;

import main.Board;
import main.Spot;

public class RandomStrategy extends Strategy {

    private Board board;

    public RandomStrategy(Board board) {
        this.board = board;
    }

    @Override
    public Spot getMove() {
        return board.getValidPlacementSpots().iterator().next();
    }
}
