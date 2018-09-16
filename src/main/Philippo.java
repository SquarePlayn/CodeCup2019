package main;

import main.Strategy.StrategyCollection;

import java.util.Scanner;

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
        System.err.println(board.getSpot(2, 5).getStringRepresentation());
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        boolean white = line.equals("Start");
        if (! white) {
            board.getSpot(line).setFlippo(Flippo.BLACK);
        }
        for (int i = 0; i < 29; i ++) {
            Spot move = strategyCollection.getMove();
            System.out.println(move.getStringRepresentation());
            System.out.flush();
            move.setFlippo(Flippo.WHITE);
            board.getSpot(sc.nextLine()).setFlippo(Flippo.BLACK);
        }
        Spot move = strategyCollection.getMove();
        System.out.println(move.getStringRepresentation());
        System.out.flush();
        move.setFlippo(Flippo.WHITE);
        if (white) {
            board.getSpot(sc.nextLine()).setFlippo(Flippo.BLACK);
        }

    }

    public static void main(String[] args) {
        System.err.println("Philippo main is called");
        new Philippo().runContest();
    }

}
