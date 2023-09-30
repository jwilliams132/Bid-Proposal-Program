import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CombinedFormat extends Format {

    public static final String fileHeader = "COUNTY";

    public CombinedFormat() {

    }

    public CombinedFormat(Test test) {

        FileManager fileManager = new FileManager();
        File file = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, null);
        List<String> fileContents = fileManager.readFile(file);
        List<Job> jobs = jobsFromFormat(fileContents);
        jobs.forEach(job -> job.printJobInfo());
    }

    @Override
    public String jobToFormat(Job job) {

        throw new UnsupportedOperationException(
                "Files using CombinedFormat are from the Whitley Siddons site and will only ever have info taken from this format, never to.");
    }

    @Override
    public Job jobFromFormat(String jobLineString) {

        List<String> job = new ArrayList<String>(Arrays.asList(jobLineString.split("\\|")));

        List<Contractor> contractorList = new ArrayList<Contractor>();
        List<LineItem> lineItems = new ArrayList<LineItem>();
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
        }
        return new Job(county, highway, csj, workingDays, lineItems, contractorList);
    }

    @Override
    public List<Job> jobsFromFormat(List<String> contentsByLine) {

        final String END_OF_JOB_DELIMITER = "=".repeat(80);

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
