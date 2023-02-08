import java.io.File;
import java.util.ArrayList;

public class ParseBidderDoc {

	private ArrayList<Job> jobList = new ArrayList<Job>();
	private String bidderTestFilePath = "C:\\Users\\Jacob\\Documents\\IDE\\Eclipse Workspace Directory\\Bid Proposal Program\\BidderData Combined October.txt";
	private final FileManager fileManager = new FileManager();
	private File bidderTestFile = fileManager.chooseFile(bidderTestFilePath, null, FileManager.fileChooserOptions.OPEN, null);

	public static void main(String[] args) {

		ParseBidderDoc parseBidderDoc = new ParseBidderDoc();
		parseBidderDoc.jobList = parseBidderDoc.separateBidderFile(parseBidderDoc.fileManager.readFile(parseBidderDoc.bidderTestFile));
	}

	public ParseBidderDoc() {

	}

	/*
	 * This function separates a list of strings into jobs and adds them to an array
	 * of job strings
	 */
	public ArrayList<Job> separateBidderFile(ArrayList<String> bidderContentsByLine) {

		// Initialize an empty list of Job objects
		ArrayList<Job> jobs = new ArrayList<Job>();

		// Initialize an empty list of contractors
		ArrayList<Contractor> contractors = new ArrayList<Contractor>();

		// Initialize empty strings for the current job's county, highway, and CSJ
		String county = "", highway = "", csj = "";

		// iterate until the first job
		int lineIndex = 0;
		while (!bidderContentsByLine.get(lineIndex).startsWith("#"))
			lineIndex++;

		// If the line starts with "#", extract the county, CSJ, and highway information
		for (; lineIndex < bidderContentsByLine.size(); lineIndex++) {

			if (bidderContentsByLine.get(lineIndex).startsWith("#")) {

				String[] tokens = bidderContentsByLine.get(lineIndex).split(",");
				county = tokens[0].substring(7, tokens[0].length());
				csj = tokens[1].substring(3, 14);
				highway = tokens[2].trim();
				continue;
			}

			// Ignore lines starting with "*****"
			if (bidderContentsByLine.get(lineIndex).startsWith("*****"))
				continue;

			/*
			 * If the line is empty or is the last line of the file, assume that the current
			 * job is complete and reset the Job object
			 */
			if (bidderContentsByLine.get(lineIndex).isEmpty() || lineIndex + 1 == bidderContentsByLine.size()) {

				jobs.add(new Job(county, highway, csj, 0, null, contractors));
				contractors = new ArrayList<Contractor>();
				continue;
			}

			// Ignore lines starting with "Email"
			if (bidderContentsByLine.get(lineIndex).startsWith("Email"))
				continue;

			/*
			 * If it is the last job's contractor and it has an email, create a new
			 * Contractor object using both lines and add it to the list of contractors for
			 * the current job
			 */
			if (bidderContentsByLine.size() - lineIndex == 1
					|| bidderContentsByLine.get(lineIndex + 1).startsWith("Email"))

				contractors.add(new Contractor(
						bidderContentsByLine.get(lineIndex) + " " + bidderContentsByLine.get(lineIndex + 1)));

			// Otherwise, create a new Contractor object using only the current line and add
			// it to the list of contractors for the current job
			else

				contractors.add(new Contractor(bidderContentsByLine.get(lineIndex)));
		}

		// return ArrayList<Job>
		return new ArrayList<Job>(jobs);
	}
}