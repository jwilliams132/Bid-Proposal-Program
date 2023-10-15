import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A concrete implementation of the Format interface for handling data in the V1
 * Format.
 */
public class V1Format extends Format {

    final int COUNTY_INDEX = 0;
    final int HIGHWAY_INDEX = 1;
    final int CSJ_INDEX = 2;
    final int WORKING_DAYS_INDEX = 3;
    final int UP_TO_MOBS_INDEX = 4;
    final int TOTAL_MOBS_INDEX = 5;
    final int ADDITIONAL_MOBS_INDEX = 6;

    final int LINE_ITEM_COUNT_INDEX = 7;
    final int START_OF_LINE_ITEMS = 8;

    final int LENGTH_OF_LINE_ITEM = 3;
    final int LINE_ITEM_DESCRIPTION_OFFSET = 0;
    final int LINE_ITEM_QUANTITY_OFFSET = 1;
    final int LINE_ITEM_PRICE_OFFSET = 2;

    final int CONTRACTOR_COUNT_FORMAT_PLACEHOLDER = 1;

    final int LENGTH_OF_CONTRACTORS = 3;
    final int CONTRACTOR_NAME_OFFSET = 0;
    final int CONTRACTOR_PHONE_OFFSET = 1;
    final int CONTRACTOR_EMAIL_OFFSET = 2;

    /**
     * The file header specific to the V1 Format.
     */
    public static final String fileHeader = "|";

    /**
     * Default constructor for V1Format.
     */
    public V1Format() {

    }

    /**
     * Constructor for V1Format that takes a Test enum.
     *
     * @param test A Test enum value (not used in this constructor).
     */
    public V1Format(Test test) {

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
     * Converts a Job object into a formatted string using the V1Format.
     *
     * <p>
     * This method takes a Job object and converts it into a formatted string that
     * adheres
     * to the V1Format structure. It includes county information, highway
     * information, line items,
     * and contractor details.
     *
     * @param job The Job object to be converted into a V1Format string.
     * @return A formatted string in V1Format representing the provided Job object.
     */
    @Override
    public String jobToFormat(Job job) {

        ArrayList<String> result = new ArrayList<>();

        result.add(COUNTY_INDEX, job.getCounty());
        result.add(HIGHWAY_INDEX, job.getHighway());
        result.add(CSJ_INDEX, job.getCsj());
        result.add(WORKING_DAYS_INDEX, String.valueOf(job.getWorkingDays()));
        result.add(UP_TO_MOBS_INDEX, String.valueOf(job.getUpTo_Mobs()));
        result.add(TOTAL_MOBS_INDEX, String.format("%.6f", job.getTotalMobs()));
        result.add(ADDITIONAL_MOBS_INDEX, String.format("%.6f", job.getAdditionalMobs()));

        List<LineItem> lineItems = job.getLineItems();
        List<Contractor> contractors = job.getContractorList();

        final int LINE_ITEM_COUNT = lineItems.size();
        final int CONTRACTOR_COUNT = contractors.size();

        final int CONTRACTOR_COUNT_INDEX = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM;

        final int START_OF_CONTRACTORS = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM
                + CONTRACTOR_COUNT_FORMAT_PLACEHOLDER;

        result.add(LINE_ITEM_COUNT_INDEX, String.valueOf(LINE_ITEM_COUNT));

        for (int lineItem = 0; lineItem < LINE_ITEM_COUNT; lineItem++) {

            int starOfThisLineItem = START_OF_LINE_ITEMS + lineItem * LENGTH_OF_LINE_ITEM;

            result.add(starOfThisLineItem + LINE_ITEM_DESCRIPTION_OFFSET,
                    lineItems.get(lineItem).getDescription());

            result.add(starOfThisLineItem + LINE_ITEM_QUANTITY_OFFSET,
                    String.valueOf(lineItems.get(lineItem).getQuantity()));

            result.add(starOfThisLineItem + LINE_ITEM_PRICE_OFFSET,
                    String.format("%.2f", lineItems.get(lineItem).getPrice()));
        }

        result.add(CONTRACTOR_COUNT_INDEX, String.valueOf(CONTRACTOR_COUNT));

        for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

            int startOfThisContractor = START_OF_CONTRACTORS + contractor * LENGTH_OF_CONTRACTORS;

            result.add(startOfThisContractor + CONTRACTOR_NAME_OFFSET,
                    contractors.get(contractor).getContractorName());

            result.add(startOfThisContractor + CONTRACTOR_PHONE_OFFSET,
                    contractors.get(contractor).getContractorPhoneNumber());

            result.add(startOfThisContractor + CONTRACTOR_EMAIL_OFFSET,
                    contractors.get(contractor).getContractorEmail());
        }

        StringBuilder resultString = new StringBuilder();
        resultString.append("|");
        for (String infoLine : result) {

            resultString.append(infoLine).append('|');
        }
        resultString.deleteCharAt(resultString.length() - 1);
        return resultString.toString();
    }

    /**
     * Parses a formatted string in V2Format and converts it into a Job object.
     *
     * <p>
     * This method takes a formatted string in the V2Format and parses it to create
     * a
     * corresponding Job object. It expects the input string to follow the V2Format
     * structure.
     *
     * @param jobLineString The formatted string in V2Format to be parsed into a Job
     *                      object.
     * @return A Job object representing the data parsed from the input string.
     */
    @Override
    public Job jobFromFormat(String jobLineString) {

        // Remove the first character from jobLineString
        jobLineString = jobLineString.substring(1);

        String[] tokens = jobLineString.split("\\|");

        final int LINE_ITEM_COUNT = Integer.parseInt(tokens[LINE_ITEM_COUNT_INDEX]);

        final int CONTRACTOR_COUNT_INDEX = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM;
        final int CONTRACTOR_COUNT = Integer.parseInt(tokens[CONTRACTOR_COUNT_INDEX]);
        final int START_OF_CONTRACTORS = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM
                + CONTRACTOR_COUNT_FORMAT_PLACEHOLDER;

        ArrayList<LineItem> lineItems = new ArrayList<>();
        for (int lineItem = 0; lineItem < LINE_ITEM_COUNT; lineItem++) {

            int starOfThisLineItem = START_OF_LINE_ITEMS + lineItem * LENGTH_OF_LINE_ITEM;
            lineItems.add(new LineItem(
                    tokens[starOfThisLineItem + LINE_ITEM_DESCRIPTION_OFFSET],
                    new BigDecimal(tokens[starOfThisLineItem + LINE_ITEM_QUANTITY_OFFSET]),
                    new BigDecimal(tokens[starOfThisLineItem + LINE_ITEM_PRICE_OFFSET])));
        }

        ArrayList<Contractor> contractors = new ArrayList<>();
        for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

            int startOfThisContractor = START_OF_CONTRACTORS + contractor * LENGTH_OF_CONTRACTORS;

            contractors.add(new Contractor(
                    tokens[startOfThisContractor + CONTRACTOR_NAME_OFFSET],
                    tokens[startOfThisContractor + CONTRACTOR_PHONE_OFFSET],
                    tokens[startOfThisContractor + CONTRACTOR_EMAIL_OFFSET]));
        }

        return new Job(
                tokens[COUNTY_INDEX], // String county
                tokens[HIGHWAY_INDEX], // String highway
                tokens[CSJ_INDEX], // String csj
                Integer.parseInt(tokens[WORKING_DAYS_INDEX]), // int workingDays
                Integer.parseInt(tokens[UP_TO_MOBS_INDEX]), // int upTo_Mobs
                new BigDecimal(tokens[TOTAL_MOBS_INDEX]), // BigDecimal totalMobs
                new BigDecimal(tokens[ADDITIONAL_MOBS_INDEX]), // BigDecimal additionalMobs
                lineItems, // ArrayList<LineItem> lineItems
                contractors);
    }
}
