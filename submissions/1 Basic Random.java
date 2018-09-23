import java.util.*;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Collection;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.nio.file.Files;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;



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


enum Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST
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


enum Flippo {
    NONE, BLACK, WHITE
}




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


/**
 * Contains settings in one place for easy tweaking
 */
class Settings {



}



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

}



class RandomStrategy extends Strategy {

    private Board board;

    public RandomStrategy(Board board) {
        this.board = board;
    }

    @Override
    public Spot getMove() {
        return board.getValidPlacementSpots().iterator().next();
    }
}




abstract class Strategy {

    public static final Class<? extends Strategy> DEFAULT_STRATEGY = RandomStrategy.class;

    public abstract Spot getMove();

}




class StrategyCollection extends Strategy {

    private HashMap<Class<? extends Strategy>, Strategy> strategyInstances;

    private Board board;

    private Class<? extends Strategy> strategy;

    public StrategyCollection(Board board) {
        this.board = board;
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
                System.err.println("Exception when intiating strategy with only board argument");
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
    public Spot getMove() {
        return getStrategyInstance().getMove();
    }

}

