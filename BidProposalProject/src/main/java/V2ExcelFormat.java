import java.util.List;

public class V2ExcelFormat extends ExcelFormat {

    private final String templateFileName = "";

    private final int CSJCOLUMN = 6;
    private final int CSJROW = 3;

    private final int HIGHWAYCOLUMN = 0;
    private final int HIGHWAYROW = 30;

    private final int COUNTYCOLUMN = 0;
    private final int COUNTYROW = 33;

    private final int ADDMOBSCOLUMN = 3;
    private final int ADDMOBSROW = 23;

    private final int TOTALMOBSCOLUMN = 6;
    private final int TOTALMOBSROW = 20;

    private final int UPTOMOBSCOLUMN = 4;
    private final int UPTOMOBSROW = 20;

    private final int ESTIMATENOCOLUMN = 27;
    private final int ESTIMATENOROW = 0;

    private final int CONTNAMECOLUMN = 0;
    private final int CONTNAMEROW = 18;

    private final int CONTPHONECOLUMN = 0;
    private final int CONTPHONEROW = 22;

    private final int CONTEMAILCOLUMN = 0;
    private final int CONTEMAILROW = 24;

    private final int SENTTOCOLUMN = 4;
    private final int SENTTOROW = 3;

    private final int LIQUANTITYCOLUMN = 3;
    private final int LIQUANTITYROW = 7;

    private final int LIDESCRIPTIONCOLUMN = 4;
    private final int LIDISCRIPTIONROW = 7;

    private final int LIPRICECOLUMN = 5;
    private final int LIPRICEROW = 7;

    private final int LITOTALCOLUMN = 6;
    private final int LITOTALROW = 7;

    private final int TOTALAMOUNTCOLUMN = 6;
    private final int TOTALAMOUNTROW = 36;

    @Override
    public void createExcelFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Job> getInfoFromExcelFile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInfoFromExcelFile'");
    }

    @Override
    public void populateExcel(Job job) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'populateExcel'");
    }
    
}
