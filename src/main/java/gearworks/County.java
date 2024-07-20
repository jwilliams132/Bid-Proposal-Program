package gearworks;

public class County {

	private String name;
	private String district;
	private String largestCity;
	
	public County(String name, String district, String largestCity) {

		this.name = name;
		this.district = district;
		this.largestCity = largestCity;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDistrict() {

		return district;
	}

	public void setDistrict(String district) {

		this.district = district;
	}

	public String getLargestCity() {

		return largestCity;
	}

	public void setLargestCity(String largestCity) {

		this.largestCity = largestCity;
	}
}
