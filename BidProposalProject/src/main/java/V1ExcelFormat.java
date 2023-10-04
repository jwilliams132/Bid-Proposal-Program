import java.util.List;

public class V1ExcelFormat extends ExcelFormat {

    private final String templateFileName = "BidProposalProject\\src\\main\\resources\\Test Template.xlsm";
    private int startingEstimateNo = 1234;
    private String estimateNumber = "";

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

        createWorkBook(templateFileName);
        populateExcel(null);

    }

    @Override
    public List<Job> getInfoFromExcelFile() {

        throw new UnsupportedOperationException("Unimplemented method 'getInfoFromExcelFile'");
    }

    @Override
    public void populateExcel(Job job) {

        String sheetName;
        Contractor contractor;
        LineItem lineItem;
        float lineItemAmount;
        float totalAmount = 0;

        for (int contractorIndex = 0; contractorIndex < job.getContractorList().size(); contractorIndex++) {

            totalAmount = 0;

            sheetName = String.valueOf(contractorIndex + 1);
            contractor = job.getContractorList().get(contractorIndex);

            setCellValue(sheetName, CSJCOLUMN, CSJROW, job.getCsj());
            setCellValue(sheetName, HIGHWAYCOLUMN, HIGHWAYROW, job.getHighway());
            setCellValue(sheetName, COUNTYCOLUMN, COUNTYROW, job.getCounty());
            setCellValue(sheetName, ADDMOBSCOLUMN, ADDMOBSROW, job.getAdditionalMobs());
            setCellValue(sheetName, TOTALMOBSCOLUMN, TOTALMOBSROW, job.getTotalMobs());
            setCellValue(sheetName, UPTOMOBSCOLUMN, UPTOMOBSROW, job.getUpTo_Mobs());
            setCellValue(sheetName, ESTIMATENOCOLUMN, ESTIMATENOROW,
                    String.format("%s%d", "WR-2023-", estimateNumber + contractorIndex));

            totalAmount += job.getTotalMobs();
            setCellValue(sheetName, CONTNAMECOLUMN, CONTNAMEROW, contractor.getContractorName());
            setCellValue(sheetName, CONTEMAILCOLUMN, CONTEMAILROW, contractor.getContractorEmail());
            setCellValue(sheetName, SENTTOCOLUMN, SENTTOROW, contractor.getContractorEmail());
            setCellValue(sheetName, CONTPHONECOLUMN, CONTPHONEROW, contractor.getContractorPhoneNumber());

            for (int lineItemIndex = 0; lineItemIndex < job.getLineItems().size(); lineItemIndex++) {

                lineItem = job.getLineItems().get(lineItemIndex);
                lineItemAmount = lineItem.getQuantity() * lineItem.getPrice();
                totalAmount += lineItemAmount;

                setCellValue(sheetName, LIQUANTITYCOLUMN, LIQUANTITYROW + lineItemIndex, lineItem.getQuantity());
                setCellValue(sheetName, LIDESCRIPTIONCOLUMN, LIDISCRIPTIONROW + lineItemIndex,
                        lineItem.getDescription());
                setCellValue(sheetName, LIPRICECOLUMN, LIPRICEROW + lineItemIndex, lineItem.getPrice());
                setCellValue(sheetName, LITOTALCOLUMN, LITOTALROW + lineItemIndex,
                        String.format("$%,1.2f", lineItemAmount));
            }
            setCellValue(sheetName, TOTALAMOUNTCOLUMN, TOTALAMOUNTROW, String.format("$%,1.2f", totalAmount));
        }
    }

}
