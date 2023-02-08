import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseFullDoc {

	public static void main(String[] args) {

//		FileManager manager = new FileManager();
//		File file1 = new File("C:\\Users\\Jacob\\Desktop\\Letting\\Test Letting\\Testtext.txt");
//		File file2 = new File("C:\\Users\\Jacob\\Desktop\\Letting\\Test Letting\\Testtext2.txt");
//
//		List<String> l1 = manager.readFile(file1);
//		List<String> l2 = manager.readFile(file2);
//
//		for (int i = 0; i < (l1.size() < l2.size() ? l2.size() : l1.size()); i++) {
//			if (l1.get(i).equals(l2.get(i))) {
//				System.out.println("match");
//			} else {
//				System.out.println();
//				System.out.println(l1.get(i));
//				System.out.println(l2.get(i));
//				System.out.println();
//			}
//		}
	}

	private FileManager fileManager = new FileManager();
	private Audit audit;

	private ArrayList<ArrayList<String>> arrayOfJobStrings;
	private ArrayList<Job> jobList = new ArrayList<Job>();

	private ArrayList<String> contentsByLine;
	private File inputFile = null;

	private String bidFileType;

	public ParseFullDoc() {

	}

	public void parseData() {

		importFileData(inputFile);
		separateJobs();
		for (ArrayList<String> job : arrayOfJobStrings) {

			jobList.add(extractJobData(job));
		}
		audit.add("All job data extracted.");
	}

	public void importFileData(File file) {

		audit.add("Tries to open file.");
		contentsByLine = fileManager.readFile(file);
		audit.add("	File opened successfully.");
	}

	// creates new file and saves Job data into it
	public void exportDataFiles(File formattedOutput, File userFriendlyOutput, File emailList) {

		audit.add("Tries to save file.");

		ArrayList<String> formattedOutputBuffer = new ArrayList<String>();
		ArrayList<String> userFriendlyOutputBuffer = new ArrayList<String>();
		ArrayList<String> emailListBuffer = new ArrayList<String>();

		// add all job data to fileContentBuffer
		for (Job job : jobList) {

			formattedOutputBuffer.add(job.formatJobInfo()); // add data to file

			userFriendlyOutputBuffer.addAll(job.formatUserFriendlyJobInfo());
			userFriendlyOutputBuffer.add("-".repeat(100));

			emailListBuffer.addAll(job.formatEmailList());
		}
		fileManager.saveFile(formattedOutput, formattedOutputBuffer);
		fileManager.saveFile(userFriendlyOutput, userFriendlyOutputBuffer);
		fileManager.saveFile(emailList, emailListBuffer);

		audit.add("	File saved successfully.");
	}

	public void separateJobs() {

		arrayOfJobStrings = new ArrayList<ArrayList<String>>();
		ArrayList<String> job = new ArrayList<String>();

		final String DELIMITER = "|";
		final String NEW_JOB_DELIMITER = "==========";
		if (contentsByLine.get(0).startsWith(DELIMITER)) {

			audit.add("Previous file loaded.");
			setBidFileType("Previous file");
			for (String nextLine : contentsByLine) {

				arrayOfJobStrings.add(
						new ArrayList<String>(Arrays.asList(nextLine.substring(1, nextLine.length()).split("\\|"))));
			}
			return;
		}

		for (String nextLine : contentsByLine) {

			if (nextLine.startsWith(NEW_JOB_DELIMITER)) {

				arrayOfJobStrings.add(job);
				job = new ArrayList<String>();
			} else {

				job.add(nextLine);

			}
		}
		audit.add("New file loaded.");
		setBidFileType("New file");
	}

	public Job extractJobData(ArrayList<String> job) {

		final String FORMATTED_FILE_IDENTIFIER = "|";

		if (contentsByLine.get(0).startsWith(FORMATTED_FILE_IDENTIFIER))
			return separateJobInfoFromPrevious(job);

		return separateJobInfoFromNew(job);
	} // end of public Job extractJobData(ArrayList<String> job)

	public Job separateJobInfoFromNew(ArrayList<String> job) {

		ArrayList<Contractor> contractorList = new ArrayList<Contractor>();
		ArrayList<LineItem> lineItems = new ArrayList<LineItem>();
		String county = "", highway = "", csj = "";
		int workingDays = 0;
		boolean lineItemStart = false, contractorStart = false;

		for (int index = 0; index < job.size(); index++) {

			// get data from county line
			if (job.get(index).startsWith("COUNTY")) {

				county = job.get(index).substring(8, 32).trim();
				highway = job.get(index).substring(41, 59).trim();
			}

			// get data from control line
			if (job.get(index).startsWith("CONTROL")) {

				csj = job.get(index).substring(16, 27);
			}

			// get data from working days
			if (job.get(index).startsWith("TIME FOR COMPLETION")) {

				Matcher workingDaysMatcher = Pattern.compile(": [0-9]* WORKING DAYS").matcher(job.get(index));
				if (workingDaysMatcher.find()) {

					String buffer = workingDaysMatcher.group();
					buffer = buffer.substring(2, buffer.length() - 13).trim();

					if (buffer.length() != 0) {
						workingDays = Integer.valueOf(buffer);
					}
				}
			}

			// start the line item count
			if (job.get(index).startsWith("ITEM DES")) {

				lineItemStart = true;
				index = index + 3;
			}

			// add each line item to ArrayList
			if (lineItemStart) {

				if (!job.get(index).startsWith("+ DELETED ->"))
					lineItems.add(new LineItem(job.get(index).substring(13, 53).trim(),
							Float.valueOf(job.get(index).substring(55, 72).trim().replaceAll(",", "")), 0));

				if (job.get(index + 1).isBlank()) {
					lineItemStart = false;
				}
			}

			// start the contractor count
			if (job.get(index).startsWith("PLANHOLDERS")) {

				contractorStart = true;
				index = index + 2;
			}

			if (contractorStart) { // add each contractor to array

				if (job.get(index).startsWith("*****")) // if the current element in the job list starts with "*****",
														// increment the index
					index++;

				/*
				 * create a new Contractor object with the current element in the job list and
				 * either the next element in the job list if it starts with "EMAIL", or a
				 * string indicating that no email was found then add the contractor to the
				 * contractorList array
				 */
				contractorList.add(new Contractor(
						job.get(index) + "  " + (job.get(index + 1).trim().startsWith("EMAIL") ? job.get(index + 1)
								: "=============No Email Found=============")));

				// if the next or next-to-next element in the job list is blank, set
				// contractorStart to false
				if (job.get(index + 1).isBlank() || job.get(index + 2).isBlank())
					contractorStart = false;

				// if the next element in the job list starts with "EMAIL", increment the index
				if (job.get(index + 1).trim().startsWith("EMAIL"))
					index++;
			}

		} // end of for(i = 0; i < job.size(); i++)
		audit.add("Loaded job:  " + csj + " " + county);

		return new Job(county, highway, csj, workingDays, lineItems, contractorList);
	}

	public Job separateJobInfoFromPrevious(ArrayList<String> job) {

		ArrayList<Contractor> contractorList = new ArrayList<Contractor>();
		ArrayList<LineItem> lineItems = new ArrayList<LineItem>();
		String county = "", highway = "", csj = "";
		int workingDays = 0, upToMobs = 0;
		float totalMobs = 0, additionalMobs = 0;

		county = job.get(0);
		highway = job.get(1);
		csj = job.get(2);
		workingDays = Integer.valueOf(job.get(3));
		upToMobs = Integer.valueOf(job.get(4));
		totalMobs = Float.valueOf(job.get(5));
		additionalMobs = Float.valueOf(job.get(6));

		final int START_OF_LINEITEMS_SECTION_INDEX = 7;
		int numOfLineItems = Integer.valueOf(job.get(START_OF_LINEITEMS_SECTION_INDEX));
		int ListIndexString;

		String description;
		float quantity;
		float price;

		final int LENGTH_OF_LINE_ITEM = 3;
		final int LINE_ITEM_COUNT_LINE = 1;
		final int QUANTITY_OFFSET = 1;
		final int PRICE_OFFSET = 2;

		for (int lineItemIndex = 0; lineItemIndex < numOfLineItems; lineItemIndex++) {

			ListIndexString = START_OF_LINEITEMS_SECTION_INDEX + lineItemIndex * LENGTH_OF_LINE_ITEM
					+ LINE_ITEM_COUNT_LINE;

			description = job.get(ListIndexString);
			quantity = Float.valueOf(job.get(ListIndexString + QUANTITY_OFFSET));
			price = Float.valueOf(job.get(ListIndexString + PRICE_OFFSET));

			lineItems.add(new LineItem(description, quantity, price));
		}

		String name;
		String email;
		String phone;

		final int LENGTH_OF_CONTRACTORS = 3;
		final int CONTRACTOR_COUNT_LINE = 1;
		final int PHONE_OFFSET = 1;
		final int EMAIL_OFFSET = 2;

		int startOfContractorsSectionIndex = START_OF_LINEITEMS_SECTION_INDEX + LINE_ITEM_COUNT_LINE
				+ numOfLineItems * LENGTH_OF_LINE_ITEM;
		int numOfContractors = Integer.valueOf(job.get(startOfContractorsSectionIndex));

		for (int contractorIndex = 0; contractorIndex < numOfContractors; contractorIndex++) {

			ListIndexString = startOfContractorsSectionIndex + contractorIndex * LENGTH_OF_CONTRACTORS
					+ CONTRACTOR_COUNT_LINE;

			name = job.get(ListIndexString);
			phone = job.get(ListIndexString + PHONE_OFFSET);
			email = job.get(ListIndexString + EMAIL_OFFSET);
			contractorList.add(new Contractor(name, phone, email));
		}

		audit.add("Loaded job:  " + csj + " " + county);
		return new Job(county, highway, csj, workingDays, lineItems, upToMobs, totalMobs, additionalMobs,
				contractorList);
	}

	// ====================================================================================================
	// Updating the Bidders
	// ====================================================================================================

	/*
	 * This function separates a list of strings into jobs and adds them to an array
	 * of job strings
	 */
	public ArrayList<Job> separateBidderFile(File bidderFile) {

		ArrayList<String> bidderContentsByLine = fileManager.readFile(bidderFile);
		ArrayList<Job> jobs = new ArrayList<Job>();
		ArrayList<Contractor> contractors = new ArrayList<Contractor>();
		String county = "", highway = "", csj = "";
		String NEW_JOB_DELIMITER = "#";

		int lineIndex = 0;

		boolean firstJob = bidderContentsByLine.get(lineIndex).startsWith(NEW_JOB_DELIMITER);

		while (firstJob)
			lineIndex++;

		for (; lineIndex < bidderContentsByLine.size(); lineIndex++) {

			if (bidderContentsByLine.get(lineIndex).startsWith(NEW_JOB_DELIMITER)) {

				String[] tokens = bidderContentsByLine.get(lineIndex).split(",");
				county = tokens[0].substring(7, tokens[0].length());
				csj = tokens[1].substring(3, 14);
				highway = tokens[2].trim();
				continue;
			}

			if (bidderContentsByLine.get(lineIndex).startsWith("*****"))
				continue;

			boolean lineIsEmpty = bidderContentsByLine.get(lineIndex).isEmpty();
			boolean isLastLine = lineIndex + 1 == bidderContentsByLine.size();

			if (lineIsEmpty || isLastLine) {

				jobs.add(new Job(county, highway, csj, 0, null, contractors));
				contractors = new ArrayList<Contractor>();
				continue;
			}

			if (bidderContentsByLine.get(lineIndex).startsWith("Email"))
				continue;

			boolean ifLastContractor = bidderContentsByLine.size() - lineIndex == 1;
			boolean ifContractorHasEmail = bidderContentsByLine.get(lineIndex + 1).startsWith("Email");

			if (ifLastContractor || ifContractorHasEmail) {

				contractors.add(new Contractor(
						bidderContentsByLine.get(lineIndex) + " " + bidderContentsByLine.get(lineIndex + 1)));
			} else {

				contractors.add(new Contractor(bidderContentsByLine.get(lineIndex)));
			}
		}

		// return ArrayList<Job>
		return new ArrayList<Job>(jobs);
	}

	public void updateBidders(File bidderFile) {

		ArrayList<Job> updatedBiddersJobs = separateBidderFile(bidderFile);

		for (Job updatedBiddersJob : updatedBiddersJobs)

			for (Job job : jobList)

				if (job.getCsj().equals(updatedBiddersJob.getCsj()))

					job.setContractorList(updatedBiddersJob.getContractorList());
	}

	public ArrayList<String> createMailingList() {

		return null;
	}

	// ====================================================================================================
	// Getter/Setters
	// ====================================================================================================

	public void setAudit(Audit audit) {
		this.audit = audit;
	}

	public Audit getAudit() {
		return audit;
	}

	public File getNewInputFile() {
		return inputFile;
	}

	public void setNewInputFile(File file) {
		inputFile = file;
	}

	public ArrayList<Job> getJobList() {
		return jobList;
	}

	public void setJobList(ArrayList<Job> selectedJobList) {
		jobList = selectedJobList;
	}

	public String getBidFileType() {
		return bidFileType;
	}

	public void setBidFileType(String bidFileType) {
		this.bidFileType = bidFileType;
	}
}