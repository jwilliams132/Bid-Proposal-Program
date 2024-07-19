package gearworks;

import java.util.List;

public class JobFilter_FileName implements JobFilterInterface {

	private List<String> fileNames;

	public JobFilter_FileName(List<String> fileNames) {

		this.fileNames = fileNames;
	}

	@Override
	public boolean apply(Job job) {
		
		String fileName = String.format("%s-%s\\(%s\\).txt", job.getCounty(), job.getCsj(), job.getHighway());
		return fileNames.contains(fileName);
	}
}
