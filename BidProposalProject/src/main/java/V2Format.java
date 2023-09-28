import java.io.File;
import java.util.ArrayList;

public class V2Format extends Format {

	final int COUNTY_INDEX = 0;
	final int HIGHWAY_INDEX = 1;
	final int CSJ_INDEX = 2;
	final int WORKING_DAYS_INDEX = 3;
	final int UP_TO_MOBS_INDEX = 4;
	final int TOTAL_MOBS_INDEX = 5;
	final int ADDITIONAL_MOBS_INDEX = 6;
	final int LINE_ITEM_COUNT_INDEX = 7;
	final int CONTRACTOR_COUNT_INDEX = 8;
	final int START_OF_LINE_ITEMS = 9;

	final int LENGTH_OF_LINE_ITEM = 3;
	final int LINE_ITEM_DESCRIPTION_OFFSET = 0;
	final int LINE_ITEM_QUANTITY_OFFSET = 1;
	final int LINE_ITEM_PRICE_OFFSET = 2;

	final int LENGTH_OF_CONTRACTORS = 4;
	final int CONTRACTOR_NAME_OFFSET = 0;
	final int CONTRACTOR_PHONE_OFFSET = 1;
	final int CONTRACTOR_EMAIL_OFFSET = 2;
	final int ESTIMATE_NO_OF_JOB_OFFSET = 3;

	public V2Format() {

	}

	public V2Format(Test test) {

		FileManager fileManager = new FileManager();
		File file = fileManager.chooseFile(null, null, FileManager.fileChooserOptions.OPEN, null);
		ArrayList<String> fileContents = fileManager.readFile(file);
		ArrayList<Job> jobs = jobsFromFormat(fileContents);
		jobs.forEach(job -> job.printJobInfo());
	}

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

		ArrayList<LineItem> lineItems = job.getLineItems();
		ArrayList<Contractor> contractors = job.getContractorList();

		final int LINE_ITEM_COUNT = lineItems.size();
		final int CONTRACTOR_COUNT = contractors.size();
		final int START_OF_CONTRACTORS = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM;

		result.add(LINE_ITEM_COUNT_INDEX, String.valueOf(LINE_ITEM_COUNT));
		result.add(CONTRACTOR_COUNT_INDEX, String.valueOf(CONTRACTOR_COUNT));

		for (int lineItem = 0; lineItem < LINE_ITEM_COUNT; lineItem++) {

			result.add(START_OF_LINE_ITEMS + LINE_ITEM_DESCRIPTION_OFFSET + lineItem * LINE_ITEM_COUNT,
					lineItems.get(lineItem).getDescription());
			result.add(START_OF_LINE_ITEMS + LINE_ITEM_QUANTITY_OFFSET + lineItem * LINE_ITEM_COUNT,
					String.valueOf(lineItems.get(lineItem).getQuantity()));
			result.add(START_OF_LINE_ITEMS + LINE_ITEM_PRICE_OFFSET + lineItem * LINE_ITEM_COUNT,
					String.valueOf(lineItems.get(lineItem).getPrice()));
		}

		for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

			result.add(START_OF_CONTRACTORS + CONTRACTOR_NAME_OFFSET + contractor * CONTRACTOR_COUNT,
					contractors.get(contractor).getContractorName());
			result.add(START_OF_CONTRACTORS + CONTRACTOR_PHONE_OFFSET + contractor * CONTRACTOR_COUNT,
					contractors.get(contractor).getContractorPhoneNumber());
			result.add(START_OF_CONTRACTORS + CONTRACTOR_EMAIL_OFFSET + contractor * CONTRACTOR_COUNT,
					contractors.get(contractor).getContractorEmail());
			result.add(START_OF_CONTRACTORS + ESTIMATE_NO_OF_JOB_OFFSET + contractor * CONTRACTOR_COUNT,
					contractors.get(contractor).getContractorEstimateNo());
		}

		StringBuilder resultString = new StringBuilder();
		for (String infoLine : result) {

			resultString.append(infoLine).append('|');
		}
		return resultString.toString();
	}

	@Override
	public Job jobFromFormat(String jobLineString) {

		String[] tokens = jobLineString.split("\\|");

		final int LINE_ITEM_COUNT = Integer.parseInt(tokens[LINE_ITEM_COUNT_INDEX]);
		final int CONTRACTOR_COUNT = Integer.parseInt(tokens[CONTRACTOR_COUNT_INDEX]);
		final int START_OF_CONTRACTORS = START_OF_LINE_ITEMS + LINE_ITEM_COUNT * LENGTH_OF_LINE_ITEM;

		ArrayList<LineItem> lineItems = new ArrayList<>();
		for (int lineItem = 0; lineItem < LINE_ITEM_COUNT; lineItem++) {

			lineItems.add(new LineItem(
					tokens[START_OF_LINE_ITEMS + LINE_ITEM_DESCRIPTION_OFFSET
							+ lineItem * LINE_ITEM_COUNT],
					Float.parseFloat(
							tokens[START_OF_LINE_ITEMS + LINE_ITEM_QUANTITY_OFFSET
									+ lineItem * LINE_ITEM_COUNT]),
					Float.parseFloat(
							tokens[START_OF_LINE_ITEMS + LINE_ITEM_PRICE_OFFSET
									+ lineItem * LINE_ITEM_COUNT])));
		}

		ArrayList<Contractor> contractors = new ArrayList<>();
		for (int contractor = 0; contractor < CONTRACTOR_COUNT; contractor++) {

			contractors.add(new Contractor(
					tokens[START_OF_CONTRACTORS + CONTRACTOR_NAME_OFFSET
							+ contractor * CONTRACTOR_COUNT],
					tokens[START_OF_CONTRACTORS + CONTRACTOR_PHONE_OFFSET
							+ contractor * CONTRACTOR_COUNT],
					tokens[START_OF_CONTRACTORS + CONTRACTOR_EMAIL_OFFSET
							+ contractor * CONTRACTOR_COUNT],
					tokens[START_OF_CONTRACTORS + ESTIMATE_NO_OF_JOB_OFFSET
							+ contractor * CONTRACTOR_COUNT]));
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
