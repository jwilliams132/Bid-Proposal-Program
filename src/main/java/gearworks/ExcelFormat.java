package gearworks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.Month;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public abstract class ExcelFormat implements ExcelFormatInterface {

    private Workbook workbook;

    public void createWorkBook(String filePath) {

        try {

            // Open the Excel file
            FileInputStream file = new FileInputStream(filePath);

            // Get the workbook instance
            workbook = new XSSFWorkbook(file);

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

    public void saveWorkbook(String filePath) {
        try {

            // Write the workbook to a file
            FileOutputStream file = new FileOutputStream(filePath);
            workbook.write(file);

            // Close the file
            file.close();
        } catch (FileNotFoundException e) {

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Exception");
            alert.setHeaderText("File Creation");
            alert.setContentText(String.format(
                    "This file interferes with excel file creation: %n%n%s%n%nClose all excel files with this name.",
                    Paths.get(filePath).getFileName().toString()));
            alert.showAndWait();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String getEstimateNo(String lettingMonthDirectory) {

        String[] path = lettingMonthDirectory.split("\\\\");
        int lettingIndex = 0;

        for (int parent = 0; parent < path.length; parent++) {

            if (path[parent].equals("Letting")) {
                lettingIndex = parent;
            }
        }
        String year = null;
        Month month = null;
        try {
            year = path[lettingIndex + 1];
            month = Month.valueOf(path[lettingIndex + 2].toUpperCase());
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    "Letting file might not have year and month directories set up properly.");
        }
        int monthNumber = month.getValue();

        return String.format("WR-%s-%02d-", year, monthNumber);
    }

    public void createNewSheet(String sheetName) {

        if (workbook instanceof XSSFWorkbook) {

            // XSSFSheet firstSheet = (XSSFSheet) workbook.getSheetAt(0); // Assuming you
            // want the first sheet

            // Create a new sheet by cloning the first sheet
            XSSFSheet newSheet = ((XSSFWorkbook) workbook).cloneSheet(0);

            // Set the name of the new sheet
            workbook.setSheetName(workbook.getSheetIndex(newSheet), sheetName);
        } else {

            throw new UnsupportedOperationException("This method only supports XSSFWorkbook.");
        }
    }
}
