package gearworks;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.util.CellReference;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExcelFormat_V2 extends ExcelFormat {

    private final String templateFileName = "src\\main\\resources\\gearworks\\Test Template (9-30-23).xlsm";

    private final CellReference CSJ = new CellReference("H3");
    private final CellReference HIGHWAY = new CellReference("A35");
    private final CellReference COUNTY = new CellReference("H5");
    private final CellReference PERMOBS = new CellReference("F24");
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
    private final CellReference GENERALCONDITION3 = new CellReference("B43"); // USES UPTOMOBS, ADDMOBS
    private final CellReference SPECIALCONDITION1 = new CellReference("B48"); // USES STANDBYDAYPRICE
    private final CellReference SPECIALCONDITION2 = new CellReference("B50"); // USES PRICEAPPLICABLEDATE
    private final CellReference MINIMUMDAYCHARGE = new CellReference("B56"); // USES PRICEAPPLICABLEDATE

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

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Success");
        alert.setHeaderText("Success");
        alert.setContentText("Excel files were created");
        alert.showAndWait();
    }

    @Override
    public List<Job> getInfoFromExcelFile() {

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
            setCellValue(sheetName, PERMOBS.getCol(), PERMOBS.getRow(), job.getTotalMobs());
            setCellValue(sheetName, TOTALMOBS.getCol(), TOTALMOBS.getRow(), job.getTotalMobs().multiply(BigDecimal.valueOf(job.getUpTo_Mobs())));
            setCellValue(sheetName, ESTIMATENO.getCol(), ESTIMATENO.getRow(),
                    String.format("%s%03d", estimateString, contractorNumber + contractorIndex));

            totalAmount = totalAmount.add(job.getTotalMobs().multiply(BigDecimal.valueOf(job.getUpTo_Mobs())));

            setCellValue(sheetName, CONTNAME.getCol(), CONTNAME.getRow(), contractor.getContractorName());
            setCellValue(sheetName, CONTEMAIL.getCol(), CONTEMAIL.getRow(), contractor.getContractorEmail());
            setCellValue(sheetName, SENTTO.getCol(), SENTTO.getRow(), contractor.getContractorEmail());
            setCellValue(sheetName, CONTPHONE.getCol(), CONTPHONE.getRow(), contractor.getContractorPhoneNumber());

            for (int lineItemIndex = 0; lineItemIndex < job.getLineItems().size(); lineItemIndex++) {

                lineItem = job.getLineItems().get(lineItemIndex);
                lineItemAmount = lineItem.getQuantity().multiply(lineItem.getPrice());
                totalAmount = totalAmount.add(lineItemAmount);

                setCellValue(sheetName, LIQUANTITY.getCol(), LIQUANTITY.getRow() + lineItemIndex,
                        lineItem.getQuantity());
                setCellValue(sheetName, LIDESCRIPTION.getCol(), LIDESCRIPTION.getRow() + lineItemIndex,
                        lineItem.getDescription());
                setCellValue(sheetName, LIPRICE.getCol(), LIPRICE.getRow() + lineItemIndex, lineItem.getPrice());
                setCellValue(sheetName, LITOTAL.getCol(), LITOTAL.getRow() + lineItemIndex,
                        String.format("$%,1.2f", lineItemAmount));
            }
            setCellValue(sheetName, TOTALAMOUNT.getCol(), TOTALAMOUNT.getRow(), String.format("$%,1.2f", totalAmount));

            BigDecimalToWordsConverter converter = new BigDecimalToWordsConverter();
            setCellValue(sheetName, GENERALCONDITION3.getCol(), GENERALCONDITION3.getRow(), String.format(
                    "%s (%d) Mobilization included in initial proposal. Additional Mobilizations shall be %s each.",
                    converter.convertIntToWords(job.getUpTo_Mobs()), job.getUpTo_Mobs(),
                    converter.convertCurrencyToWords(job.getAdditionalMobs())));

            setCellValue(sheetName, SPECIALCONDITION1.getCol(), SPECIALCONDITION1.getRow(),
                    String.format(
                            "Any stand by days not caused by Williams Road, LLC shall be assessed at $%,.2f per day.",
                            job.getStandbyPrice()));

            // ================================================================================
            // CODE TO GET PRICE EXPIRATION DATE
            // ================================================================================

            LocalDate originalLocalDate = job.getBiddingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate newLocalDate = originalLocalDate.plus(60, ChronoUnit.DAYS);
            Date priceExpirationDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            String priceExpirationDateString = dateFormat.format(priceExpirationDate);

            // ================================================================================

            setCellValue(sheetName, SPECIALCONDITION2.getCol(), SPECIALCONDITION2.getRow(),
                    String.format("Price is applicable through %s.", priceExpirationDateString)); // ex: "October 12,
                                                                                                  // 2023"

            setCellValue(sheetName, MINIMUMDAYCHARGE.getCol(), MINIMUMDAYCHARGE.getRow(),
                    String.format(
                            "Low production caused by lack of trucking or phasing/planning will result in a $%,.0f minimum day charge (Charge by yard or minimum day rate; whichever is greater). Cannot attain reasonable production if mill is consistently idle due to lack of trucks at mill.",
                            job.getMinimumDayCharge()));
        }
    }

}