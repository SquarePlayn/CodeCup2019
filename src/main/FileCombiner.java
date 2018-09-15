package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Outputs all files in the source folder into one combined .java file
 * Takes care of import combining and putting at the top
 * Makes any top-level class that is declared public package-specific
 */
public class FileCombiner {

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
        line = line.replace("public class", "class");
        line = line.replace("public abstract class", "abstract class");
        line = line.replace("public enum", "enum");
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