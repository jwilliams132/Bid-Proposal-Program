package gearworks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InputFileProcessor {

    private FileManager fileManager = new FileManager();

    public enum FileFormat {

        COMBINED, V1, V2, V3
    }

    public InputFileProcessor() {

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
        if (fileContents.get(0).startsWith(CombinedFormat.fileHeader))

            fileFormat = new CombinedFormat();

        if (fileContents.get(0).startsWith(V1Format.fileHeader))

            fileFormat = new V1Format();

        if (fileContents.get(0).startsWith(V2Format.fileHeader)) {

            fileFormat = new V2Format();
            fileContents.remove(0); // removes file header
        }

		if (fileContents.get(0).startsWith(V3Format.fileHeader)) {

            fileFormat = new V3Format();
            fileContents.remove(0); // removes file header
        }

        if (fileFormat == null) {

            showWarning("Warning", "Error", "The File (" + knownFilePath + ") Opened Does Not Match Known Formats");
            throw new UnsupportedOperationException("File Does Not Match Known Formats");
        }

        return fileFormat.jobsFromFormat(fileContents);
    }

    public void saveFileFormat(List<Job> jobs, String filePath, FileFormat format) {

        FormatInterface fileFormat;
        switch (format) {

            case V1:
                fileFormat = new V1Format();
                break;

            case V2:
                fileFormat = new V2Format();
                break;

			case V3:
                fileFormat = new V3Format();
                break;

            default:
                fileFormat = new V2Format();
                break;
        }

        File outputFile = fileManager.chooseFile(filePath, null, FileManager.fileChooserOptions.SAVE, null);
        fileManager.saveFile(outputFile, fileFormat.jobsToFormat(jobs));
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

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(warningMessage + ": " + argument);

        alert.showAndWait();
    }
}
