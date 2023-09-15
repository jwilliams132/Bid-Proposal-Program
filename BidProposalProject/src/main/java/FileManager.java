import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileManager {

	public enum fileChooserOptions {
		OPEN, SAVE
	};

	// Create a File object that points to the user's desktop directory.
	private final File desktopDirectory = new File(System.getProperty("user.home") + "/Desktop");

	public FileManager() {

	}

	public File chooseFile(String knownFile, String currentDirectory, fileChooserOptions option,
			FileFilter fileFilter) {

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
		return null;
	}

	public String chooseDirectory(String currentDirectory) {

		if (currentDirectory == null)
			currentDirectory = desktopDirectory.getAbsolutePath();
		JFileChooser directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directoryChooser.setCurrentDirectory(new File(currentDirectory));
		int response = directoryChooser.showOpenDialog(null);
		if (response == JFileChooser.APPROVE_OPTION)

			return directoryChooser.getSelectedFile().getAbsolutePath();

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

	public void saveFile(File file, ArrayList<String> content) {

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
}
