package gearworks;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileFormat_TxDot_Single {

	public Job jobFromFormat(File file) {

		FileManager fileManager = new FileManager();
		List<String> fileContents = fileManager.readFile(file);
		// fileContents.forEach(line -> System.out.println(line));

		List<Contractor> contractorList = new ArrayList<Contractor>();
		List<LineItem> lineItems = new ArrayList<LineItem>();
		String county = "", highway = "", csj = "";
		Date biddingDate = null;
		int workingDays = 0;
		boolean lineItemStart = false, contractorStart = false;

		for (int index = 0; index < fileContents.size(); index++) {
			String line = fileContents.get(index);
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
					line = fileContents.get(index);
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
					System.out.println(county);
					if (fileContents.size() > index + 1 && fileContents.get(index + 1).isBlank()) {
						lineItemStart = false;
					}
				}

				// start the contractor count
				if (line.startsWith("PLANHOLDERS")) {

					contractorStart = true;
					index += 2;
					line = fileContents.get(index).trim();
				}
				if (contractorStart) { // add each contractor to array

					if (line.startsWith("*****")) {// if the current element in the job list starts with "*****",
													// increment the index
						index++;
						line = fileContents.get(index).trim();
					}

					/*
					 * create a new Contractor object with the current element in the job list and
					 * either the next element in the job list if it starts with "EMAIL", or a
					 * string indicating that no email was found then add the contractor to the
					 * contractorList array
					 */
					contractorList.add(new Contractor(
							line + "  "
									+ (fileContents.size() > index + 1
											&& (fileContents.get(index + 1).trim().startsWith("EMAIL")
													|| fileContents.get(index + 1).trim().startsWith("Email"))
															? fileContents.get(index + 1)
															: "==No Email Found==")));

					// if the next or next-to-next element in the job list is blank, set
					// contractorStart to false
					if (fileContents.size() > index + 2
							&& (fileContents.get(index + 1).isBlank()
									|| fileContents.get(index + 2).isBlank()))
						contractorStart = false;

					// if the next element in the job list starts with "EMAIL", increment the index
					if (fileContents.size() > index + 1 && fileContents.get(index + 1).trim().startsWith("EMAIL")) {
						index++;
						line = fileContents.get(index).trim();
					}
				}
			} catch (StringIndexOutOfBoundsException e) {
				// e.printStackTrace();
				System.out.println("Line Index:  " + index + ("  Line:  " + line));
			} catch (IndexOutOfBoundsException e) {

				System.out.println("=".repeat(50));
				System.out.println(county);
				System.out.println("=".repeat(50));
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("NumberFormatException happened.");
			}
		}
		return new Job(county, highway, csj, workingDays, biddingDate, lineItems, contractorList);
	}

	public List<Job> jobsFromFormat(List<File> files) {

		return files.stream().map(file -> jobFromFormat(file)).collect(Collectors.toList());
	}
}
