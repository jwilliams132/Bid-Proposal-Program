import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class V2Format extends Format {

    final int COUNTY_INDEX = 0;
    final int HIGHWAY_INDEX = 1;
    final int CSJ_INDEX = 2;
    final int WORKING_DAYS_INDEX = 3;
    final int UP_TO_MOBS_INDEX = 4;
    final int TOTAL_MOBS_INDEX = 5;
    final int ADDITIONAL_MOBS_INDEX = 6;
    final int BIDDING_DATE_INDEX = 7;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    final int LINE_ITEM_COUNT_INDEX = 8;
    final int CONTRACTOR_COUNT_INDEX = 9;
    final int START_OF_LINE_ITEMS = 10;

    final int LENGTH_OF_LINE_ITEM = 3;
    final int LINE_ITEM_DESCRIPTION_OFFSET = 0;
    final int LINE_ITEM_QUANTITY_OFFSET = 1;
    final int LINE_ITEM_PRICE_OFFSET = 2;

    final int LENGTH_OF_CONTRACTORS = 4;
    final int CONTRACTOR_NAME_OFFSET = 0;
    final int CONTRACTOR_PHONE_OFFSET = 1;
    final int CONTRACTOR_EMAIL_OFFSET = 2;
    final int ESTIMATE_NO_OF_JOB_OFFSET = 3;

    /**
     * A concrete implementation of the Format interface for handling data in the V2
     * Format.
     */
    public static final String fileHeader = "----V2 FORMAT----";

    /**
     * Default constructor for V2Format.
     */
    public V2Format() {

    }

    /**
     * Constructor for V2Format that takes a Test enum.
     *
     * @param test A Test enum value (not used in this constructor).
     */
    public V2Format(Test test) {

        FileManager fileManager = new FileManager();
        File file = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, null);
        ArrayList<String> fileContents = fileManager.readFile(file);

        List<Job> jobs = jobsFromFormat(fileContents);
        List<String> jobStrings = jobsToFormat(jobs);

        for (int i = 0; i < fileContents.size(); i++) {

            if (fileContents.get(i).equals(jobStrings.get(i))) {

                System.out.println("job: " + i + " :good");
            } else {
                System.out.println(fileContents.get(i));
                System.out.println(jobStrings.get(i));
            }
        }
    }

    /**
     * Converts a Job object into a formatted string in V2Format.
     *
     * <p>
     * This method takes a Job object and converts it into a formatted string
     * following
     * the V2Format structure. The resulting string can be saved or transmitted as
     * V2Format data.
     *
     * @param job The Job object to be formatted.
     * @return A formatted string representing the Job object in V2Format.
     */
    @Override
    public String jobToFormat(Job job) {

        ArrayList<String> result = new ArrayList<>();

        result.add(COUNTY_INDEX, job.getCounty());
        result.add(HIGHWAY_INDEX, job.getHighway());
        result.add(CSJ_INDEX, job.getCsj());
        result.add(WORKING_DAYS_INDEX, String.valueOf(job.getWorkingDays()));
        result.add(UP_TO_MOBS_INDEX, String.valueOf(job.getUpTo_Mobs()));
        result.add(TOTAL_MOBS_INDEX, String.valueOf(job.getTotalMobs()));
        result.add(ADDITIONAL_MOBS_INDEX, String.valueOf(job.getAdditionalMobs()));
        result.add(BIDDING_DATE_INDEX, dateFormat.format(job.getBiddingDate()));
        List<LineItem> lineItems = job.getLineItems();
        List<Contractor> contractors = job.getContractorList();

        final int LINE_ITEM_COUNT = lineItems.size();
        final int CONTRACTOR_COUNT = contractors.size();

        final int START_OF_CONTRACTORS = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM;

        result.add(LINE_ITEM_COUNT_INDEX, String.valueOf(LINE_ITEM_COUNT));
        result.add(CONTRACTOR_COUNT_INDEX, String.valueOf(CONTRACTOR_COUNT));

        for (int lineItem = 0; lineItem < LINE_ITEM_COUNT; lineItem++) {

            int starOfThisLineItem = START_OF_LINE_ITEMS + lineItem * LENGTH_OF_LINE_ITEM;

            result.add(starOfThisLineItem + LINE_ITEM_DESCRIPTION_OFFSET,
                    lineItems.get(lineItem).getDescription());

            result.add(starOfThisLineItem + LINE_ITEM_QUANTITY_OFFSET,
                    String.valueOf(lineItems.get(lineItem).getQuantity()));

            result.add(starOfThisLineItem + LINE_ITEM_PRICE_OFFSET,
                    String.format("%.2f", lineItems.get(lineItem).getPrice()));
        }

        for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

            int startOfThisContractor = START_OF_CONTRACTORS + contractor * LENGTH_OF_CONTRACTORS;

            result.add(startOfThisContractor + CONTRACTOR_NAME_OFFSET,
                    contractors.get(contractor).getContractorName());

            result.add(startOfThisContractor + CONTRACTOR_PHONE_OFFSET,
                    contractors.get(contractor).getContractorPhoneNumber());

            result.add(startOfThisContractor + CONTRACTOR_EMAIL_OFFSET,
                    contractors.get(contractor).getContractorEmail());

            result.add(startOfThisContractor + ESTIMATE_NO_OF_JOB_OFFSET,
                    contractors.get(contractor).getContractorEstimateNo());
        }

        StringBuilder resultString = new StringBuilder();
        for (String infoLine : result) {

            resultString.append(infoLine).append('|');
        }
        resultString.deleteCharAt(resultString.length() - 1);
        return resultString.toString();
    }

    /**
     * Parses a formatted job data string in the V2Format and creates a Job object.
     *
     * <p>
     * This method is specific to the V2Format and is used to parse a job data
     * string
     * in V2Format format and construct a Job object from it.
     *
     * @param jobLineString A formatted string representing job data in V2Format.
     * @return A Job object parsed from the input jobLineString.
     */
    @Override
    public Job jobFromFormat(String jobLineString) {

        String[] tokens = jobLineString.split("\\|");

        final int LINE_ITEM_COUNT = Integer.parseInt(tokens[LINE_ITEM_COUNT_INDEX]);
        final int CONTRACTOR_COUNT = Integer.parseInt(tokens[CONTRACTOR_COUNT_INDEX]);
        final int START_OF_CONTRACTORS = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM;

        List<LineItem> lineItems = new ArrayList<>();
        for (int lineItem = 0; lineItem < LINE_ITEM_COUNT; lineItem++) {

            int starOfThisLineItem = START_OF_LINE_ITEMS + lineItem * LENGTH_OF_LINE_ITEM;

            lineItems.add(new LineItem(
                    tokens[starOfThisLineItem + LINE_ITEM_DESCRIPTION_OFFSET],
                    new BigDecimal(tokens[starOfThisLineItem + LINE_ITEM_QUANTITY_OFFSET]),
                    new BigDecimal(tokens[starOfThisLineItem + LINE_ITEM_PRICE_OFFSET])));
        }

        List<Contractor> contractors = new ArrayList<>();
        for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

            int startOfThisContractor = START_OF_CONTRACTORS + contractor * LENGTH_OF_CONTRACTORS;
            contractors.add(new Contractor(
                    tokens[startOfThisContractor + CONTRACTOR_NAME_OFFSET],
                    tokens[startOfThisContractor + CONTRACTOR_PHONE_OFFSET],
                    tokens[startOfThisContractor + CONTRACTOR_EMAIL_OFFSET],
                    tokens[startOfThisContractor + ESTIMATE_NO_OF_JOB_OFFSET]));
        }
        Date biddingDate = null;
        try {

            dateFormat.parse(tokens[BIDDING_DATE_INDEX]);
        } catch (Exception e) {

            // TODO: handle exception
        }
        return new Job(
                tokens[COUNTY_INDEX], // String county
                tokens[HIGHWAY_INDEX], // String highway
                tokens[CSJ_INDEX], // String csj
                Integer.parseInt(tokens[WORKING_DAYS_INDEX]), // int workingDays
                Integer.parseInt(tokens[UP_TO_MOBS_INDEX]), // int upTo_Mobs
                new BigDecimal(tokens[TOTAL_MOBS_INDEX]), // BigDecimal totalMobs
                new BigDecimal(tokens[ADDITIONAL_MOBS_INDEX]), // BigDecimal additionalMobs
                biddingDate,
                lineItems, // ArrayList<LineItem> lineItems
                contractors);
    }

    @Override
    public List<String> jobsToFormat(List<Job> jobs) {

        ArrayList<String> jobLineStrings = new ArrayList<String>();
        String buffer;
        jobLineStrings.add(fileHeader);
        for (Job job : jobs) {

            buffer = jobToFormat(job);
            System.out.println(buffer.length());
            if (buffer.length() > 500) {

                jobLineStrings.add(buffer.substring(0, 500));
                jobLineStrings.add(buffer.substring(500, buffer.length()));
            } else {

                jobLineStrings.add(buffer);
                jobLineStrings.add("PLACEHOLDER");
            }

        }
        return jobLineStrings;
    }

    @Override
    public List<Job> jobsFromFormat(List<String> jobLineStrings) {

        ArrayList<Job> jobs = new ArrayList<Job>();
        StringBuilder jobString;
        System.out.println(jobLineStrings.size());
        jobLineStrings.forEach(x -> System.out.println(x));
        for (int index = 0; index < jobLineStrings.size(); index += 2) {

            jobString = new StringBuilder(jobLineStrings.get(index));
            jobString.append(jobLineStrings.get(index + 1).equals("PLACEHOLDER") ? "" : jobLineStrings.get(index + 1));
            // System.out.println(jobString.toString());
            jobs.add(jobFromFormat(jobString.toString()));
        }

        return jobs;
    }
}
