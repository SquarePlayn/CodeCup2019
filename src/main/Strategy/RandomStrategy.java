package main.Strategy;

import main.Board;
import main.Flippo;
import main.Spot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A strategy that takes a random spot out of all possible placement spots
 */
public class RandomStrategy extends Strategy {

    private Board board;

    public RandomStrategy(Board board) {
        this.board = board;
    }

    @Override
    public Spot getMove(Flippo color) {
        ArrayList<Spot> spots = new ArrayList<>(board.getValidPlacementSpots(color));
        Collections.shuffle(spots);
        return spots.get(0);
    }

}
