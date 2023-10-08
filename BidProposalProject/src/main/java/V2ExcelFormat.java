import java.math.BigDecimal;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.util.CellReference;

public class V2ExcelFormat extends ExcelFormat {

    private final String templateFileName = "BidProposalProject\\src\\main\\resources\\Test Template (9-30-23).xlsm";

    private final CellReference CSJ = new CellReference("H3");
    private final CellReference HIGHWAY = new CellReference("A35");
    private final CellReference COUNTY = new CellReference("H5");
    private final CellReference TOTALMOBS = new CellReference("H24");
    private final CellReference ESTIMATENO = new CellReference("A33");
    private final CellReference CONTNAME = new CellReference("A23");
    private final CellReference CONTPHONE = new CellReference("A28");
    private final CellReference CONTEMAIL = new CellReference("A30");
    private final CellReference SENTTO = new CellReference("F3");
    private final CellReference LIDESCRIPTION = new CellReference("E9");
    private final CellReference LIQUANTITY = new CellReference("F9");
    private final CellReference LIPRICE = new CellReference("G9");
    private final CellReference LITOTAL = new CellReference("H9");
    private final CellReference TOTALAMOUNT = new CellReference("H28");
    private final CellReference GENERALCONDITION3 = new CellReference("B43"); // USES DAYSOFPRODUCTION
    private final CellReference GENERALCONDITION4 = new CellReference("B44"); // USES UPTOMOBS, ADDMOBS
    private final CellReference SPECIALCONDITION1 = new CellReference("B49"); // USES STANDBYDAYPRICE
    private final CellReference SPECIALCONDITION2 = new CellReference("B51"); // USES PRICEAPPLICABLEDATE
    private final CellReference MINIMUMDAYCHARGE = new CellReference("B57"); // USES PRICEAPPLICABLEDATE

    @Override
    public void createExcelFile(List<Job> jobs, String lettingMonthDirectory) {

        int contractorNumber = 0;
        // do something for estimate #
        for (Job job : jobs) {

            createWorkBook(templateFileName);
            for (int i = 0; i < job.getContractorList().size(); i++) {

                createNewSheet(String.valueOf(i + 2));
            }
            populateExcel(job, getEstimateNo(lettingMonthDirectory), contractorNumber);
            saveWorkbook(String.format("%s\\%S %s%s", lettingMonthDirectory,
                    job.getCounty(),
                    job.getCsj(), ".xlsm"));
            contractorNumber += job.getContractorList().size();
        }

        JOptionPane.showMessageDialog(null, "Success" + ": " + "Success", "Excel files were created",
                JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public List<Job> getInfoFromExcelFile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInfoFromExcelFile'");
    }

    @Override
    public void populateExcel(Job job, String estimateString, int contractorNumber) {

        String sheetName;
        Contractor contractor;
        LineItem lineItem;
        BigDecimal lineItemAmount;
        BigDecimal totalAmount = new BigDecimal(0);

        for (int contractorIndex = 0; contractorIndex < job.getContractorList().size(); contractorIndex++) {

            totalAmount = new BigDecimal(0);

            sheetName = String.valueOf(contractorIndex + 1);
            contractor = job.getContractorList().get(contractorIndex);

            setCellValue(sheetName, CSJ.getCol(), CSJ.getRow(), job.getCsj());
            setCellValue(sheetName, HIGHWAY.getCol(), HIGHWAY.getRow(), job.getHighway());
            setCellValue(sheetName, COUNTY.getCol(), COUNTY.getRow(), job.getCounty());
            setCellValue(sheetName, TOTALMOBS.getCol(), TOTALMOBS.getRow(), job.getTotalMobs());
            setCellValue(sheetName, ESTIMATENO.getCol(), ESTIMATENO.getRow(),
                    String.format("%s%03d", estimateString, contractorNumber + contractorIndex));

            totalAmount.add(job.getTotalMobs());
            setCellValue(sheetName, CONTNAME.getCol(), CONTNAME.getRow(), contractor.getContractorName());
            setCellValue(sheetName, CONTEMAIL.getCol(), CONTEMAIL.getRow(), contractor.getContractorEmail());
            setCellValue(sheetName, SENTTO.getCol(), SENTTO.getRow(), contractor.getContractorEmail());
            setCellValue(sheetName, CONTPHONE.getCol(), CONTPHONE.getRow(), contractor.getContractorPhoneNumber());

            for (int lineItemIndex = 0; lineItemIndex < job.getLineItems().size(); lineItemIndex++) {

                lineItem = job.getLineItems().get(lineItemIndex);
                lineItemAmount = lineItem.getQuantity().multiply(lineItem.getPrice());
                totalAmount.add(lineItemAmount);

                setCellValue(sheetName, LIQUANTITY.getCol(), LIQUANTITY.getRow() + lineItemIndex,
                        lineItem.getQuantity());
                setCellValue(sheetName, LIDESCRIPTION.getCol(), LIDESCRIPTION.getRow() + lineItemIndex,
                        lineItem.getDescription());
                setCellValue(sheetName, LIPRICE.getCol(), LIPRICE.getRow() + lineItemIndex, lineItem.getPrice());
                setCellValue(sheetName, LITOTAL.getCol(), LITOTAL.getRow() + lineItemIndex,
                        String.format("$%,1.2f", lineItemAmount));
            }
            setCellValue(sheetName, TOTALAMOUNT.getCol(), TOTALAMOUNT.getRow(), String.format("$%,1.2f", totalAmount));

            setCellValue(sheetName, GENERALCONDITION3.getCol(), GENERALCONDITION3.getRow(),
                    String.format(
                            "Williams Road, LLC's proposal is based on no more than %d days of production (milling).",
                            job.getProductionDays()));

            BigDecimalToWordsConverter converter = new BigDecimalToWordsConverter();
            setCellValue(sheetName, GENERALCONDITION4.getCol(), GENERALCONDITION4.getRow(), String.format(
                    "One (1) Mobilization included in initial proposal. Additional Mobilizations shall be %s each.",
                    converter.convertToWords(job.getAdditionalMobs())));

            setCellValue(sheetName, SPECIALCONDITION1.getCol(), SPECIALCONDITION1.getRow(),
                    String.format(
                            "Any stand by days not caused by Williams Road, LLC shall be assessed at $%,.2f per day.",
                            job.getStandbyPrice()));

            setCellValue(sheetName, SPECIALCONDITION2.getCol(), SPECIALCONDITION2.getRow(),
                    String.format("Price is applicable through %s.", "October 12, 2023")); // ex: "October 12, 2023"
                    
            setCellValue(sheetName, MINIMUMDAYCHARGE.getCol(), MINIMUMDAYCHARGE.getRow(),
                    String.format(
                            "Low production caused by lack of trucking or phasing/planning will result in a $%,.0f minimum day charge (Charge by yard or minimum day rate; whichever is greater). Cannot attain reasonable production if mill is consistently idle due to lack of trucks at mill.",
                            job.getMinimumDayCharge()));
        }
    }

}
