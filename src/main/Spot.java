package main;

import java.util.Collection;
import java.util.HashMap;

public class Spot {

    private int x;
    private int y;
    private Flippo flippo;
    private HashMap<Direction, Spot> neighbours;

    public Spot(int x, int y) {
        this.x = x;
        this.y = y;
        this.flippo = Flippo.NONE;
        this.neighbours = new HashMap<>();
    }

    /**
     * Returns whether a spot is valid to place a piece on
     * A spot is valid when it is empty and has a neighbouring piece
     */
    public boolean isValidPlacementSpot() {
        return isEmpty() && ! getNeighbours().stream().allMatch(Spot::isEmpty);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Flippo getFlippo() {
        return flippo;
    }

    public void setFlippo(Flippo flippo) {
        this.flippo = flippo;
    }

    public boolean isEmpty() {
        return flippo == Flippo.NONE;
    }

    public Collection<Spot> getNeighbours() {
        return neighbours.values();
    }

    public Spot getNeighbour(Direction direction) {
        return neighbours.getOrDefault(direction, null);
    }

    public void setNeighbour(Direction direction, Spot spot) {
        neighbours.put(direction, spot);
    }

}
