package gearworks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class FileManager {

    public enum fileChooserOptions {
        OPEN, SAVE
    };

    // Create a File object that points to the user's desktop directory.
    private final File desktopDirectory = new File(System.getProperty("user.home") + "/Desktop");

    public FileManager() {

    }

    /*
     * File userFriendlyOutput = fileManager.chooseFile(FILE_NAME,
     * DIRECTORY,FileManager.fileChooserOptions.OPEN,FILTER);
     */
    public File chooseFile(String knownFile, String currentDirectory, fileChooserOptions option,
            // FileFilter fileFilter) {
            FileChooser.ExtensionFilter fileFilter) {

        FileChooser fileChooser = new FileChooser(); // create a new JFileChooser object
        fileChooser.setInitialDirectory(desktopDirectory);

        if (fileFilter != null)
            fileChooser.getExtensionFilters().add(fileFilter);

        if (knownFile != null) {

            File givenFile = new File(knownFile);

            if (givenFile.exists())
                return givenFile;

            if (option == fileChooserOptions.SAVE) {
                try {
                    givenFile.createNewFile();
                    return givenFile;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        if (currentDirectory != null)
            // sets the directory to start your search for a file in
            fileChooser.setInitialDirectory(new File(currentDirectory));

        // Show the open or save dialog based on the value of "option"
        File selectedFile = option == fileChooserOptions.OPEN
                ? fileChooser.showOpenDialog(null)
                : fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            return selectedFile;
        }

        return null;
    }

    public String chooseDirectory(String currentDirectory) {

        if (currentDirectory == null)
            currentDirectory = System.getProperty("user.home");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(currentDirectory));

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        }

        return null;
    }

    public ArrayList<String> readFile(File file) {

        Scanner scanner = null;
        ArrayList<String> fileContents = new ArrayList<String>();

        // Try to create a Scanner object to read the input file.
        // If the file is not found, print an error message.
        try {

            scanner = new Scanner(file);
        } catch (Exception e) {

            System.out.println("COULD NOT FIND FILE.");
        }

        // While the scanner has more lines, add each line to the ArrayList.
        // Trim the line to remove any leading or trailing white space.
        while (scanner.hasNext())
            fileContents.add(scanner.nextLine());

        // Close the scanner and return the ArrayList of file contents.
        scanner.close();
        return fileContents;
    }

    public void saveFile(File file, List<String> content) {

        // Create a Formatter object to write to the output file.
        // If the file cannot be created, print an error message.
        Formatter formatter = null;
        try {

            formatter = new Formatter(file);
        } catch (Exception e) {

            System.err.println("File not Created");
        }

        // Write each element of the ArrayList to the file, followed by a newline.
        for (String line : content) {

            formatter.format("%s%n", line);
        }

        // Close the Formatter and save the file.
        formatter.close();
    }

    public List<String> readFile(String filePath) {

        File file = chooseFile(filePath, null, FileManager.fileChooserOptions.OPEN, null);
        return readFile(file);
    }
}
