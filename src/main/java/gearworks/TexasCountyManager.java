package gearworks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Usage:
 * TexasCityFinder cityFinder = new TexasCityFinder("src\\main\\resources\\List_of_counties_in_Texas.csv");
 * System.out.println(cityFinder.getLargestCity("Travis"));
 */

public class TexasCountyManager {

	private final String RELATIVE_FILE_PATH = "src\\main\\resources\\Counties.json";

	private JSON_Manager json_Manager = new JSON_Manager();

	private Map<String, County> countiesMap = new TreeMap<String, County>();
	private List<County> countyList = new ArrayList<County>();

	public TexasCountyManager() {

		countyList = openCountiesFile();
		countyList.stream().map(county -> countiesMap.put(county.getName().toUpperCase(), county));
	}

	private List<County> openCountiesFile() {

		try {

			File counties_File = new File(RELATIVE_FILE_PATH);
			return json_Manager.parseJsonFile(counties_File, County[].class);
		} catch (NullPointerException e) {

			System.err.println((RELATIVE_FILE_PATH) + " WAS NOT FOUND.  ->  " + e.getMessage());
			return null;
		} catch (IOException e) {

			System.err.println("THERE WAS AN ISSUE WITH THIS FILE:  " + (RELATIVE_FILE_PATH) + " BEING LOADED.  ->  "
					+ e.getMessage());
			return null;
		}
	}

	public String getLargestCity(String nameInCaps) {

		return countiesMap.get(nameInCaps).getLargestCity();
	}

	public String getDistrict(String nameInCaps) {

		return countiesMap.get(nameInCaps).getDistrict();
	}
}
