import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

public class ExcelManager {

    private Workbook workbook;
    private String sheetName;
    private FormulaEvaluator evaluator;

    public ExcelManager() {

    }

    public void createWorkBook(String filePath) {
        try {

            // Open the Excel file
            FileInputStream file = new FileInputStream(filePath);

            // Get the workbook instance
            workbook = new XSSFWorkbook(file);

            evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // Close the file
            file.close();
        } catch (FileNotFoundException fNFE) {

            fNFE.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void setCellValue(String sheetName, int colNum, int rowNum, Object value) {
        Sheet sheet = workbook.getSheet(sheetName);

        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        // Set the cell value based on the type of the input value
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue()); // Convert BigDecimal to double
        } else {
            // Unsupported value type
            throw new IllegalArgumentException("Invalid value type: " + value.getClass().getName());
        }
    }

    public void setCellValue(Object value, int colNum, int rowNum) {
        Sheet sheet = workbook.getSheet(sheetName);

        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        // Set the cell value based on the type of the input value
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else {
            // Unsupported value type
            throw new IllegalArgumentException("Invalid value type: " + value.getClass().getName());
        }

        evaluator.evaluateInCell(cell);
    }

    public void evaluateCell(String sheetName, int row, int col) {
        evaluator.evaluateInCell(workbook.getSheet(sheetName).getRow(row).getCell(col));
    }

    public void evaluate() {
        evaluator.evaluateAll();
    }

    public void saveWorkbook(String filePath) {
        try {

            // Write the workbook to a file
            FileOutputStream file = new FileOutputStream(filePath);
            workbook.write(file);

            // Close the file
            file.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
