package main.Strategy;

import main.Board;
import main.Spot;
import main.Flippo;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for a strategy that can decide on which move to do
 */
public abstract class Strategy {

    public static final Class<? extends Strategy> DEFAULT_STRATEGY = MinMaxStrategy.class;

    protected Board board;

    public static final List<Class<? extends Strategy>> STRATEGIES = Arrays.asList(
            RandomStrategy.class,
            FirstPossibleStrategy.class,
            MinMaxStrategy.class,
            MaxImmediateScoreStrategy.class
    );

    public Strategy(Board board) {
        this.board = board;
    }

    public abstract Spot getMove(Flippo color);

}
