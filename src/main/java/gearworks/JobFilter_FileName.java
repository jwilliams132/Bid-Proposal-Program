package gearworks;

import java.util.List;

public class JobFilter_FileName implements JobFilterInterface {

	public List<String> fileNames;

	public JobFilter_FileName(List<String> fileNames) {

		this.fileNames = fileNames;
	}

	@Override
	public boolean apply(Job job) {
		
		String fileName = String.format("%s-%s(%s).txt", job.getCounty().replace(", ETC", ""), job.getCsj(), job.getHighway().replace(", ETC", ""));
		System.out.println("File name:  " + fileName);
		System.out.println(fileNames.contains(fileName));
		return fileNames.contains(fileName);
	}
}
