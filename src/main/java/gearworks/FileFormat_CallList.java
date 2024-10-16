package gearworks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FileFormat_CallList extends FileFormat {

	public FileFormat_CallList() {

	}

	@Override
	public List<String> jobsToFormat(List<Job> jobs) {

		List<String> callListBuffer = new ArrayList<String>();
		Map<String, List<String>> map = new HashMap<>();

		// add all job data to fileContentBuffer
		StringBuilder sb;
		callListBuffer.add("-=".repeat(25));
		for (Job job : jobs) {

			sb = new StringBuilder();
			sb.append(job.getCounty()).append(" ").append(job.getCsj());
			callListBuffer.add("");
			callListBuffer.add(sb.toString());
			callListBuffer.add("-".repeat(25));
			callListBuffer.add("");
			sb = new StringBuilder();
			job.getContractorList().forEach(contractor -> {
				callListBuffer.add(contractor.getContractorName());
				callListBuffer.add(contractor.getContractorPhoneNumber());
				callListBuffer.add(contractor.getContractorEmail());
				callListBuffer.add("");
			});
			callListBuffer.add("");
			callListBuffer.add("-=".repeat(25));
		}
		callListBuffer.add("=".repeat(100));
		callListBuffer.add("");



		for (Job job : jobs) {

			for (Contractor contractor : job.getContractorList()) {

				if (!map.containsKey(contractor.getContractorName())) {

					map.put(contractor.getContractorName(), new ArrayList<String>());
				}
				map.get(contractor.getContractorName()).add(job.getCounty() + " " + job.getCsj());

			}
		}
		for (Map.Entry<String,List<String>> entry : map.entrySet()) {
			
			callListBuffer.add("");
			callListBuffer.add(entry.getKey());
			callListBuffer.add("");
			for (String string : entry.getValue()) {
				callListBuffer.add(string);
			}
			callListBuffer.add("");
			callListBuffer.add("-=".repeat(25));
		}
		return callListBuffer;
	}
}
