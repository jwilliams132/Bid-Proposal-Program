package gearworks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JobFilter_District implements JobFilterInterface {

	private Set<String> counties;

	public JobFilter_District(String csvFilePath) {

		counties = new HashSet<>();
		// Load counties from the CSV file
		try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
			
			String line;
			while ((line = br.readLine()) != null) {

				String[] values = line.split(",");
				counties.add(values[0].trim()); // Assuming the county name is the first field
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public boolean apply(Job job) {

		return counties.contains(job.getCounty());
	}

}
