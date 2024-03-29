package main.Strategy;

import main.Board;
import main.Flippo;
import main.Spot;

import java.util.HashMap;

/**
 * Strategy that is a wrapper around multiple strategies.
 * Can be used to swap between strategies
 * Makes sure only one instance of each strategy is created
 */
public class StrategyCollection extends Strategy {

    private HashMap<Class<? extends Strategy>, Strategy> strategyInstances;

    private Class<? extends Strategy> strategy;

    public StrategyCollection(Board board) {
        super(board);
        this.strategyInstances = new HashMap<>();
        this.strategy = Strategy.DEFAULT_STRATEGY;
    }

    /**
     * Get a strategy by its class. Makes sure always max one is created
     */
    private Strategy getStrategyInstance() {
        if (strategyInstances.containsKey(strategy)) {
            return strategyInstances.get(strategy);
        } else {
            try {
                Strategy strategyInstance = strategy.getConstructor(Board.class).newInstance(board);
                strategyInstances.put(strategy, strategyInstance);
                return strategyInstance;
            } catch (Exception e) {
                System.err.println("Exception when initiating strategy with only board argument");
                e.printStackTrace();
                return null;
            }
        }
    }

    public Class<? extends Strategy> getStrategy() {
        return strategy;
    }

    public void setStrategy(Class<? extends Strategy> strategy) {
        this.strategy = strategy;
    }

    @Override
    public Spot getMove(Flippo color) {
        return getStrategyInstance().getMove(color);
    }

}
