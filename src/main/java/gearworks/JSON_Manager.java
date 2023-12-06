package gearworks;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides methods for loading and saving preferences from/to JSON
 * files.
 * It uses the Jackson library for JSON processing.
 */
public class JSON_Manager {

    /**
     * Loads preferences from a JSON file.
     *
     * @param filePath       The path to the JSON file.
     * @param preferredClass The class type representing the preferences.
     * @param <T>            The type of preferences class.
     * @return An instance of the preferences class loaded from the JSON file, or a
     *         new instance if loading fails.
     */
    public <T> T loadPreferences(String filePath, Class<T> preferredClass) {

        File file = new File(filePath);
        ObjectMapper mapper = new ObjectMapper();

        try {

            return mapper.readValue(file, preferredClass);
        } catch (IOException e) {

            e.printStackTrace();
            try {

                return preferredClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {

                ex.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Saves preferences to a JSON file.
     *
     * @param filePath         The path to the JSON file.
     * @param preferences      The preferences to be saved.
     * @param preferencesClass The class type representing the preferences.
     * @param <T>              The type of preferences class.
     */
    public <T> void savePreferences(String filePath, T preferences, Class<T> preferencesClass) {
        
        File file = new File(filePath);
        ObjectMapper mapper = new ObjectMapper();

        try {

            mapper.writeValue(file, preferences);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}