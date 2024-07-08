package gearworks;

// import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A concrete implementation of the Format interface for handling data in the
 * Combined Format.
 * This format is specific to data from the Whitley Siddons site.
 */
public class CombinedFormat extends Format {

	/**
	 * The file header specific to the Combined Format.
	 */
	public static final String fileHeader = "COUNTY";

	/**
	 * Default constructor for CombinedFormat.
	 */
	public CombinedFormat() {

	}

	/**
	 * Constructor for CombinedFormat that takes a Test enum.
	 *
	 * @param test A Test enum value (not used in this constructor).
	 */
	// public CombinedFormat(Test test) {

	// FileManager fileManager = new FileManager();
	// File file = fileManager.chooseFile(null, null,
	// FileManager.fileChooserOptions.OPEN, null);
	// List<String> fileContents = fileManager.readFile(file);
	// List<Job> jobs = jobsFromFormat(fileContents);
	// jobs.forEach(job -> job.printJobInfo());
	// }

	/**
	 * Converts a Job object into a formatted job data string in the CombinedFormat.
	 *
	 * <p>
	 * This method is specific to the CombinedFormat and should not be overridden by
	 * subclasses. It converts a Job object into a formatted string that adheres to
	 * the
	 * CombinedFormat structure.
	 *
	 * @param job A Job object to be converted into a CombinedFormat formatted
	 *            string.
	 * @return A formatted string representing the job data in CombinedFormat.
	 * @throws UnsupportedOperationException if an attempt is made to override this
	 *                                       method.
	 */
	@Override
	public String jobToFormat(Job job) {

		throw new UnsupportedOperationException(
				"Files using CombinedFormat are from the Whitley Siddons site and will only ever have info taken from this format, never to.");
	}

	/**
	 * Parses a formatted job data string in the CombinedFormat and creates a Job
	 * object.
	 *
	 * <p>
	 * This method is specific to the CombinedFormat and is used to parse a job data
	 * string
	 * in CombinedFormat format and construct a Job object from it.
	 *
	 * @param jobLineString A formatted string representing job data in
	 *                      CombinedFormat.
	 * @return A Job object parsed from the input jobLineString.
	 */
	@Override
	public Job jobFromFormat(String jobLineString) {

		List<String> job = new ArrayList<String>(Arrays.asList(jobLineString.split("\\|", -1)));

		List<Contractor> contractorList = new ArrayList<Contractor>();
		List<LineItem> lineItems = new ArrayList<LineItem>();
		String county = "", highway = "", csj = "";
		Date biddingDate = null;
		int workingDays = 0;
		boolean lineItemStart = false, contractorStart = false;

		for (int index = 0; index < job.size(); index++) {
			String line = job.get(index);
			try {

				// get data from county line
				if (line.startsWith("COUNTY:")) {

					county = line.substring(8, 32).trim();
					highway = line.substring(41, 59).trim();
					continue;
				}

				// get data from control line
				if (line.startsWith("CONTROL")) {

					csj = line.substring(16, 27);
					continue;
				}

				// get data from working days
				if (line.startsWith("TIME FOR COMPLETION")) {

					Matcher workingDaysMatcher = Pattern.compile(": [0-9]* WORKING DAYS").matcher(line);
					if (workingDaysMatcher.find()) {

						String buffer = workingDaysMatcher.group();
						buffer = buffer.substring(2, buffer.length() - 13).trim();

						if (buffer.length() != 0) {
							workingDays = Integer.valueOf(buffer);
						}
					}
					continue;
				}

				// Check if the line starts with "BIDS RECEIVED UNTIL: "
				if (line.startsWith("BIDS RECEIVED UNTIL:  ")) {
					// Define a regular expression pattern to match the date and time
					String datePattern = "\\d{1,2}:\\d{2}\\s[a-zA-Z]{2}\\s[a-zA-Z]+\\s\\d{1,2},\\s\\d{4}";

					// Create a Pattern object
					Pattern pattern = Pattern.compile(datePattern);

					// Create a Matcher object to find the date and time
					Matcher matcher = pattern.matcher(line);

					// Check if the pattern is found in the line
					if (matcher.find()) {
						// Extract and save the date and time
						String extractedDateTime = matcher.group();

						// Define a SimpleDateFormat to parse the extracted date and time
						SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a MMMM dd, yyyy");

						try {
							biddingDate = dateFormat.parse(extractedDateTime);
							// dateObject now contains the Date representation of the extracted date and
							// time
						} catch (ParseException e) {
							// Handle parsing errors, e.g., if the date format is not as expected
							e.printStackTrace();
						}
					}
					continue;
				}

				// start the line item count
				if (line.trim().startsWith("ITEM DES")) {

					lineItemStart = true;
					index += 3;
					line = job.get(index);
				}

				// add each line item to ArrayList
				if (lineItemStart) {

					if (!line.startsWith("+ DELETED ->")) {

						try {
							
							lineItems.add(new LineItem(
									line.substring(5, 9).trim(),
									line.substring(10, 14).trim(),
									// line.substring(15, 18).trim(), // Special No.
									line.substring(19, 58).trim(),
									line.substring(59, 62).trim(),
									new BigDecimal(line.substring(63, 78).trim().replaceAll(",", "")),
									new BigDecimal(0)));
						} catch (NumberFormatException e) {

							System.out.println("spec.:  " + line.substring(5, 18).trim());
							System.out.println("desc.:  " + line.substring(19, 58).trim());
							System.out.println("quan.:  " + line.substring(63, 78).trim().replaceAll(",", ""));
							// Handle the NumberFormatException
							System.err.println("NumberFormatException occurred: " + e.getMessage());
							// You can also throw a custom exception if needed
						}
					}

					if (job.get(index + 1).isBlank()) {
						lineItemStart = false;
					}
				}
				
				// start the contractor count
				if (line.startsWith("PLANHOLDERS")) {

					contractorStart = true;
					index += 2;
					line = job.get(index).trim();
				}
				if (csj.equals("0978-01-039"))
					System.out.println("bitch");
				if (contractorStart) { // add each contractor to array

					if (line.startsWith("*****")) {// if the current element in the job list starts with "*****",
													// increment the index
						index++;
						line = job.get(index).trim();
					}

					/*
					 * create a new Contractor object with the current element in the job list and
					 * either the next element in the job list if it starts with "EMAIL", or a
					 * string indicating that no email was found then add the contractor to the
					 * contractorList array
					 */
					contractorList.add(new Contractor(
							line + "  "
									+ (job.get(index + 1).trim().startsWith("EMAIL")
											|| job.get(index + 1).trim().startsWith("Email") ? job.get(index + 1)
													: "==No Email Found==")));

					// if the next or next-to-next element in the job list is blank, set
					// contractorStart to false
					if (job.get(index + 1).isBlank() || job.get(index + 2).isBlank())
						contractorStart = false;

					// if the next element in the job list starts with "EMAIL", increment the index
					if (job.get(index + 1).trim().startsWith("EMAIL")) {
						index++;
						line = job.get(index).trim();
					}
				}
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
				System.out.println("Line Index:  " + index + ("  Line:  " + line));
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException happened.");
			}
		}
		return new Job(county, highway, csj, workingDays, biddingDate, lineItems, contractorList);
	}

	/**
	 * Parses a list of formatted job data strings in the CombinedFormat and creates
	 * a list of Job objects.
	 *
	 * <p>
	 * This method is specific to the CombinedFormat and is used to parse a list of
	 * job data strings
	 * in CombinedFormat format and construct a list of Job objects from them.
	 *
	 * @param contentsByLine A list of formatted strings representing job data in
	 *                       CombinedFormat, where each
	 *                       string represents a single job entry.
	 * @return A list of Job objects parsed from the input job data strings.
	 */
	@Override
	public List<Job> jobsFromFormat(List<String> contentsByLine) {

		final String END_OF_JOB_DELIMITER = "================================================================================";

		StringBuilder jobLineString = new StringBuilder();
		List<Job> jobs = new ArrayList<Job>();
		for (String nextLine : contentsByLine) {
			if (nextLine.startsWith(END_OF_JOB_DELIMITER)) {

				jobs.add(jobFromFormat(jobLineString.toString()));
				jobLineString = new StringBuilder();
				continue;
			} else {

				jobLineString.append(nextLine).append("|");
			}
		}
		return jobs;
	}
}
