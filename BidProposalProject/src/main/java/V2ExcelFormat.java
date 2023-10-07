import java.math.BigDecimal;
import java.util.List;

public class V2ExcelFormat extends ExcelFormat {

    private final String templateFileName = "BidProposalProject\\src\\main\\resources\\Test Template (9-30-23).xlsm";

    private final CellCoordinates CSJ = new CellCoordinates(7, 2);
    private final CellCoordinates HIGHWAY = new CellCoordinates(0, 34);
    private final CellCoordinates COUNTY = new CellCoordinates(7, 4);
    private final CellCoordinates ADDMOBS = new CellCoordinates(1, 43);
    private final CellCoordinates ADDMOBS2 = new CellCoordinates(1, 44);
    private final CellCoordinates TOTALMOBS = new CellCoordinates(6, 20);
    private final CellCoordinates UPTOMOBS = new CellCoordinates(7, 23);
    private final CellCoordinates ESTIMATENO = new CellCoordinates(0, 22);
    private final CellCoordinates CONTNAME = new CellCoordinates(0, 22);
    private final CellCoordinates CONTPHONE = new CellCoordinates(0, 27);
    private final CellCoordinates CONTEMAIL = new CellCoordinates(0, 29);
    private final CellCoordinates SENTTO = new CellCoordinates(5, 2);
    private final CellCoordinates LIDESCRIPTION = new CellCoordinates(4, 8);
    private final CellCoordinates LIQUANTITY = new CellCoordinates(5, 8);
    private final CellCoordinates LIPRICE = new CellCoordinates(6, 8);
    private final CellCoordinates LITOTAL = new CellCoordinates(7, 8);
    private final CellCoordinates TOTALAMOUNT = new CellCoordinates(7, 27);
    private final CellCoordinates DAYSOFPRODUCTION = new CellCoordinates(1, 42);
    private final CellCoordinates STANDBYDAYPRICE = new CellCoordinates(1, 49);
    private final CellCoordinates PRICEAPPLICABLEDATE = new CellCoordinates(1, 51);

    @Override
    public void createExcelFile(List<Job> jobs) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Job> getInfoFromExcelFile() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInfoFromExcelFile'");
    }

    @Override
    public void populateExcel(Job job, String estimateNumber) {

        String sheetName;
        Contractor contractor;
        LineItem lineItem;
        BigDecimal lineItemAmount;
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
                    String.format("%s%d", "WR-2023-", estimateNumber + contractorIndex));

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
