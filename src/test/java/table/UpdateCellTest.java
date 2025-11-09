package table;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.table.UpdateCell;

/**
 * @author Test Author
 */
public class UpdateCellTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {

    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Name"));
    schema.add(new Schema("Age"));
    schema.add(new Schema("City"));
    schema.add(new Schema("Country"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("John"));
    row1Values.add(new StringValue("30"));
    row1Values.add(new StringValue("New York"));
    row1Values.add(new StringValue("USA"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("Alice"));
    row2Values.add(new StringValue("25"));
    row2Values.add(new StringValue("Boston"));
    row2Values.add(new StringValue("USA"));
    rows.add(new Row(row2Values));

    // Row 3
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue("Bob"));
    row3Values.add(new StringValue("35"));
    row3Values.add(new StringValue("Chicago"));
    row3Values.add(new StringValue("USA"));
    rows.add(new Row(row3Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testUpdateCellByName() {
    // Update a cell by column name
    TableValue result = UpdateCell.action(
        inputTable,
        "NAME",           // Select by name
        "Age",            // Column name
        false,            // Case-insensitive
        null,             // Column index not used
        0.0,              // Row index (first row)
        new StringValue("31")  // New value
    );

    Table resultTable = result.get();

    // Verify the cell is updated
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "31");

    // Verify other cells are unchanged
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "John");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(2).toString(), "New York");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(1).toString(), "25");
  }

  @Test
  public void testUpdateCellByIndex() {
    // Update a cell by column index
    TableValue result = UpdateCell.action(
        inputTable,
        "INDEX",          // Select by index
        null,             // Column name not used
        null,             // Case-insensitive (not applicable for index)
        2.0,              // Column index (City)
        1.0,              // Row index (second row)
        new StringValue("San Francisco") // New value
    );

    Table resultTable = result.get();

    // Verify the cell is updated
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(2).toString(),
        "San Francisco");

    // Verify other cells are unchanged
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "Alice");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(1).toString(), "25");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(2).toString(), "New York");
  }

  @Test
  public void testUpdateCellCaseInsensitiveColumnName() {
    // Test case-insensitive column name matching
    TableValue result = UpdateCell.action(
        inputTable,
        "NAME",
        "name",  // lowercase, while schema has "Name"
        false,   // Case-insensitive
        null,
        0.0,
        new StringValue("Jane")
    );

    Table resultTable = result.get();
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "Jane");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellCaseSensitiveColumnName() {
    // Test case-sensitive column name matching (should fail)
    UpdateCell.action(
        inputTable,
        "NAME",
        "name",  // lowercase, while schema has "Name"
        true,    // Case-sensitive (should fail to find "name")
        null,
        0.0,
        new StringValue("Jane")
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellWithInvalidColumnName() {
    // Try to update a cell with a non-existent column name
    UpdateCell.action(
        inputTable,
        "NAME",
        "NonExistentColumn", // Invalid column name
        false,
        null,
        0.0,
        new StringValue("Test Value")
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellWithInvalidColumnIndex() {
    // Try to update a cell with an out-of-bounds column index
    UpdateCell.action(
        inputTable,
        "INDEX",
        null,
        null,
        10.0,  // Out of bounds index
        0.0,
        new StringValue("Test Value")
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellWithInvalidRowIndex() {
    // Try to update a cell with an out-of-bounds row index
    UpdateCell.action(
        inputTable,
        "NAME",
        "Name",
        false,
        null,
        10.0,  // Out of bounds index
        new StringValue("Test Value")
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellWithNegativeRowIndex() {
    // Try to update a cell with a negative row index
    UpdateCell.action(
        inputTable,
        "NAME",
        "Name",
        false,
        null,
        -1.0,  // Negative index
        new StringValue("Test Value")
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellWithNegativeColumnIndex() {
    // Try to update a cell with a negative column index
    UpdateCell.action(
        inputTable,
        "INDEX",
        null,
        null,
        -1.0,  // Negative index
        0.0,
        new StringValue("Test Value")
    );
  }

  @Test
  public void testUpdateCellPreservesOriginalTable() {
    // Get the original value
    String originalValue = inputTable.getRows().get(0).getValues().get(0).toString();

    // Update a cell
    UpdateCell.action(
        inputTable,
        "NAME",
        "Name",
        false,
        null,
        0.0,
        new StringValue("Changed Value")
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getRows().get(0).getValues().get(0).toString(), originalValue);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to update a cell in a null table
    UpdateCell.action(
        null,  // Null table
        "NAME",
        "Name",
        false,
        null,
        0.0,
        new StringValue("Test Value")
    );
  }


  @Test(expectedExceptions = BotCommandException.class)
  public void testWithInvalidSelectMethod() {
    // Try to update a cell with an invalid selection method
    UpdateCell.action(
        inputTable,
        "INVALID_METHOD",  // Invalid method
        "Name",
        false,
        null,
        0.0,
        new StringValue("Test Value")
    );
  }

  @Test
  public void testUpdateCellInLastRow() {
    // Update a cell in the last row
    int lastRowIndex = inputTable.getRows().size() - 1;

    TableValue result = UpdateCell.action(
        inputTable,
        "NAME",
        "Name",
        false,
        null,
        (double) lastRowIndex,
        new StringValue("Robert")
    );

    Table resultTable = result.get();

    // Verify the cell is updated
    Assert.assertEquals(resultTable.getRows().get(lastRowIndex).getValues().get(0).toString(),
        "Robert");
  }

  @Test
  public void testUpdateCellInLastColumn() {
    // Update a cell in the last column
    int lastColumnIndex = inputTable.getSchema().size() - 1;

    TableValue result = UpdateCell.action(
        inputTable,
        "INDEX",
        null,
        null,
        (double) lastColumnIndex,
        0.0,
        new StringValue("Canada")
    );

    Table resultTable = result.get();

    // Verify the cell is updated
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(lastColumnIndex).toString(),
        "Canada");
  }

}