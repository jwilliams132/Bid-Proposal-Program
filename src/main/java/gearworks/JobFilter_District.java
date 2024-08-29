package gearworks;

import java.util.Set;

public class JobFilter_District implements JobFilterInterface {

	private Set<String> blacklistedDistricts;
	private CountyManager countyManager = new CountyManager();

	public JobFilter_District(Set<String> blacklistedDistricts) {

		this.blacklistedDistricts = blacklistedDistricts;
	}

	@Override
	public boolean apply(Job job) {

		return !blacklistedDistricts.contains(countyManager.getDistrict(job.getCounty().toUpperCase()));
	}

}
