import java.util.ArrayList;

public class Job {

	private ArrayList<Contractor> contractorList = new ArrayList<Contractor>();
	private ArrayList<LineItem> lineItems = new ArrayList<LineItem>();
	private String county, highway, csj;
	private int workingDays = 0, upTo_Mobs = 1;
	private float totalMobs = 0, additionalMobs = 0;

	// used for no pricing added
	public Job(String county, String highway, String csj, int workingDays, ArrayList<LineItem> lineItems,
			ArrayList<Contractor> contractorList) {

		setCounty(county);
		setHighway(highway);
		setCsj(csj);
		setWorkingDays(workingDays);

		if (lineItems != null) {
			setLineItems(lineItems);
		}

		setContractorList(contractorList);

		removeBlacklistedContractors();
	}

	public Job(String county, String highway, String csj, int workingDays, ArrayList<LineItem> lineItems, int upTo_Mobs,
			float totalMobs, float additionalMobs, ArrayList<Contractor> contractorList) {

		setCounty(county);
		setHighway(highway);
		setCsj(csj);
		setWorkingDays(workingDays);

		setUpTo_Mobs(upTo_Mobs);
		setTotalMobs(totalMobs);
		setAdditionalMobs(additionalMobs);

		if (lineItems != null) {
			setLineItems(lineItems);
		}

		setContractorList(contractorList);

		removeBlacklistedContractors();
	}

	public Job(
			String county, //
			String highway, //
			String csj, //
			int workingDays, //
			int upTo_Mobs, //
			float totalMobs, //
			float additionalMobs, //
			ArrayList<LineItem> lineItems, //
			ArrayList<Contractor> contractorList) {
	}

	// ====================================================================================================
	// Blacklisting
	// ====================================================================================================

	// removes blacklisted contractors i.e. Angel Bros, Austin Bridge and Road, Lone
	// Star, Texas Materials
	public void removeBlacklistedContractors() {

		ArrayList<String> blacklistedContractorNames = new ArrayList<String>();
		ArrayList<Contractor> blacklistedContractors = new ArrayList<Contractor>();

		// populate the blacklist ArrayList with names
		blacklistedContractorNames.add("Angel Brothers");
		blacklistedContractorNames.add("AUSTIN BRIDGE & ROAD SERVICES, LP");
		blacklistedContractorNames.add("LONE STAR PAVING COMPANY");
		blacklistedContractorNames.add("TEXAS MATERIALS GROUP, INC");

		// for every contractor in the ArrayList, compare the name to the blacklisted
		// names
		for (Contractor contractor : contractorList) {

			// checks every index of the blacklist against the name
			for (String blacklistName : blacklistedContractorNames) {

				// compares names against blacklist
				if (contractor.getContractorName().equalsIgnoreCase(blacklistName)) {

					blacklistedContractors.add(contractor); // add blacklisted Contractor object to ArrayList
				}
			}
		}

		// removes blacklisted contractors from contractorList
		for (Contractor contractor : blacklistedContractors) {

			contractorList.remove(contractor);
		}
	}

	// ====================================================================================================
	// Outputting Data
	// ====================================================================================================

	public void printJobInfo() {

		System.out.println("County:		      " + getCounty());
		System.out.println("Highway:	      " + getHighway());
		System.out.println("CSJ:		      " + getCsj());
		System.out.println("Working Days:     " + getWorkingDays());
		System.out.printf("Up to %d Mobs%n", getUpTo_Mobs());
		System.out.println("Total Mobs:       " + getTotalMobs());
		System.out.println("Additional Mobs:  " + getAdditionalMobs());

		if (!getLineItems().isEmpty()) {
			System.out.printf("%n%-40s%-12s%8s%n", "Item Description:", "Quantities:", "Price:");
			System.out.println("=".repeat(60));
			for (LineItem lineItem : getLineItems()) {

				System.out.println(lineItem.returnLineItems());
			}
		}

		System.out.printf("%n%-40s%-45s%40s%n", "Contractor Name", "Phone Number", "Email");
		System.out.println("=".repeat(125));
		for (Contractor contractor : getContractorList()) {

			System.out.printf("%-40s%-45s%40s%n", contractor.getContractorName(), contractor.getContractorPhoneNumber(),
					contractor.getContractorEmail());
		}
		System.out.println("=".repeat(125));
		System.out.println("=".repeat(125));
	}

	public String formatJobInfo() {

		String buffer = "";
		// buffer = String.format("|%s|%s|%s", getCounty(), getHighway(), getCsj());
		buffer = String.format("|%s|%s|%s|%d|%d|%4f|%4f", getCounty(), getHighway(), getCsj(), getWorkingDays(),
				getUpTo_Mobs(), getTotalMobs(), getAdditionalMobs());
		if (getLineItems() != null) {

			buffer = String.format("%s|%s", buffer, String.valueOf(getLineItems().size()));

			for (int index = 0; index < getLineItems().size(); index++) {

				buffer = String.format("%s%s", buffer, getLineItems().get(index).formatLineItems());
			}
		}

		buffer = String.format("%s|%s", buffer, String.valueOf(getContractorList().size()));

		for (int index = 0; index < getContractorList().size(); index++) {

			buffer = String.format("%s%s", buffer, getContractorList().get(index).formatContractorInfo());
		}
		return buffer;
	}

	public ArrayList<String> formatUserFriendlyJobInfo() {

		ArrayList<String> job = new ArrayList<String>();

		job.add("County:		      " + getCounty());
		job.add("Highway:	      " + getHighway());
		job.add("CSJ:		      " + getCsj());
		job.add("Working Days:     " + getWorkingDays());

		if (!getLineItems().isEmpty()) {
			job.add(String.format("%n%-40s%-12s%8s%n", "Item Description:", "Quantities:", "Price:"));
			job.add("=".repeat(60));
			for (LineItem lineItem : getLineItems()) {

				job.add(lineItem.returnLineItems());
			}
		}
		return job;
	}

	public ArrayList<String> formatEmailList() {

		ArrayList<String> emails = new ArrayList<String>();

		emails.add(getCounty() + "   " + getCsj());
		emails.add(String.format("Bid Proposal for %s - %s (%s)", getCounty(), getHighway(), getCsj()));
		emails.add("-".repeat(80));
		for (Contractor contractor : getContractorList()) {
			if (!contractor.getContractorEmail().equalsIgnoreCase("=============No Email Found=============")) {
				emails.add(contractor.getContractorEmail());
			} else {
				emails.add(contractor.getContractorEmail() + "   " + contractor.getContractorName());
			}
		}
		emails.add("");
		return emails;
	}

	// ====================================================================================================
	// Getter Setters
	// ====================================================================================================

	public ArrayList<Contractor> getContractorList() {
		return contractorList;
	}

	public void setContractorList(ArrayList<Contractor> contractorList) {
		this.contractorList = contractorList;
	}

	public ArrayList<LineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(ArrayList<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getHighway() {
		return highway;
	}

	public void setHighway(String highway) {
		this.highway = highway;
	}

	public String getCsj() {
		return csj;
	}

	public void setCsj(String csj) {
		this.csj = csj;
	}

	public int getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(int workingDays) {
		this.workingDays = workingDays;
	}

	public int getUpTo_Mobs() {
		return upTo_Mobs;
	}

	public void setUpTo_Mobs(int upTo_Mobs) {
		this.upTo_Mobs = upTo_Mobs;
	}

	public float getTotalMobs() {
		return totalMobs;
	}

	public void setTotalMobs(float totalMobs) {
		this.totalMobs = totalMobs;
	}

	public float getAdditionalMobs() {
		return additionalMobs;
	}

	public void setAdditionalMobs(float additionalMobs) {
		this.additionalMobs = additionalMobs;
	}

	public float getSumOfQuantities() {
		float sum = 0;
		for (LineItem lineItem : getLineItems()) {
			sum += lineItem.getQuantity();
		}
		return sum;
	}
}