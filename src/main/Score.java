package main;

import java.util.HashMap;

/**
 * Container for a Score, being a hashmap with scores for black and white
 * assured to be available
 */
public class Score extends HashMap<Flippo, Integer> {

    public Score(int black, int white) {
        this.put(Flippo.BLACK, black);
        this.put(Flippo.WHITE, white);
    }

}
