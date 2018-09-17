package main.Strategy;

import main.Spot;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for a strategy that can decide on which move to do
 */
public abstract class Strategy {

    public static final Class<? extends Strategy> DEFAULT_STRATEGY = RandomStrategy.class;

    public static final List<Class<? extends Strategy>> STRATEGIES = Arrays.asList(
            RandomStrategy.class,
            FirstPossibleStrategy.class
    );

    public abstract Spot getMove();

}
