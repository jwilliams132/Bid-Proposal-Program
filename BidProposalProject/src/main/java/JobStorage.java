import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class JobStorage {

    private final File lettingFolder = new File(System.getProperty("user.home") + "/Desktop/Letting");
    private final String fileLookUpTargetName = "Program Output.txt";

    private FileManager fileManager = new FileManager();
    public List<String> filePaths = new ArrayList<>(); // Output

    public JobStorage() {

    }

    /**
     * Reads the contents of a file located at the specified path and returns them
     * as a list of strings.
     *
     * @param knownFilePath The path to the file to be read.
     * @return A list of strings representing the contents of the file, or null if
     *         no file is selected.
     */
    public ArrayList<String> getFileContents(String knownFilePath) {

        File inputFile = fileManager.chooseFile(knownFilePath, null, FileManager.fileChooserOptions.OPEN, null);
        if (inputFile == null) {

            showWarning("Warning", "Error", "No file selected");
            return null;
        }
        return fileManager.readFile(inputFile);
    }

    /**
     * Displays a warning message dialog box with the specified header and warning
     * message.
     *
     * @param header         The header or title of the warning dialog.
     * @param warningMessage The warning message to be displayed.
     * @param argument       Additional information or an argument to be included in
     *                       the warning message.
     */
    public void showWarning(String header, String warningMessage, String argument) {

        JOptionPane.showMessageDialog(null, warningMessage + ": " + argument, header, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Parses a file located at the specified path and returns a list of Job
     * objects.
     *
     * @param knownFilePath The path to the file to be parsed.
     * @return A list of Job objects extracted from the file according to its
     *         format.
     * @throws UnsupportedOperationException if the file format is not recognized or
     *                                       supported.
     */
    public List<Job> parseFile(String knownFilePath) {

        List<String> fileContents = getFileContents(knownFilePath);
        FormatInterface fileFormat = null;

        if (fileContents.get(0).startsWith(CombinedFormat.fileHeader))
            fileFormat = new CombinedFormat();

        if (fileContents.get(0).startsWith(V1Format.fileHeader))
            fileFormat = new V1Format();

        if (fileContents.get(0).startsWith(V2Format.fileHeader))
            fileFormat = new V2Format();

        if (fileFormat == null) {
            showWarning("Warning", "Error", "The File (" + knownFilePath + ") Opened Does Not Match Known Formats");
            throw new UnsupportedOperationException("File Does Not Match Known Formats");
        }

        return fileFormat.jobsFromFormat(fileContents);
    }

    /**
     * Updates the job storage file by collecting job data from multiple files in a
     * specified folder
     * and saving it in a consolidated format.
     */
    public void updateJobStorageFile() {

        List<String> filesPaths = findFilePaths(lettingFolder.getAbsolutePath()); // gets list of path strings
        List<List<Job>> list = new ArrayList<List<Job>>(); // creates new list of joblists
        FormatInterface v2Output = new V2Format();

        filesPaths.forEach(path -> list.add(parseFile(path))); // adds each joblist to list
        List<String> jobStorageContents = new ArrayList<>();
        for (List<Job> job : list) {

            jobStorageContents.addAll(v2Output.jobsToFormat(job)); // adds each joblist item to storage content
        }
        File jobStorageFile = fileManager.chooseFile(lettingFolder.getAbsolutePath() + "/Job Storage.txt", null,
                FileManager.fileChooserOptions.SAVE, null);

        fileManager.saveFile(jobStorageFile, jobStorageContents); // saves txt file of V2Format of all letting Program
                                                                  // Output.txt files
    }

    /**
     * Recursively searches for files with a specific target name in the specified
     * directory
     * and its subdirectories and returns a list of their absolute paths.
     *
     * @param directoryPath The path to the directory to start searching from.
     * @return A list of absolute file paths that match the specified target name.
     */
    public List<String> findFilePaths(String directoryPath) {

        File directory = new File(directoryPath);
        File[] directoryContents = directory.listFiles();

        if (directoryContents == null)
            return filePaths;

        for (File file : directoryContents) {

            if (file.isDirectory() && !file.getName().equals("Testing"))

                findFilePaths(file.getAbsolutePath());

            if (file.isFile() && file.getName().equals(fileLookUpTargetName))

                filePaths.add(file.getAbsolutePath());
        }
        return filePaths;
    }
}
