import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class JobStorage {

    private final File lettingFolder = new File(System.getProperty("user.home") + "/Desktop/Letting");
    private final String fileLookUpTargetName = "Program Output.txt";

    private FileManager fileManager = new FileManager();
    public List<String> filePaths = new ArrayList<>();
    public List<String> savedFilePaths = new ArrayList<>();
    public List<Job> savedJobStorage;

    public JobStorage() {

        savedJobStorage = parseFile(lettingFolder.getAbsolutePath() + "/Job Storage.txt");
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
        System.out.println(fileContents.get(0));
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

    // ====================================================================================================
    // Bulk Storage Methods
    // ====================================================================================================

    /**
     * Updates the job storage file by collecting job data from multiple files in a
     * specified folder
     * and saving it in a consolidated format.
     */
    public void updateJobStorageFile() {

        List<String> filePaths = findFilePaths(lettingFolder.getAbsolutePath()); // gets list of path strings
        List<Job> jobs = new ArrayList<Job>(); // creates new list of joblists
        List<String> jobStorageContents = new ArrayList<>();

        FormatInterface v2Output = new V2Format();

        for (String path : filePaths) {

            for (String savedPath : savedFilePaths) {

                if (path.equals(savedPath))

                    break;
                filePaths.add(path);
                jobs.addAll(parseFile(path));
            }
        }
        jobs.forEach(job -> jobStorageContents.add(v2Output.jobToFormat(job))); // adds each formatted output for each
                                                                                // job to jobStorageContents

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

    // ====================================================================================================
    // Misc. Methods
    // ====================================================================================================

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
}
