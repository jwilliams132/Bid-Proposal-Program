import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.synth.SynthLookAndFeel;

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
            FileFilter fileFilter) {

        // synthLookAndFeel();
        JFileChooser fileChooser = new JFileChooser(); // create a new JFileChooser object

        // Set the current directory of the JFileChooser to the user's desktop
        // directory.
        fileChooser.setCurrentDirectory(desktopDirectory);

        if (fileFilter != null)
            fileChooser.setFileFilter(fileFilter);

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
            fileChooser.setCurrentDirectory(new File(currentDirectory));

        // Show the open or save dialog based on the value of "option"
        int response = option == FileManager.fileChooserOptions.OPEN ? fileChooser.showOpenDialog(null)
                : fileChooser.showSaveDialog(null);

        // if the response to the file chooser returns the value that you selected
        // something...
        if (response == JFileChooser.APPROVE_OPTION) {
            return new File(fileChooser.getSelectedFile().getAbsolutePath()); // create new file object of
                                                                              // the file selected
        }
        // windowsLookAndFeel();
        return null;
    }

    public String chooseDirectory(String currentDirectory) {

        // synthLookAndFeel();
        if (currentDirectory == null)
            currentDirectory = desktopDirectory.getAbsolutePath();
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setCurrentDirectory(new File(currentDirectory));
        int response = directoryChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION)

            return directoryChooser.getSelectedFile().getAbsolutePath();
        // windowsLookAndFeel();
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
            fileContents.add(scanner.nextLine().trim());

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

    // private static void synthLookAndFeel() {

    //     SynthLookAndFeel laf = new SynthLookAndFeel();
    //     try {

    //         File file = new File("BidProposalProject\\src\\main\\resources\\CyanTheme.xml");
    //         URL url = file.toURI().toURL();
    //         laf.load(url);
    //         UIManager.setLookAndFeel(laf);
    //     } catch (ParseException parseException) {

    //         System.out.println("parseException");
    //         parseException.printStackTrace();
    //     } catch (IllegalArgumentException IllegalArgumentException) {

    //         System.out.println("IllegalArgumentException");
    //         IllegalArgumentException.printStackTrace();
    //     } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {

    //         System.out.println("unsupportedLookAndFeelException");
    //         unsupportedLookAndFeelException.printStackTrace();
    //     } catch (MalformedURLException e) {

    //         e.printStackTrace();
    //     } catch (IOException e) {

    //         e.printStackTrace();
    //     }

    // }

    // private static void windowsLookAndFeel() {

    //     try {

    //         UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    //     } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
    //             | UnsupportedLookAndFeelException e) {

    //         e.printStackTrace();
    //     }
    // }
}
