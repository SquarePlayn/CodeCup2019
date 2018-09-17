package main;

/**
 * Represents the 8 directions on the board
 */
public enum Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    public Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case NORTHEAST: return SOUTHWEST;
            case EAST: return WEST;
            case SOUTHEAST: return NORTHWEST;
            case SOUTH: return NORTH;
            case SOUTHWEST: return NORTHEAST;
            case WEST: return EAST;
            case NORTHWEST: return SOUTHEAST;
            default: return null;
        }
    }
}
