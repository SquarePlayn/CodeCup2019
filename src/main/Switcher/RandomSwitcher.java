package main.Switcher;

import main.Strategy.Strategy;

import java.util.Random;

/**
 * Switches to a different random Strategy each turn
 */
public class RandomSwitcher extends Switcher {

    private Random random;

    public RandomSwitcher() {
        this.random = new Random();
    }

    @Override
    public Class<? extends Strategy> getStrategy() {
        return Strategy.STRATEGIES.get(random.nextInt(Strategy.STRATEGIES.size()));
    }
}
