package main.Switcher;

import main.Strategy.Strategy;

/**
 * Abstract class used for classes that can decide which strategy to run on each turn
 */
public abstract class Switcher {

    public abstract Class<? extends Strategy> getStrategy ();

}
