import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ExcelFormat implements ExcelFormatInterface {

    private Workbook workbook;
    private FormulaEvaluator evaluator;

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
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else {
            // Unsupported value type
            throw new IllegalArgumentException("Invalid value type: " + value.getClass().getName());
        }
    }
}
