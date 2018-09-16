package main.Strategy;

import main.Board;
import main.Spot;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class Strategy {

    public static final Class<? extends Strategy> DEFAULT_STRATEGY = RandomStrategy.class;

    public abstract Spot getMove();

}
