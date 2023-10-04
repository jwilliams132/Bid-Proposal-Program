import java.util.List;

public interface ExcelFormatInterface {

    public void createExcelFile();

    public List<Job> getInfoFromExcelFile();

    public void populateExcel(Job job);
}
