package main;

import java.util.*;

public class Board {
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Spot[][] board;
    private LinkedHashSet<Spot> spotSet;

    public Board() {
        this.board = new Spot[WIDTH][HEIGHT];
        this.spotSet = null;

        // Initialize the board with no flippos
        for (int x = 0; x < WIDTH; x ++) {
            for (int y = 0; y < HEIGHT; y ++) {
                board[x][y] = new Spot(x, y);
            }
        }

        // Set the boards neighbours
        for (int x = 0; x < WIDTH; x ++) {
            for (int y = 0; y < HEIGHT; y ++) {
                if (x < WIDTH - 1) {
                    if (y > 0) {
                        board[x][y].setNeighbour(Direction.NORTHEAST, board[x+1][y-1]);
                        board[x+1][y-1].setNeighbour(Direction.SOUTHWEST, board[x][y]);
                    }
                    board[x][y].setNeighbour(Direction.EAST, board[x+1][y]);
                    board[x+1][y].setNeighbour(Direction.WEST, board[x][y]);
                    if (y < HEIGHT - 1) {
                        board[x][y].setNeighbour(Direction.SOUTHEAST, board[x+1][y+1]);
                        board[x+1][y+1].setNeighbour(Direction.NORTHWEST, board[x][y]);
                    }
                }
                if (y < HEIGHT - 1) {
                    board[x][y].setNeighbour(Direction.SOUTH, board[x][y+1]);
                    board[x][y+1].setNeighbour(Direction.NORTH, board[x][y]);
                }
            }
        }

        // Construct the starting center
        board[3][3].setFlippo(Flippo.WHITE);
        board[4][3].setFlippo(Flippo.BLACK);
        board[4][4].setFlippo(Flippo.WHITE);
        board[3][4].setFlippo(Flippo.BLACK);
    }

    public Spot[][] getBoard() {
        return board;
    }

    /**
     * Returns the set of all spots that are empty
     */
    public LinkedHashSet<Spot> getEmptySpots() {
        LinkedHashSet<Spot> emptySpots = new LinkedHashSet<>(getSpotSet());
        emptySpots.removeIf(Spot::isEmpty);
        return emptySpots;
    }

    public LinkedHashSet<Spot> getValidPlacementSpots() {
        LinkedHashSet<Spot> validPlacementSpots = new LinkedHashSet<>(getSpotSet());
        validPlacementSpots.removeIf(i -> ! i.isValidPlacementSpot());
        return validPlacementSpots;
    }

    /**
     * Return all spots on the board as a set
     */
    public LinkedHashSet<Spot> getSpotSet() {
        if (spotSet == null) {
            spotSet = new LinkedHashSet<>();
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    spotSet.add(board[x][y]);
                }
            }
        }
        return spotSet;
    }

    /**
     * Checks whether a given x and y lie on the board
     * 0 <= x < WIDTH
     * 0 <= y < HEIGHT
     */
    public boolean areValidCoordinates(int x, int y) {
        return (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT);
    }
}
