package table;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.table.SliceTable;

/**
 * @author Test Author
 */
public class SliceTableTest {

  private Table inputTable;
  private List<Value> indexList;

  @BeforeMethod
  public void setUp() {

    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("ID"));
    schema.add(new Schema("Name"));
    schema.add(new Schema("Value"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Create 5 rows for testing
    for (int i = 0; i < 5; i++) {
      List<Value> rowValues = new ArrayList<>();
      rowValues.add(new StringValue(String.valueOf(i + 1)));  // ID
      rowValues.add(new StringValue("Name" + (i + 1)));      // Name
      rowValues.add(new StringValue("Value" + (i + 1)));     // Value
      rows.add(new Row(rowValues));
    }

    inputTable.setRows(rows);

    // Create index list for testing
    indexList = Arrays.asList(
        new NumberValue(0),  // First row
        new NumberValue(2),  // Third row
        new NumberValue(4)   // Fifth row
    );
  }

  @Test
  public void testSliceTableByRange() {
    // Extract rows by range
    TableValue result = SliceTable.action(
        inputTable,
        "RANGE",    // Select by range
        1.0,        // Start row index (second row)
        3.0,        // End row index (fourth row)
        null        // Index list not used
    );

    Table resultTable = result.get();

    // Verify the schema is preserved
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "ID");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "Name");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "Value");

    // Verify the extracted rows
    Assert.assertEquals(resultTable.getRows().size(), 3); // Rows 1, 2, and 3

    // Check the IDs of the extracted rows (should be 2, 3, 4)
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "2");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "3");
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(0).toString(), "4");
  }

  @Test
  public void testSliceTableByIndex() {
    // Extract rows by specific indices
    TableValue result = SliceTable.action(
        inputTable,
        "INDEX_LIST", // Select by index list
        null,         // Start row not used
        null,         // End row not used
        indexList     // List of indices to extract
    );

    Table resultTable = result.get();

    // Verify the schema is preserved
    Assert.assertEquals(resultTable.getSchema().size(), 3);

    // Verify the extracted rows
    Assert.assertEquals(resultTable.getRows().size(), 3); // 3 rows from the index list

    // Check the IDs of the extracted rows (should be 1, 3, 5)
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "1");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "3");
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(0).toString(), "5");
  }

  @Test
  public void testSliceWithRangeExceedingTableSize() {
    // Extract rows with end index exceeding table size
    TableValue result = SliceTable.action(
        inputTable,
        "RANGE",
        3.0,        // Start row index (fourth row)
        10.0,       // End row index (exceeds table size)
        null
    );

    Table resultTable = result.get();

    // Verify the extracted rows (should only extract existing rows)
    Assert.assertEquals(resultTable.getRows().size(), 2); // Rows 3 and 4 (indices start from 0)
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "4");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "5");
  }

  @Test
  public void testSliceWithSingleRowRange() {
    // Extract a single row using range
    TableValue result = SliceTable.action(
        inputTable,
        "RANGE",
        2.0,        // Start row index (third row)
        2.0,        // End row index (same as start)
        null
    );

    Table resultTable = result.get();

    // Verify the extracted row
    Assert.assertEquals(resultTable.getRows().size(), 1);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "3");
  }

  @Test
  public void testSliceWithSingleRowIndex() {
    // Create an index list with a single index
    List<Value> singleIndexList = List.of(
        new NumberValue(2)  // Third row
    );

    // Extract a single row using index list
    TableValue result = SliceTable.action(
        inputTable,
        "INDEX_LIST",
        null,
        null,
        singleIndexList
    );

    Table resultTable = result.get();

    // Verify the extracted row
    Assert.assertEquals(resultTable.getRows().size(), 1);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "3");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithInvalidIndex() {
    // Create an index list with an invalid index
    List<Value> invalidIndexList = Arrays.asList(
        new NumberValue(0),   // Valid
        new NumberValue(10)   // Invalid (out of bounds)
    );

    // Try to extract rows with an invalid index
    SliceTable.action(
        inputTable,
        "INDEX_LIST",
        null,
        null,
        invalidIndexList
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithNegativeStartIndex() {
    // Try to extract rows with a negative start index
    SliceTable.action(
        inputTable,
        "RANGE",
        -1.0,  // Negative start index
        3.0,
        null
    );
  }

  @Test
  public void testSliceWithNegativeEndIndex() {
    // Try to extract rows with a negative end index
    TableValue result = SliceTable.action(
        inputTable,
        "RANGE",
        0.0,
        -1.0,  // Negative end index
        null
    );
    Table resultTable = result.get();
    Assert.assertEquals(resultTable.getRows().size(), 0);
  }

  @Test
  public void testSliceWithEndIndexLessThanStartIndex() {
    // Try to extract rows with end index less than start index
    TableValue result = SliceTable.action(
        inputTable,
        "RANGE",
        3.0,   // Start index
        1.0,   // End index less than start
        null
    );
    Table resultTable = result.get();
    Assert.assertEquals(resultTable.getRows().size(), 0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithNullIndexList() {
    // Try to extract rows with a null index list
    SliceTable.action(
        inputTable,
        "INDEX_LIST",
        null,
        null,
        null  // Null index list
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithInvalidMethod() {
    // Try to extract rows with an invalid method
    SliceTable.action(
        inputTable,
        "INVALID_METHOD",  // Invalid method
        0.0,
        3.0,
        null
    );
  }

  @Test
  public void testPreservesOriginalTable() {
    // Get the original row count
    int originalRowCount = inputTable.getRows().size();

    // Slice table
    SliceTable.action(
        inputTable,
        "RANGE",
        1.0,
        3.0,
        null
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getRows().size(), originalRowCount);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to slice a null table
    SliceTable.action(
        null,  // Null table
        "RANGE",
        0.0,
        3.0,
        null
    );
  }

}