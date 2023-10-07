import java.math.BigDecimal;
import java.util.List;

public class V1ExcelFormat extends ExcelFormat {

    private final String templateFileName = "BidProposalProject\\src\\main\\resources\\Test Template.xlsm";
    private int startingEstimateNo = 1234;

    private String estimateNumber = "";

    private final CellCoordinates CSJ = new CellCoordinates(6, 3);
    private final CellCoordinates HIGHWAY = new CellCoordinates(0, 30);
    private final CellCoordinates COUNTY = new CellCoordinates(0, 33);
    private final CellCoordinates ADDMOBS = new CellCoordinates(3, 23);
    private final CellCoordinates TOTALMOBS = new CellCoordinates(6, 20);
    private final CellCoordinates UPTOMOBS = new CellCoordinates(4, 20);
    private final CellCoordinates ESTIMATENO = new CellCoordinates(27, 0);
    private final CellCoordinates CONTNAME = new CellCoordinates(0, 18);
    private final CellCoordinates CONTPHONE = new CellCoordinates(0, 22);
    private final CellCoordinates CONTEMAIL = new CellCoordinates(0, 24);
    private final CellCoordinates SENTTO = new CellCoordinates(4, 3);
    private final CellCoordinates LIQUANTITY = new CellCoordinates(3, 7);
    private final CellCoordinates LIDESCRIPTION = new CellCoordinates(4, 7);
    private final CellCoordinates LIPRICE = new CellCoordinates(5, 7);
    private final CellCoordinates LITOTAL = new CellCoordinates(6, 7);
    private final CellCoordinates TOTALAMOUNT = new CellCoordinates(6, 36);

    @Override
    public void createExcelFile(List<Job> jobs, String lettingMonthDirectory) {

        int contractorNumber = 0;
        // do something for estimate #
        for (Job job : jobs) {

            contractorNumber++;
            createWorkBook(templateFileName);
            populateExcel(job, getEstimateNo(lettingMonthDirectory), contractorNumber);
            saveWorkbook(String.format("%s\\%S %s%s", lettingMonthDirectory,
            job.getCounty(),
            job.getCsj(), ".xlsm"));
        }

    }

    // excelManager.saveWorkbook(String.format("%s\\%S %s%s", lettingMonthDirectory,
    //                 parseFullDoc.getJobList().get(jobIndex).getCounty(),
    //                 parseFullDoc.getJobList().get(jobIndex).getCsj(), ".xlsm"));
    @Override
    public List<Job> getInfoFromExcelFile() {

        throw new UnsupportedOperationException("Unimplemented method 'getInfoFromExcelFile'");
    }

    @Override
    public void populateExcel(Job job, String estimateString, int contractorNumber) {

        String sheetName;
        Contractor contractor;
        LineItem lineItem;
        BigDecimal lineItemAmount = new BigDecimal(0);
        BigDecimal totalAmount = new BigDecimal(0);

        for (int contractorIndex = 0; contractorIndex < job.getContractorList().size(); contractorIndex++) {

            totalAmount = new BigDecimal(0);

            sheetName = String.valueOf(contractorIndex + 1);
            contractor = job.getContractorList().get(contractorIndex);

            setCellValue(sheetName, CSJ.column, CSJ.row, job.getCsj());
            setCellValue(sheetName, HIGHWAY.column, HIGHWAY.row, job.getHighway());
            setCellValue(sheetName, COUNTY.column, COUNTY.row, job.getCounty());
            setCellValue(sheetName, ADDMOBS.column, ADDMOBS.row, job.getAdditionalMobs());
            setCellValue(sheetName, TOTALMOBS.column, TOTALMOBS.row, job.getTotalMobs());
            setCellValue(sheetName, UPTOMOBS.column, UPTOMOBS.row, job.getUpTo_Mobs());
            setCellValue(sheetName, ESTIMATENO.column, ESTIMATENO.row,
                    String.format("%s%d", estimateString, contractorNumber));

            totalAmount += job.getTotalMobs();
            setCellValue(sheetName, CONTNAME.column, CONTNAME.row, contractor.getContractorName());
            setCellValue(sheetName, CONTEMAIL.column, CONTEMAIL.row, contractor.getContractorEmail());
            setCellValue(sheetName, SENTTO.column, SENTTO.row, contractor.getContractorEmail());
            setCellValue(sheetName, CONTPHONE.column, CONTPHONE.row, contractor.getContractorPhoneNumber());

            for (int lineItemIndex = 0; lineItemIndex < job.getLineItems().size(); lineItemIndex++) {

                lineItem = job.getLineItems().get(lineItemIndex);
                lineItemAmount = lineItem.getQuantity().multiply(lineItem.getPrice());
                totalAmount.add(lineItemAmount);

                setCellValue(sheetName, LIQUANTITY.column, LIQUANTITY.row + lineItemIndex, lineItem.getQuantity());
                setCellValue(sheetName, LIDESCRIPTION.column, LIDESCRIPTION.row + lineItemIndex,
                        lineItem.getDescription());
                setCellValue(sheetName, LIPRICE.column, LIPRICE.row + lineItemIndex, lineItem.getPrice());
                setCellValue(sheetName, LITOTAL.column, LITOTAL.row + lineItemIndex,
                        String.format("$%,1.2f", lineItemAmount));
            }
            setCellValue(sheetName, TOTALAMOUNT.column, TOTALAMOUNT.row, String.format("$%,1.2f", totalAmount));
        }
    }
}
