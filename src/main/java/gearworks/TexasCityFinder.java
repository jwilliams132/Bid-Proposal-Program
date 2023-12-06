package gearworks;

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

	public TexasCityFinder() {

		// Initialize the map
		countyToCity = new HashMap<String, String>();
		// Read the CSV file
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader("src\\main\\resources\\gearworks\\List_of_counties_in_Texas.csv"));
			String line;
			while ((line = reader.readLine()) != null) {

				// Split the line into columns
				String[] columns = line.split(",");
				countyToCity.put(columns[0].toUpperCase(), columns[1]);
			}
			// Close the file
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLargestCity(String county) {

		if (county.length() > 5 && county.substring(county.length() - 5).equals(", ETC"))
			county = county.substring(0, county.length() - 5);
		return countyToCity.get(county);
	}
}
