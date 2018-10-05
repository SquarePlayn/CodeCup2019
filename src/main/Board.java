package main;

import java.util.*;

/**
 * Representation of the playboard of WIDTH x HEIGHT spots
 */
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

    /**
     * Get all spots where a player of specified color would be allowed to place one
     */
    public LinkedHashSet<Spot> getValidPlacementSpots(Flippo color) {
        LinkedHashSet<Spot> validPlacementSpots = new LinkedHashSet<>(getSpotSet());
        validPlacementSpots.removeIf(i -> ! i.isValidPlacementSpot());
        boolean canFlipSome = validPlacementSpots.stream().anyMatch(i -> getMoveSpots(i, color).size() > 0);
        if (canFlipSome) {
            validPlacementSpots.removeIf(i -> getMoveSpots(i, color).size() == 0);
        }

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

    /**
     * Receive a spot on the board using its string representation
     */
    public Spot getSpot(String string) {
        if (string.length() < 2) {
            return null;
        } else {
            int x = string.charAt(1) - '1';
            int y = string.charAt(0) - 'A';
            return getSpot(x, y);
        }
    }

    /**
     * Get the score of the current state of the board
     */
    public Score getScore() {
        /*Score score = new Score(-2, -2);
        for (Spot spot : getSpotSet()) {
            Flippo spotColor = spot.getFlippo();
            if (spotColor != Flippo.NONE) {
                score.put(spotColor, score.get(spotColor) + 1);
            }
        }*/

        int black = -2;
        int white = -2;
        for (Spot spot : getSpotSet()) {
            Flippo spotColor = spot.getFlippo();
            if (spotColor == Flippo.BLACK) {
                black ++;
            } else if (spotColor == Flippo.WHITE) {
                white ++;
            }
        }

        return new Score(black, white);
    }

    /**
     * Execute the placing of a flippo in all directions
     * Wrapper for the Spot variant of doMove
     */
    public LinkedHashSet<Spot> doMove(String spot, Flippo color) {
        return doMove(getSpot(spot), color);
    }

    /**
     * Execute the placing of a flippo in all directions
     */
    public LinkedHashSet<Spot> doMove(Spot spot, Flippo color) {
        LinkedHashSet<Spot> flipped = getMoveSpots(spot, color);
        flipped.forEach(Spot::flip);
        spot.setFlippo(color);
        return flipped;
    }

    /**
     * Get all spots that would be flipped if a Flippo of specified color is placed at the specified spot
     */
    public LinkedHashSet<Spot> getMoveSpots(Spot spot, Flippo color) {
        LinkedHashSet<Spot> flipped = new LinkedHashSet<>();
        for (Direction direction : Direction.values()) {
            flipped.addAll(getMoveSpotsInDirection(spot, color, direction));
        }
        return flipped;
    }

    /**
     * Get all spots that would be flipped in the direction if this color flippo is placed at the spot
     */
    public LinkedHashSet<Spot> getMoveSpotsInDirection(Spot spot, Flippo color, Direction direction) {
        LinkedHashSet<Spot> flipped = new LinkedHashSet<>();

        // For each neighbour until the end or empty spot
        Spot neighbour = spot.getNeighbour(direction);
        LinkedHashSet<Spot> lastSegment = new LinkedHashSet<>();
        while (neighbour != null && neighbour.getFlippo() != Flippo.NONE) {
            // If of the same color, flip all in the latest segment and reset segment
            if (neighbour.getFlippo() == color) {
                flipped.addAll(lastSegment);
                lastSegment = new LinkedHashSet<>();
            }
            lastSegment.add(neighbour);
            neighbour = neighbour.getNeighbour(direction);
        }
        return flipped;
    }

    public void printBoard() {
        for (int y = 0; y < HEIGHT; y ++) {
            for (int x = 0; x < WIDTH; x ++) {
                Spot spot = getSpot(x, y);
                if (spot.getFlippo() == Flippo.NONE) {
                    System.err.print(".");
                } else if (spot.getFlippo() == Flippo.BLACK) {
                    System.err.print("X");
                } else if (spot.getFlippo() == Flippo.WHITE) {
                    System.err.print("O");
                } else {
                    System.err.println("Trying to print a flippo that's not possible");
                }
            }
            System.err.println();
        }
    }

    /**
     * Retrieve a spot on the board using its coordinates
     */
    public Spot getSpot(int x, int y) {
        if (! areValidCoordinates(x, y)) {
            return null;
        } else {
            return board[x][y];
        }
    }
}
