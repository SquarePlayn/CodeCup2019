package main;

import java.util.LinkedHashSet;

/**
 * Stores the act of doing a move
 */
public class Move {

    private Board board;
    private Spot spot;
    private Flippo placeColor;

    private LinkedHashSet<Spot> flipped;
    private boolean executed;

    /**
     * Initiate the move if it is already executed
     */
    public Move(Board board, Spot spot, Flippo placeColor, LinkedHashSet<Spot> flipped) {
        this.board = board;
        this.spot = spot;
        this.placeColor = placeColor;
        this.flipped = flipped;
        this.executed = true;
    }

    /**
     * Initiate the move if it is not yet executed
     */
    public Move(Board board, Spot spot, Flippo placeColor) {
        this.board = board;
        this.spot = spot;
        this.placeColor = placeColor;
        this.executed = false;
    }

    /**
     * Executes this move
     */
    public boolean execute() {
        if (executed) {
            System.err.println("Tried to execute already executed move");
            return false;
        } else {
            flipped = board.doMove(spot, placeColor);
            executed = true;
            return true;
        }
    }


    /**
     * Undoes this move
     */
    public boolean undo() {
        if (! executed) {
            System.err.println("Tried to undo a move that is not yet executed");
            return false;
        } else {
            flipped.forEach(Spot::flip);
            spot.setFlippo(Flippo.NONE);
            executed = false;
            return true;
        }
    }

    public Board getBoard() {
        return board;
    }

    public Spot getSpot() {
        return spot;
    }

    public Flippo getPlaceColor() {
        return placeColor;
    }


    public LinkedHashSet<Spot> getFlipped() {
        if (executed) {
            return flipped;
        } else {
            return new LinkedHashSet<>();
        }
    }
}
