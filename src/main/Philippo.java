package main;

import main.Strategy.StrategyCollection;

class Philippo {

    private Board board;
    private StrategyCollection strategyCollection;

    public Philippo() {
        this.board = new Board();
        this.strategyCollection = new StrategyCollection(board);
    }

    public void runContest() {
        System.err.println("Philippo running contest");
        // TODO
    }

    public static void main(String[] args) {
        System.err.println("Philippo main is called");
        new Philippo().runContest();
    }

}
