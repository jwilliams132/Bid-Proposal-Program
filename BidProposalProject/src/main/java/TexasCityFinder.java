import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Usage:
 * TexasCityFinder cityFinder = new TexasCityFinder("src\\main\\resources\\List_of_counties_in_Texas.csv");
 * System.out.println(cityFinder.getLargestCity("Travis"));
 */

public class TexasCityFinder {
	private Map<String, String> countyToCity;

	public TexasCityFinder(String filePath) {

		// Initialize the map
		countyToCity = new HashMap<>();
		// Read the CSV file
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {

				// Split the line into columns
				String[] columns = line.split(",");
				countyToCity.put(columns[0], columns[1]);
			}
			// Close the file
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getLargestCity(String county) {
		county = county.toLowerCase();
		county = county.replaceFirst(county.substring(0, 1), county.substring(0, 1).toUpperCase());
		return countyToCity.get(county);
	}
}
