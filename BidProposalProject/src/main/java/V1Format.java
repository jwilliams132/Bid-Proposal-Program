import java.io.File;
import java.util.ArrayList;

public class V1Format extends Format {

    public V1Format() {

    }

    public V1Format(Test test) {

        FileManager fileManager = new FileManager();
        File file = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, null);
        ArrayList<String> fileContents = fileManager.readFile(file);

        ArrayList<Job> jobs = jobsFromFormat(fileContents);
        ArrayList<String> jobStrings = jobsToFormat(jobs);

        for (int i = 0; i < fileContents.size(); i++) {

            if (fileContents.get(i).equals(jobStrings.get(i))) {

                System.out.println("job: " + i + " :good");
            } else {
                System.out.println(fileContents.get(i));
                System.out.println(jobStrings.get(i));
            }
        }
    }

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

        ArrayList<LineItem> lineItems = job.getLineItems();
        ArrayList<Contractor> contractors = job.getContractorList();

        final int LINE_ITEM_COUNT = lineItems.size();
        final int CONTRACTOR_COUNT = contractors.size();

        final int CONTRACTOR_COUNT_INDEX = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM;

        final int START_OF_CONTRACTORS = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM
                + CONTRACTOR_COUNT_FORMAT_PLACEHOLDER;

        result.add(LINE_ITEM_COUNT_INDEX, String.valueOf(LINE_ITEM_COUNT));

        for (int lineItem = 0; lineItem < LINE_ITEM_COUNT; lineItem++) {

            result.add(START_OF_LINE_ITEMS + LINE_ITEM_DESCRIPTION_OFFSET + lineItem * LENGTH_OF_LINE_ITEM,
                    lineItems.get(lineItem).getDescription());
            result.add(START_OF_LINE_ITEMS + LINE_ITEM_QUANTITY_OFFSET + lineItem * LENGTH_OF_LINE_ITEM,
                    String.valueOf(lineItems.get(lineItem).getQuantity()));
            result.add(START_OF_LINE_ITEMS + LINE_ITEM_PRICE_OFFSET + lineItem * LENGTH_OF_LINE_ITEM,
                    String.format("%.2f", lineItems.get(lineItem).getPrice()));
        }

        result.add(CONTRACTOR_COUNT_INDEX, String.valueOf(CONTRACTOR_COUNT));

        for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

            result.add(START_OF_CONTRACTORS + CONTRACTOR_NAME_OFFSET + contractor * LENGTH_OF_CONTRACTORS,
                    contractors.get(contractor).getContractorName());
            result.add(START_OF_CONTRACTORS + CONTRACTOR_PHONE_OFFSET + contractor * LENGTH_OF_CONTRACTORS,
                    contractors.get(contractor).getContractorPhoneNumber());
            result.add(START_OF_CONTRACTORS + CONTRACTOR_EMAIL_OFFSET + contractor * LENGTH_OF_CONTRACTORS,
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

            lineItems.add(new LineItem(
                    tokens[START_OF_LINE_ITEMS + LINE_ITEM_DESCRIPTION_OFFSET
                            + lineItem * LENGTH_OF_LINE_ITEM],
                    Float.parseFloat(
                            tokens[START_OF_LINE_ITEMS + LINE_ITEM_QUANTITY_OFFSET
                                    + lineItem * LENGTH_OF_LINE_ITEM]),
                    Float.parseFloat(
                            tokens[START_OF_LINE_ITEMS + LINE_ITEM_PRICE_OFFSET
                                    + lineItem * LENGTH_OF_LINE_ITEM])));
        }

        ArrayList<Contractor> contractors = new ArrayList<>();
        for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

            contractors.add(new Contractor(
                    tokens[START_OF_CONTRACTORS + CONTRACTOR_NAME_OFFSET
                            + contractor * LENGTH_OF_CONTRACTORS],
                    tokens[START_OF_CONTRACTORS + CONTRACTOR_PHONE_OFFSET
                            + contractor * LENGTH_OF_CONTRACTORS],
                    tokens[START_OF_CONTRACTORS + CONTRACTOR_EMAIL_OFFSET
                            + contractor * LENGTH_OF_CONTRACTORS]));
        }

        return new Job(
                tokens[COUNTY_INDEX], // String county
                tokens[HIGHWAY_INDEX], // String highway
                tokens[CSJ_INDEX], // String csj
                Integer.parseInt(tokens[WORKING_DAYS_INDEX]), // int workingDays
                Integer.parseInt(tokens[UP_TO_MOBS_INDEX]), // int upTo_Mobs
                Float.parseFloat(tokens[TOTAL_MOBS_INDEX]), // float totalMobs
                Float.parseFloat(tokens[ADDITIONAL_MOBS_INDEX]), // float additionalMobs
                lineItems, // ArrayList<LineItem> lineItems
                contractors);
    }
}