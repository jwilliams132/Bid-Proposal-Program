package gearworks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class JSON_Manager {

	private FileManager fileManager = new FileManager();
	private ObjectMapper objectMapper = new ObjectMapper();

	public JSON_Manager() {

	}

	public <T> List<T> parseJsonFile(File inputFile, Class<T[]> type) throws IOException {

		try {

			T[] array = objectMapper.readValue(inputFile, type);
			return Arrays.asList(array);
		} catch (JsonParseException e) {

			showWarning("JSON Parsing Error", "Error parsing JSON file (JSON syntax is wrong)", e.getMessage());
			throw e;
		} catch (JsonMappingException e) {

			showWarning("JSON Mapping Error", "Error mapping JSON file to Job objects (JSON can't map to Job Objects)",
					e.getMessage());
			throw e;
		} catch (IOException e) {

			showWarning("IO Error", "Error reading JSON file (e.g., file not found, permission issues)",
					e.getMessage());
			throw e;
		}
	}

	public <T> boolean saveToJSON(String filePath, boolean makeUnique, T objectToSave) {

		File jsonOutput = fileManager.chooseFile(filePath, makeUnique,
				null,
				FileManager.fileChooserOptions.SAVE, null);

		try {

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(jsonOutput, objectToSave);
		} catch (JsonGenerationException e) {

			showWarning("JSON Generation Error", "Error generating JSON", e.getMessage());
			e.printStackTrace();
			return false;
		} catch (JsonMappingException e) {

			showWarning("JSON Mapping Error", "Error mapping JSON file to Java objects", e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {

			showWarning("IO Error", "Error writing to JSON file", e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void showWarning(String header, String warningMessage, String argument) {

		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(header);
		alert.setHeaderText(null);
		alert.setContentText(warningMessage + ": " + argument);
		alert.showAndWait();
	}
}
