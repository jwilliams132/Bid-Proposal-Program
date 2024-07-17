package gearworks;

import java.util.ArrayList;
import java.util.List;

public class FileFormat_ClearText extends FileFormat {

	public FileFormat_ClearText() {

	}

	@Override
	public List<String> jobsToFormat(List<Job> jobs) {

		List<String> userFriendlyOutputBuffer = new ArrayList<String>();

		// add all job data to fileContentBuffer
		for (Job job : jobs) {

			userFriendlyOutputBuffer.addAll(job.formatUserFriendlyJobInfo());
			userFriendlyOutputBuffer.add("-".repeat(100));
		}
		return userFriendlyOutputBuffer;
	}
}
