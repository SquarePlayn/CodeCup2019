import java.util.Arrays;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.*;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.Collections;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;



/**
 * Representation of the playboard of WIDTH x HEIGHT spots
 */
class Board {
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


/**
 * Represents the 8 directions on the board
 */
enum Direction {
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



/**
 * Outputs all files in the source folder into one combined .java file
 * Takes care of import combining and putting at the top
 * Makes any top-level class that is declared public package-specific
 */
class FileCombiner {

    private HashSet<String> imports;
    private ArrayList<String> lines;

    /**
     * Recursively reads the folder to handle all files in the folders
     */
    private void recurseOverFolder(final File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        // Check each path and recurse if it's a folder, handle if it's a file
        for (final File fileEntry : files) {
            if (fileEntry.isDirectory()) {
                recurseOverFolder(fileEntry);
            } else {
                handleFile(fileEntry);
            }
        }
    }

    /**
     * Takes care of handling one file to output it
     */
    private void handleFile(File file) {
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                // Get the next line
                String line = reader.nextLine();

                // Clean any lines from unwanted statements
                line = cleanLine(line);

                // Filter out the imports and save
                if (line.length() >= 7 && line.substring(0, 7).equals("import ")) {
                    if (! (line.length() >= 12 && line.substring(0, 12).equals("import main."))) {
                        imports.add(line);
                    }
                } else if (! (line.length() >= 7 && line.substring(0, 7).equals("package"))){
                    lines.add(line);
                }
            }
            lines.add(""); // Add an empty line between files
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Strips unwanted gunk and improper formatting off of the line ready for output
     */
    private String cleanLine(String line) {
        line = line.replace("class", "class");
        line = line.replace("abstract class", "abstract class");
        line = line.replace("enum", "enum");
        return line;
    }

    /**
     * Outputs the collected imports and files to a specified file
     */
    private void outputFile() {
        // Define output file name
        Scanner sc = new Scanner(System.in);
        System.out.println("What do you want to call this file?");
        String name = sc.nextLine();

        // Output to file
        Path outputFile = Paths.get("submissions/" + name + ".java");
        try {
            Files.write(outputFile, imports);
            if (imports.size() > 0)
                Files.write(outputFile, Collections.singletonList(""), StandardOpenOption.APPEND);
            Files.write(outputFile, lines, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Runs the whole process of reading the files, merging them and outputting
     * them to a specified output file.
     */
    public void run() {
        // Initialize
        imports = new HashSet<>();
        lines = new ArrayList<>();

        // Read and merge source files
        final File folder = new File("src");
        recurseOverFolder(folder);

        // Output to file
        outputFile();
    }

    public static void main(String[] args) {
        new FileCombiner().run();
    }

}


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


class InputReader {
}


class Judge {
}



/**
 * Stores the act of doing a move
 */
class Move {

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
        System.err.println("Computer plays color " + (white ? "white" : "black"));
        if (! white) {
            board.doMove(line, color.opposite());
        }
        for (int i = 0; i < 29; i ++) {
            Spot move = strategyCollection.getMove(color);
            System.out.println(move.getStringRepresentation());
            System.out.flush();
            board.doMove(move, color);
            board.printBoard();
            board.doMove(sc.nextLine(), color.opposite());
        }
        Spot move = strategyCollection.getMove(color);
        System.out.println(move.getStringRepresentation());
        System.out.flush();
        board.doMove(move, color);
        if (white) {
            board.printBoard();
            board.doMove(sc.nextLine(), color.opposite());
        }
        Score score = board.getScore();
        System.err.println("Computer played color " + (white ? "white" : "black"));
        System.err.println("Final score: Black " + score.get(Flippo.BLACK) +
                " - " + score.get(Flippo.WHITE) + " White");
        // TODO NB: The above section is full bodge just to test

    }

    public static void main(String[] args) {
        System.err.println("Philippo main is called");
        new Philippo().runContest();
    }

}


class Player {

    Board board;


}



/**
 * Container for a Score, being a hashmap with scores for black and white
 * assured to be available
 */
class Score extends HashMap<Flippo, Integer> {

    public Score(int black, int white) {
        this.put(Flippo.BLACK, black);
        this.put(Flippo.WHITE, white);
    }

}


/**
 * Contains settings in one place for easy tweaking
 */
class Settings {



}



/**
 * One location on the board
 */
class Spot {

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
     * A spot is valid when it is empty and has a neighbouring
     * NB: Does not take into account the need for flipping if possible
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

    public String getStringRepresentation() {
        return "" + (char)('A' + y) + (x+1);
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

    public void flip() {
        if (getFlippo() == Flippo.NONE) {
            System.err.println("Tried to flip a field with no flippo");
        } else {
            setFlippo(getFlippo().opposite());
        }
    }
}



/**
 * A strategy that picks the first possible spot to place its  Flippo
 */
class FirstPossibleStrategy extends Strategy {

    public FirstPossibleStrategy(Board board) {
        super(board);
    }

    @Override
    public Spot getMove(Flippo color) {
        return board.getValidPlacementSpots(color).iterator().next();
    }
}



/**
 * A strategy that picks the spot that results in the state with the highest score
 */
class MaxImmediateScoreStrategy extends MinMaxStrategy {

    public MaxImmediateScoreStrategy(Board board) {
        super(board);
        super.setSearchDepth(1);
    }

    @Override
    public Spot getMove(Flippo color) {
        return super.getMove(color);
    }
}




/**
 * A strategy that uses MinMax to find the best spot
 */
class MinMaxStrategy extends Strategy {

    private static final int DEFAULT_SEARCH_DEPTH = 4; // <= 4 if for all moves

    private int searchDepth;

    public MinMaxStrategy(Board board) {
        super(board);
        this.searchDepth = DEFAULT_SEARCH_DEPTH;
    }

    @Override
    public Spot getMove(Flippo color) {
        Spot bestSpot = null;
        int bestScore = Integer.MIN_VALUE;

        System.err.println("There are " + board.getValidPlacementSpots(color).size() + " possible spots");
        for (Spot spot : board.getValidPlacementSpots(color)) {
            Move move = new Move(board, spot, color);
            move.execute();
            int posScore = getRecursiveScore(color.opposite(), searchDepth - 1).get(color);
            if (posScore > bestScore) {
                bestScore = posScore;
                bestSpot = spot;
            }
            move.undo();
        }
        System.err.println("Found best spot with score " + bestScore);

        return bestSpot;
    }

    /**
     * Get the MinMax best score for this color, looking a certain depth from here
     */
    private Score getRecursiveScore(Flippo color, int depth) {
        if (depth <= 0) {
            return board.getScore();
        } else {
            LinkedHashSet<Spot> validPlacementSpots = board.getValidPlacementSpots(color);
            if (validPlacementSpots.size() <= 0) {
                return board.getScore();
            }

            Score bestScore = new Score(Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (Spot spot : validPlacementSpots) {
                Move move = new Move(board, spot, color);
                move.execute();
                Score posScore = getRecursiveScore(color.opposite(), depth - 1);
                if (posScore.get(color) > bestScore.get(color)) {
                    bestScore = posScore;
                }
                move.undo();
            }

            return bestScore;
        }
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }
}




/**
 * A strategy that takes a random spot out of all possible placement spots
 */
class RandomStrategy extends Strategy {

    public RandomStrategy(Board board) {
        super(board);
    }


    @Override
    public Spot getMove(Flippo color) {
        ArrayList<Spot> spots = new ArrayList<>(board.getValidPlacementSpots(color));
        Collections.shuffle(spots);
        return spots.get(0);
    }

}




/**
 * Abstract class for a strategy that can decide on which move to do
 */
abstract class Strategy {

    public static final Class<? extends Strategy> DEFAULT_STRATEGY = MinMaxStrategy.class;

    protected Board board;

    public static final List<Class<? extends Strategy>> STRATEGIES = Arrays.asList(
            RandomStrategy.class,
            FirstPossibleStrategy.class,
            MinMaxStrategy.class,
            MaxImmediateScoreStrategy.class
    );

    public Strategy(Board board) {
        this.board = board;
    }

    public abstract Spot getMove(Flippo color);

}




/**
 * Strategy that is a wrapper around multiple strategies.
 * Can be used to swap between strategies
 * Makes sure only one instance of each strategy is created
 */
class StrategyCollection extends Strategy {

    private HashMap<Class<? extends Strategy>, Strategy> strategyInstances;

    private Class<? extends Strategy> strategy;

    public StrategyCollection(Board board) {
        super(board);
        this.strategyInstances = new HashMap<>();
        this.strategy = Strategy.DEFAULT_STRATEGY;
    }

    /**
     * Get a strategy by its class. Makes sure always max one is created
     */
    private Strategy getStrategyInstance() {
        if (strategyInstances.containsKey(strategy)) {
            return strategyInstances.get(strategy);
        } else {
            try {
                Strategy strategyInstance = strategy.getConstructor(Board.class).newInstance(board);
                strategyInstances.put(strategy, strategyInstance);
                return strategyInstance;
            } catch (Exception e) {
                System.err.println("Exception when initiating strategy with only board argument");
                e.printStackTrace();
                return null;
            }
        }
    }

    public Class<? extends Strategy> getStrategy() {
        return strategy;
    }

    public void setStrategy(Class<? extends Strategy> strategy) {
        this.strategy = strategy;
    }

    @Override
    public Spot getMove(Flippo color) {
        return getStrategyInstance().getMove(color);
    }

}



class DefaultStrategySwitcher extends Switcher {

    public DefaultStrategySwitcher() {
    }

    @Override
    public Class<? extends Strategy> getStrategy() {
        return Strategy.DEFAULT_STRATEGY;
    }

}




/**
 * Switches to one random strategy at start and then uses that strategy for
 * the entire game
 */
class OneRandomStrategySwitcher extends Switcher {

    private Random random;
    private Class<? extends Strategy> strategy;

    public OneRandomStrategySwitcher() {
        random = new Random();
        strategy = Strategy.STRATEGIES.get(random.nextInt(Strategy.STRATEGIES.size()));
    }

    @Override
    public Class<? extends Strategy> getStrategy() {
        return strategy;
    }
}




/**
 * Switches to a different random Strategy each turn
 */
class RandomSwitcher extends Switcher {

    private Random random;

    public RandomSwitcher() {
        this.random = new Random();
    }

    @Override
    public Class<? extends Strategy> getStrategy() {
        return Strategy.STRATEGIES.get(random.nextInt(Strategy.STRATEGIES.size()));
    }
}



/**
 * Abstract class used for classes that can decide which strategy to run on each turn
 */
abstract class Switcher {

    public abstract Class<? extends Strategy> getStrategy ();

}

