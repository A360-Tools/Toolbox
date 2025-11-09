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
import sumit.devtools.actions.table.RemoveEmptyRows;

/**
 * @author Test Author
 */
public class RemoveEmptyRowsTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {
    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2"));
    schema.add(new Schema("Col3"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1 - Normal row with data
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("Value1"));
    row1Values.add(new StringValue("Value2"));
    row1Values.add(new StringValue("Value3"));
    rows.add(new Row(row1Values));

    // Row 2 - Empty row (all null values)
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue(""));
    row2Values.add(new StringValue(""));
    row2Values.add(new StringValue(""));
    rows.add(new Row(row2Values));

    // Row 3 - Row with some empty values
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue(""));
    row3Values.add(new StringValue("NotEmpty"));
    row3Values.add(new StringValue(""));
    rows.add(new Row(row3Values));

    // Row 4 - Another empty row
    List<Value> row4Values = new ArrayList<>();
    row4Values.add(new StringValue(""));
    row4Values.add(new StringValue(""));
    row4Values.add(new StringValue(""));
    rows.add(new Row(row4Values));

    // Row 5 - Normal row with data
    List<Value> row5Values = new ArrayList<>();
    row5Values.add(new StringValue("AnotherValue1"));
    row5Values.add(new StringValue("AnotherValue2"));
    row5Values.add(new StringValue("AnotherValue3"));
    rows.add(new Row(row5Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testRemoveEmptyRows() {
    // Remove empty rows
    TableValue result = RemoveEmptyRows.action(
        null, // help parameter not used
        inputTable
    );

    Table resultTable = result.get();

    // Verify the result
    Assert.assertEquals(resultTable.getRows().size(), 3);

    // Verify that non-empty rows are preserved
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "Value1");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(1).toString(), "NotEmpty");
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(0).toString(),
        "AnotherValue1");
  }

  @Test
  public void testRemoveEmptyRowsFromTableWithAllEmptyRows() {
    // Create a table with all empty rows
    Table allEmptyTable = new Table();
    allEmptyTable.setSchema(inputTable.getSchema());

    List<Row> emptyRows = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      List<Value> emptyValues = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        emptyValues.add(new StringValue(""));
      }
      emptyRows.add(new Row(emptyValues));
    }
    allEmptyTable.setRows(emptyRows);

    // Remove empty rows
    TableValue result = RemoveEmptyRows.action(
        null,
        allEmptyTable
    );

    // Verify the result
    Assert.assertTrue(result.get().getRows().isEmpty());
  }

  @Test
  public void testRemoveEmptyRowsFromTableWithNoEmptyRows() {
    // Create a table with no empty rows
    Table noEmptyTable = new Table();
    noEmptyTable.setSchema(inputTable.getSchema());

    List<Row> nonEmptyRows = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      List<Value> values = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        values.add(new StringValue("Value"));
      }
      nonEmptyRows.add(new Row(values));
    }
    noEmptyTable.setRows(nonEmptyRows);

    // Remove empty rows
    TableValue result = RemoveEmptyRows.action(
        null,
        noEmptyTable
    );

    // Verify the result
    Assert.assertEquals(result.get().getRows().size(), 3);
  }

  @Test
  public void testRemoveEmptyRowsFromEmptyTable() {
    // Create an empty table (no rows)
    Table emptyTable = new Table();
    emptyTable.setSchema(inputTable.getSchema());
    emptyTable.setRows(new ArrayList<>());

    // Remove empty rows
    TableValue result = RemoveEmptyRows.action(
        null,
        emptyTable
    );

    // Verify the result
    Assert.assertTrue(result.get().getRows().isEmpty());
  }

  @Test
  public void testPreservesOriginalTable() {
    // Create a copy of the original table for comparison
    int originalRowCount = inputTable.getRows().size();

    // Remove empty rows
    RemoveEmptyRows.action(
        null,
        inputTable
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getRows().size(), originalRowCount);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to remove empty rows from a null table
    RemoveEmptyRows.action(
        null,
        null
    );
  }

  @Test
  public void testPartiallyEmptyRows() {
    // Create a table with partially empty rows
    Table partialEmptyTable = new Table();
    partialEmptyTable.setSchema(inputTable.getSchema());

    List<Row> rows = new ArrayList<>();

    // Row with some null values
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(null);
    row1Values.add(new StringValue("NotNull"));
    row1Values.add(null);
    rows.add(new Row(row1Values));

    // Row with all null values
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(null);
    row2Values.add(null);
    row2Values.add(null);
    rows.add(new Row(row2Values));

    partialEmptyTable.setRows(rows);

    // Remove empty rows
    TableValue result = RemoveEmptyRows.action(
        null,
        partialEmptyTable
    );

    // Verify the result
    Assert.assertEquals(result.get().getRows().size(), 1);
  }

}