package gearworks;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Usage:
 * TexasCityFinder cityFinder = new TexasCityFinder("src\\main\\resources\\List_of_counties_in_Texas.csv");
 * System.out.println(cityFinder.getLargestCity("Travis"));
 */

public class CountyManager {

	private final String RELATIVE_FILE_PATH = "src\\main\\resources\\gearworks\\Counties.json";

	private JSON_Manager json_Manager = new JSON_Manager();

	private Map<String, County> countiesSet = new TreeMap<String, County>();
	private Set<String> districts = new TreeSet<String>();

	
	public CountyManager() {
		
		openCountiesFile().forEach(county -> {
			countiesSet.put(county.getName().toUpperCase(), county);
			districts.add(county.getDistrict());
		});
		// System.out.println(countiesSet);
		// System.out.println(districts);
	}
	
	private Set<County> openCountiesFile() {
		
		try {
			
			File counties_File = new File(RELATIVE_FILE_PATH);
			return new HashSet<>(json_Manager.parseJsonFile(counties_File, County[].class));
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
		
		return countiesSet.get(nameInCaps.replace(", ETC", "")).getLargestCity();
	}
	
	public String getDistrict(String nameInCaps) {
		
		return countiesSet.get(nameInCaps.replace(", ETC", "")).getDistrict();
	}

	public Set<String> getDistricts() {

		return districts;
	}
}
