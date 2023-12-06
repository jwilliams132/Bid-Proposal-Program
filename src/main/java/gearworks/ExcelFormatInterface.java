package gearworks;

import java.util.List;

public interface ExcelFormatInterface {

    public void createExcelFile(List<Job> jobs, String lettingMonthDirectory);

    public List<Job> getInfoFromExcelFile();

    public void populateExcel(Job job, String estimateNumber, int contractor);
}
