package gearworks;

import java.util.ArrayList;
import java.util.List;

public class EmailFormat extends Format {

	public EmailFormat() {

	}

	@Override
	public List<String> jobsToFormat(List<Job> jobs) {

		List<String> emailListBuffer = new ArrayList<String>();

		// add all job data to fileContentBuffer
		for (Job job : jobs) {

			emailListBuffer.addAll(job.formatEmailList());
		}
		return emailListBuffer;
	}
}
