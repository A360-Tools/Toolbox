package sumit.devtools.utils;

import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Utility class for Excel Pivot Table operations
 */
public class PivotTableUtil {


  /**
   * Detects the data range in a sheet
   *
   * @param sheet The sheet to analyze
   * @return AreaReference covering the data range
   * @throws BotCommandException If no data is found or if it's insufficient for a pivot table
   */
  public static AreaReference detectDataRange(XSSFSheet sheet) {
    // Find the data range in the sheet
    int firstRow = sheet.getFirstRowNum();
    int lastRow = sheet.getLastRowNum();

    if (firstRow > lastRow || lastRow < 0) {
      throw new BotCommandException("No data found in source sheet");
    }

    // Ensure we have at least two rows (header + data)
    if (lastRow - firstRow < 1) {
      throw new BotCommandException(
          "Sheet contains only a single row. Pivot tables require at least one header" +
              " row and one data row");
    }

    int firstCol = Integer.MAX_VALUE;
    int lastCol = 0;

    // Find leftmost and rightmost columns with data
    for (int i = firstRow; i <= lastRow; i++) {
      Row row = sheet.getRow(i);
      if (row != null) {
        if (row.getFirstCellNum() >= 0) {
          firstCol = Math.min(firstCol, row.getFirstCellNum());
          lastCol = Math.max(lastCol, row.getLastCellNum() - 1);
        }
      }
    }

    if (firstCol > lastCol) {
      throw new BotCommandException("No data found in source sheet");
    }

    // Create area reference from detected range
    CellReference firstCell = new CellReference(firstRow, firstCol);
    CellReference lastCell = new CellReference(lastRow, lastCol);

    AreaReference areaRef = new AreaReference(
        firstCell,
        lastCell,
        SpreadsheetVersion.EXCEL2007
    );

    // Validate the dimensions of the detected range
    validateRangeDimensions(areaRef);

    return areaRef;
  }

  /**
   * Validates that a range has sufficient dimensions for a pivot table
   *
   * @param areaRef The area reference to validate
   * @throws BotCommandException If the range is too small
   */
  public static void validateRangeDimensions(AreaReference areaRef) {
    int rowCount = areaRef.getLastCell().getRow() - areaRef.getFirstCell().getRow() + 1;
    int colCount = areaRef.getLastCell().getCol() - areaRef.getFirstCell().getCol() + 1;

    if (rowCount < 2) {
      throw new BotCommandException(
          "Source range must include at least 2 rows (header row and at least one " +
              "data row)");
    }

    if (colCount < 1) {
      throw new BotCommandException("Source range must include at least 1 column");
    }
  }

  /**
   * Validates that the data in the range is sufficient for pivot table creation. Ensures there is
   * at least one row of actual data (not just headers).
   *
   * @param sheet      The sheet containing the data
   * @param sourceArea The area defining the data range
   * @throws BotCommandException If there's not enough actual data
   */
  public static void validateDataRows(XSSFSheet sheet, AreaReference sourceArea) {
    int firstRow = sourceArea.getFirstCell().getRow();
    int lastRow = sourceArea.getLastCell().getRow();
    int firstCol = sourceArea.getFirstCell().getCol();
    int lastCol = sourceArea.getLastCell().getCol();

    // If we only have one row, there's no data (just headers)
    if (firstRow == lastRow) {
      throw new BotCommandException(
          "Source range contains only header row, but no data rows. Range must " +
              "include at least one header row and one data row.");
    }

    // Check that we have at least one data row with actual content
    boolean hasData = false;

    for (int rowIdx = firstRow + 1; rowIdx <= lastRow; rowIdx++) {
      Row row = sheet.getRow(rowIdx);
      if (row == null) {
        continue;
      }

      for (int colIdx = firstCol; colIdx <= lastCol; colIdx++) {
        Cell cell = row.getCell(colIdx);
        String value = getCellValueAsString(cell);

        if (value != null && !value.trim().isEmpty()) {
          hasData = true;
          break;
        }
      }

      if (hasData) {
        break;
      }
    }

    if (!hasData) {
      throw new BotCommandException("Source range doesn't contain any data rows with values. " +
          "Pivot tables require at least one data row with non-empty values");
    }
  }

  /**
   * Gets a string representation of a cell value
   *
   * @param cell The cell to get value from
   * @return String representation of cell value
   */
  public static String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return "";
    }

    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue().toString();
        } else {
          return String.valueOf(cell.getNumericCellValue());
        }
      case BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case FORMULA:
        return cell.getCellFormula();
      default:
        return "";
    }
  }

  /**
   * Validates the header row of the source data for empty cells and duplicate values within the
   * specified range.
   *
   * @param sheet      The source sheet
   * @param sourceArea The area reference defining the source data range
   * @throws BotCommandException If validation fails due to empty or duplicate headers
   */
  public static void validateHeaders(XSSFSheet sheet, AreaReference sourceArea) {
    // Get the header row - first row in the range
    int headerRowNum = sourceArea.getFirstCell().getRow();
    Row headerRow = sheet.getRow(headerRowNum);
    if (headerRow == null) {
      throw new BotCommandException("Header row not found in the specified range");
    }

    // Maps to track header values and their positions
    Map<String, Integer> headerMap = new HashMap<>();
    List<Integer> emptyHeaderPositions = new ArrayList<>();
    Map<String, List<Integer>> duplicateHeaders = new HashMap<>();

    // Only check cells within the range bounds
    int firstCol = sourceArea.getFirstCell().getCol();
    int lastCol = sourceArea.getLastCell().getCol();

    // Check each cell in the header row within the source range
    for (int i = firstCol; i <= lastCol; i++) {
      Cell cell = headerRow.getCell(i);
      String headerValue = getCellValueAsString(cell);

      // Check for empty header
      if (headerValue == null || headerValue.trim().isEmpty()) {
        emptyHeaderPositions.add(i);
        continue;
      }

      // Convert to lowercase for case-insensitive comparison
      String normalizedHeader = headerValue.toLowerCase();

      // Check for duplicate header
      if (headerMap.containsKey(normalizedHeader)) {
        // Add to duplicates map
        if (!duplicateHeaders.containsKey(normalizedHeader)) {
          duplicateHeaders.put(normalizedHeader, new ArrayList<>());
          duplicateHeaders.get(normalizedHeader).add(headerMap.get(normalizedHeader));
        }
        duplicateHeaders.get(normalizedHeader).add(i);
      } else {
        headerMap.put(normalizedHeader, i);
      }
    }

    // Build error message for empty headers
    if (!emptyHeaderPositions.isEmpty()) {
      StringBuilder errorMsg = new StringBuilder("Empty header cells found at positions: ");
      for (int i = 0; i < emptyHeaderPositions.size(); i++) {
        int colIndex = emptyHeaderPositions.get(i);
        String colLetter = CellReference.convertNumToColString(colIndex);
        errorMsg.append(colLetter).append(headerRowNum + 1);
        if (i < emptyHeaderPositions.size() - 1) {
          errorMsg.append(", ");
        }
      }
      throw new BotCommandException(errorMsg.toString());
    }

    // Build error message for duplicate headers
    if (!duplicateHeaders.isEmpty()) {
      StringBuilder errorMsg = new StringBuilder("Duplicate header values found: ");
      int count = 0;
      for (Map.Entry<String, List<Integer>> entry : duplicateHeaders.entrySet()) {
        errorMsg.append("'").append(entry.getKey()).append("' at positions ");
        List<Integer> positions = entry.getValue();
        for (int i = 0; i < positions.size(); i++) {
          int colIndex = positions.get(i);
          String colLetter = CellReference.convertNumToColString(colIndex);
          errorMsg.append(colLetter).append(headerRowNum + 1);
          if (i < positions.size() - 1) {
            errorMsg.append(", ");
          }
        }
        count++;
        if (count < duplicateHeaders.size()) {
          errorMsg.append("; ");
        }
      }
      throw new BotCommandException(errorMsg.toString());
    }
  }

}