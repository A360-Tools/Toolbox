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
import sumit.devtools.actions.table.RemoveEmptyColumns;

/**
 * Test class for RemoveEmptyColumns action.
 *
 * @author Test Author
 */
public class RemoveEmptyColumnsTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {
    // Create a sample table for testing
    inputTable = new Table();

    // Create schema with 5 columns
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2")); // Will be empty
    schema.add(new Schema("Col3"));
    schema.add(new Schema("Col4")); // Will be empty
    schema.add(new Schema("Col5"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("A1"));
    row1Values.add(new StringValue("")); // Empty
    row1Values.add(new StringValue("C1"));
    row1Values.add(new StringValue("")); // Empty
    row1Values.add(new StringValue("E1"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("A2"));
    row2Values.add(new StringValue("")); // Empty
    row2Values.add(new StringValue("C2"));
    row2Values.add(new StringValue("")); // Empty
    row2Values.add(new StringValue("E2"));
    rows.add(new Row(row2Values));

    // Row 3
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue("A3"));
    row3Values.add(new StringValue("")); // Empty
    row3Values.add(new StringValue("C3"));
    row3Values.add(new StringValue("")); // Empty
    row3Values.add(new StringValue("E3"));
    rows.add(new Row(row3Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testRemoveEmptyColumns() {
    // Remove empty columns
    TableValue result = RemoveEmptyColumns.action(
        inputTable,
        null // help parameter not used
    );

    Table resultTable = result.get();

    // Verify the result - should have 3 columns (Col1, Col3, Col5)
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Col1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "Col3");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "Col5");

    // Verify rows count
    Assert.assertEquals(resultTable.getRows().size(), 3);

    // Verify first row values
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "A1");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "C1");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(2).toString(), "E1");

    // Verify second row values
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "A2");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(1).toString(), "C2");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(2).toString(), "E2");

    // Verify third row values
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(0).toString(), "A3");
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(1).toString(), "C3");
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(2).toString(), "E3");
  }

  @Test
  public void testRemoveEmptyColumnsFromTableWithAllEmptyColumns() {
    // Create a table with all empty columns
    Table allEmptyTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2"));
    schema.add(new Schema("Col3"));
    allEmptyTable.setSchema(schema);

    List<Row> emptyRows = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      List<Value> emptyValues = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        emptyValues.add(new StringValue(""));
      }
      emptyRows.add(new Row(emptyValues));
    }
    allEmptyTable.setRows(emptyRows);

    // Remove empty columns
    TableValue result = RemoveEmptyColumns.action(
        allEmptyTable,
        null
    );

    // Verify the result - should have 0 columns
    Assert.assertEquals(result.get().getSchema().size(), 0);
    // Rows should still exist but with no values
    Assert.assertEquals(result.get().getRows().size(), 3);
    for (Row row : result.get().getRows()) {
      Assert.assertEquals(row.getValues().size(), 0);
    }
  }

  @Test
  public void testRemoveEmptyColumnsFromTableWithNoEmptyColumns() {
    // Create a table with no empty columns
    Table noEmptyTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2"));
    schema.add(new Schema("Col3"));
    noEmptyTable.setSchema(schema);

    List<Row> nonEmptyRows = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      List<Value> values = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        values.add(new StringValue("Value" + i + j));
      }
      nonEmptyRows.add(new Row(values));
    }
    noEmptyTable.setRows(nonEmptyRows);

    // Remove empty columns
    TableValue result = RemoveEmptyColumns.action(
        noEmptyTable,
        null
    );

    // Verify the result - should have all 3 columns
    Assert.assertEquals(result.get().getSchema().size(), 3);
    Assert.assertEquals(result.get().getRows().size(), 3);
  }

  @Test
  public void testRemoveEmptyColumnsFromEmptyTable() {
    // Create an empty table (no rows)
    Table emptyTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2"));
    schema.add(new Schema("Col3"));
    emptyTable.setSchema(schema);
    emptyTable.setRows(new ArrayList<>());

    // Remove empty columns
    TableValue result = RemoveEmptyColumns.action(
        emptyTable,
        null
    );

    // Verify the result - with no rows, all columns are considered empty
    Assert.assertEquals(result.get().getSchema().size(), 0);
    Assert.assertTrue(result.get().getRows().isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to remove empty columns from a null table
    RemoveEmptyColumns.action(
        null,
        null
    );
  }

  @Test
  public void testPartiallyEmptyColumns() {
    // Create a table with partially empty columns (should NOT be removed)
    Table partialEmptyTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2"));
    schema.add(new Schema("Col3"));
    partialEmptyTable.setSchema(schema);

    List<Row> rows = new ArrayList<>();

    // Row 1 - Col2 has a value
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("A1"));
    row1Values.add(new StringValue("B1"));
    row1Values.add(new StringValue("C1"));
    rows.add(new Row(row1Values));

    // Row 2 - Col2 is empty
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("A2"));
    row2Values.add(new StringValue(""));
    row2Values.add(new StringValue("C2"));
    rows.add(new Row(row2Values));

    // Row 3 - Col2 is empty
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue("A3"));
    row3Values.add(new StringValue(""));
    row3Values.add(new StringValue("C3"));
    rows.add(new Row(row3Values));

    partialEmptyTable.setRows(rows);

    // Remove empty columns
    TableValue result = RemoveEmptyColumns.action(
        partialEmptyTable,
        null
    );

    // Verify the result - all 3 columns should remain (Col2 has at least one non-empty value)
    Assert.assertEquals(result.get().getSchema().size(), 3);
    Assert.assertEquals(result.get().getRows().size(), 3);
  }

  @Test
  public void testPreservesOriginalTable() {
    // Create a copy of the original table for comparison
    int originalColumnCount = inputTable.getSchema().size();
    int originalRowCount = inputTable.getRows().size();

    // Remove empty columns
    RemoveEmptyColumns.action(
        inputTable,
        null
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getSchema().size(), originalColumnCount);
    Assert.assertEquals(inputTable.getRows().size(), originalRowCount);
  }

  @Test
  public void testWithNullValuesInColumn() {
    // Create a table with null values in columns
    Table nullValueTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2")); // Will have all nulls
    schema.add(new Schema("Col3"));
    nullValueTable.setSchema(schema);

    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("A1"));
    row1Values.add(null); // Null value
    row1Values.add(new StringValue("C1"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("A2"));
    row2Values.add(null); // Null value
    row2Values.add(new StringValue("C2"));
    rows.add(new Row(row2Values));

    nullValueTable.setRows(rows);

    // Remove empty columns
    TableValue result = RemoveEmptyColumns.action(
        nullValueTable,
        null
    );

    // Verify the result - should have 2 columns (Col1, Col3), Col2 should be removed
    Assert.assertEquals(result.get().getSchema().size(), 2);
    Assert.assertEquals(result.get().getSchema().get(0).getName(), "Col1");
    Assert.assertEquals(result.get().getSchema().get(1).getName(), "Col3");
  }

  @Test
  public void testMixedEmptyAndNullColumns() {
    // Create a table with mixed empty strings and null values
    Table mixedTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2")); // Empty strings
    schema.add(new Schema("Col3")); // Null values
    schema.add(new Schema("Col4"));
    mixedTable.setSchema(schema);

    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("A1"));
    row1Values.add(new StringValue(""));
    row1Values.add(null);
    row1Values.add(new StringValue("D1"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("A2"));
    row2Values.add(new StringValue(""));
    row2Values.add(null);
    row2Values.add(new StringValue("D2"));
    rows.add(new Row(row2Values));

    mixedTable.setRows(rows);

    // Remove empty columns
    TableValue result = RemoveEmptyColumns.action(
        mixedTable,
        null
    );

    // Verify the result - should have 2 columns (Col1, Col4)
    Assert.assertEquals(result.get().getSchema().size(), 2);
    Assert.assertEquals(result.get().getSchema().get(0).getName(), "Col1");
    Assert.assertEquals(result.get().getSchema().get(1).getName(), "Col4");
  }

}
