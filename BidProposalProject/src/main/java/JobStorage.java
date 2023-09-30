import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class JobStorage {

    private FileManager fileManager = new FileManager();
    private final File lettingFolder = new File(System.getProperty("user.home") + "/Desktop/Letting");
    private final String fileLookUpTargetName = "Program Output.txt";

    public JobStorage() {

    }

    public ArrayList<String> getFileContents(String knownFilePath) {

        File inputFile = fileManager.chooseFile(knownFilePath, null, FileManager.fileChooserOptions.OPEN, null);
        if (inputFile == null) {

            showWarning("Warning", "Error", "No file selected");
            return null;
        }
        return fileManager.readFile(inputFile);
    }

    public void showWarning(String header, String warningMessage, String argument) {

        JOptionPane.showMessageDialog(null, warningMessage + ": " + argument, header, JOptionPane.WARNING_MESSAGE);
    }

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

        fileManager.saveFile(jobStorageFile, jobStorageContents);        
    }

    public List<String> filePaths = new ArrayList<>(); // Output

    public List<String> findFilePaths(String directoryPath) {

        File directory = new File(directoryPath);
        File[] directoryContents = directory.listFiles();

        if (directoryContents != null) { // if folder isn't empty

            for (File file : directoryContents) { // iterate through: if its a folder ? recurse : add file to List

                if (file.isDirectory()) {

                    // Recursive call for subdirectories
                    findFilePaths(file.getAbsolutePath());
                } else if (file.isFile() && file.getName().equals(fileLookUpTargetName)) {

                    // Found the target file
                    filePaths.add(file.getAbsolutePath());
                }
            }
        }
        return filePaths;
    }
}
