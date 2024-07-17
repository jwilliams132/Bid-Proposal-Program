package gearworks;

import java.util.ArrayList;
import java.util.List;

public class FileFormat_Email extends FileFormat {

	public FileFormat_Email() {

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
