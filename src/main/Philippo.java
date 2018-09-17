package main;

import main.Strategy.StrategyCollection;

import java.util.Scanner;

/**
 * The main function that will run all other classes. Organizer
 */
class Philippo {

    private Board board;
    private StrategyCollection strategyCollection;

    public Philippo() {
        this.board = new Board();
        this.strategyCollection = new StrategyCollection(board);
    }

    public void runContest() {
        System.err.println("Philippo running contest");
        // TODO NB: The below is just bodge to test
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        boolean white = line.equals("Start");
        Flippo color = white ? Flippo.WHITE : Flippo.BLACK;
        if (! white) {
            board.doMove(line, color.opposite());
        }
        for (int i = 0; i < 29; i ++) {
            Spot move = strategyCollection.getMove();
            System.out.println(move.getStringRepresentation());
            System.out.flush();
            board.doMove(move, color);
            board.printBoard();
            board.doMove(sc.nextLine(), color.opposite());
        }
        Spot move = strategyCollection.getMove();
        System.out.println(move.getStringRepresentation());
        System.out.flush();
        board.doMove(move, color);
        if (white) {
            board.printBoard();
            board.doMove(sc.nextLine(), color.opposite());
        }
        // TODO NB: The above section is full bodge just to test
    }

    public static void main(String[] args) {
        System.err.println("Philippo main is called");
        new Philippo().runContest();
    }

}
