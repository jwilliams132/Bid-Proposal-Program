package gearworks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
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

	public <T> void csvToJSON(File inputFile, Class<T> type) {
		
        List<T> objectsList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            String line;
            while ((line = br.readLine()) != null) {

                // Manual parsing logic for quoted fields
                String[] fields = parseCSVLine(line);

                T obj = createObjectFromFields(fields, type);
                if (obj != null) {

                    objectsList.add(obj);
                }
            }

            // Write the list to a JSON file
            saveToJSON(inputFile.getAbsolutePath().replace(".csv", ".json"), false, objectsList);

        } catch (IOException e) {

            showWarning("CSV to JSON Conversion Error", "Error reading CSV file or writing JSON file", e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] parseCSVLine(String line) {

        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : line.toCharArray()) {

            if (c == '"') {

                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {

                fields.add(field.toString());
                field.setLength(0);
            } else {

                field.append(c);
            }
        }
        fields.add(field.toString()); // Add the last field

        return fields.toArray(new String[0]);
    }

    // This method should be implemented to map CSV fields to an object of type T
    private <T> T createObjectFromFields(String[] fields, Class<T> type) {

    try {

        // Create a new instance of the type T
        T instance = type.getDeclaredConstructor().newInstance();

        // Get all fields of the class
        Field[] classFields = type.getDeclaredFields();

        for (int i = 0; i < fields.length && i < classFields.length; i++) {

            Field field = classFields[i];
            field.setAccessible(true); // Make private fields accessible

            String value = fields[i];
            if (field.getType() == int.class || field.getType() == Integer.class) {

                field.set(instance, Integer.parseInt(value));
            } else if (field.getType() == String.class) {

                field.set(instance, value);
            } else if (field.getType() == BigDecimal.class) {

                field.set(instance, new BigDecimal(value));
            } else {

                // Handle other types as needed
            }
        }
        return instance;
    } catch (Exception e) {

        e.printStackTrace();
        return null;
    }
}
}
