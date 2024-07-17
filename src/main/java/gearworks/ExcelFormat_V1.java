package gearworks;

import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.ss.util.CellReference;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExcelFormat_V1 extends ExcelFormat {

    private final String templateFileName = "BidProposalProject\\src\\main\\resources\\Test Template.xlsm";

    private final CellReference CSJ = new CellReference("G4");
    private final CellReference HIGHWAY = new CellReference("A31");
    private final CellReference COUNTY = new CellReference("A34");
    private final CellReference ADDMOBS = new CellReference("D24");
    private final CellReference TOTALMOBS = new CellReference("G21");
    private final CellReference UPTOMOBS = new CellReference("E21");
    private final CellReference ESTIMATENO = new CellReference("A28");
    private final CellReference CONTNAME = new CellReference("A19");
    private final CellReference CONTPHONE = new CellReference("A23");
    private final CellReference CONTEMAIL = new CellReference("A25");
    private final CellReference SENTTO = new CellReference("E4");
    private final CellReference LIQUANTITY = new CellReference("D8");
    private final CellReference LIDESCRIPTION = new CellReference("E8");
    private final CellReference LIPRICE = new CellReference("F8");
    private final CellReference LITOTAL = new CellReference("G8");
    private final CellReference TOTALAMOUNT = new CellReference("G37");

    @Override
    public void createExcelFile(List<Job> jobs, String lettingMonthDirectory) {

        int contractorNumber = 0;
        // do something for estimate #
        for (Job job : jobs) {

            createWorkBook(templateFileName);
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

    // excelManager.saveWorkbook(String.format("%s\\%S %s%s", lettingMonthDirectory,
    // parseFullDoc.getJobList().get(jobIndex).getCounty(),
    // parseFullDoc.getJobList().get(jobIndex).getCsj(), ".xlsm"));
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

            setCellValue(sheetName, CSJ.getCol(), CSJ.getRow(), job.getCsj());
            setCellValue(sheetName, HIGHWAY.getCol(), HIGHWAY.getRow(), job.getHighway());
            setCellValue(sheetName, COUNTY.getCol(), COUNTY.getRow(), job.getCounty());
            setCellValue(sheetName, ADDMOBS.getCol(), ADDMOBS.getRow(), job.getAdditionalMobs());
            setCellValue(sheetName, TOTALMOBS.getCol(), TOTALMOBS.getRow(), job.getTotalMobs());
            setCellValue(sheetName, UPTOMOBS.getCol(), UPTOMOBS.getRow(), job.getUpTo_Mobs());
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
        }
    }
}
