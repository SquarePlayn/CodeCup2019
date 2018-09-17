package main.Strategy;

import main.Board;
import main.Spot;

import java.util.ArrayList;
import java.util.Random;

/**
 * A strategy that takes a random spot out of all possible placement spots
 */
public class RandomStrategy extends Strategy {

    private Board board;
    private Random random;

    public RandomStrategy(Board board) {
        this.board = board;
        this.random = new Random();
    }

    @Override
    public Spot getMove() {
        ArrayList<Spot> spots = new ArrayList<>(board.getValidPlacementSpots());
        return spots.get(random.nextInt(spots.size()));
    }

}
