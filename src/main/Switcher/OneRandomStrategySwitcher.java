package main.Switcher;

import main.Strategy.Strategy;

import java.util.Random;

/**
 * Switches to one random strategy at start and then uses that strategy for
 * the entire game
 */
public class OneRandomStrategySwitcher extends Switcher {

    private Random random;
    private Class<? extends Strategy> strategy;

    public OneRandomStrategySwitcher() {
        random = new Random();
        strategy = Strategy.STRATEGIES.get(random.nextInt(Strategy.STRATEGIES.size()));
    }

    @Override
    public Class<? extends Strategy> getStrategy() {
        return strategy;
    }
}
