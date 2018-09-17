package main;

/**
 * Representation of the coin on one spot
 */
enum Flippo {
    NONE, BLACK, WHITE;

    public Flippo opposite() {
        switch (this) {
            case NONE: return NONE;
            case BLACK: return WHITE;
            case WHITE: return BLACK;
            default: return null;
        }
    }
}
