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
import sumit.devtools.actions.table.SetRowAsHeader;

/**
 * @author Test Author
 */
public class SetRowAsHeaderTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {

    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("OldHeader1"));
    schema.add(new Schema("OldHeader2"));
    schema.add(new Schema("OldHeader3"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1 - Will be used as new header
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("NewHeader1"));
    row1Values.add(new StringValue("NewHeader2"));
    row1Values.add(new StringValue("NewHeader3"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("Value1"));
    row2Values.add(new StringValue("Value2"));
    row2Values.add(new StringValue("Value3"));
    rows.add(new Row(row2Values));

    // Row 3
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue("AnotherValue1"));
    row3Values.add(new StringValue("AnotherValue2"));
    row3Values.add(new StringValue("AnotherValue3"));
    rows.add(new Row(row3Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testSetRowAsHeaderAndRemoveRow() {
    // Set the first row as header and remove it
    TableValue result = SetRowAsHeader.action(
        null, // help parameter not used
        inputTable,
        0.0,  // Row index 0 (first row)
        true  // Remove row
    );

    Table resultTable = result.get();

    // Verify the schema has been updated
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "NewHeader1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "NewHeader2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "NewHeader3");

    // Verify the row has been removed
    Assert.assertEquals(resultTable.getRows().size(), 2);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "Value1");
  }

  @Test
  public void testSetRowAsHeaderWithoutRemovingRow() {
    // Set the first row as header without removing it
    TableValue result = SetRowAsHeader.action(
        null,
        inputTable,
        0.0,  // Row index 0 (first row)
        false // Do not remove row
    );

    Table resultTable = result.get();

    // Verify the schema has been updated
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "NewHeader1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "NewHeader2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "NewHeader3");

    // Verify the row has not been removed
    Assert.assertEquals(resultTable.getRows().size(), 3);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "NewHeader1");
  }

  @Test
  public void testSetMiddleRowAsHeader() {
    // Set the middle row as header
    TableValue result = SetRowAsHeader.action(
        null,
        inputTable,
        1.0,  // Row index 1 (second row)
        true  // Remove row
    );

    Table resultTable = result.get();

    // Verify the schema has been updated
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Value1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "Value2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "Value3");

    // Verify the correct row has been removed
    Assert.assertEquals(resultTable.getRows().size(), 2);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "NewHeader1");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(),
        "AnotherValue1");
  }

  @Test
  public void testSetLastRowAsHeader() {
    // Set the last row as header
    TableValue result = SetRowAsHeader.action(
        null,
        inputTable,
        2.0,  // Row index 2 (third row)
        true  // Remove row
    );

    Table resultTable = result.get();

    // Verify the schema has been updated
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "AnotherValue1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "AnotherValue2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "AnotherValue3");

    // Verify the correct row has been removed
    Assert.assertEquals(resultTable.getRows().size(), 2);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "NewHeader1");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "Value1");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithInvalidRowIndex() {
    // Try to set a non-existent row as header
    SetRowAsHeader.action(
        null,
        inputTable,
        10.0, // Out of bounds index
        true
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNegativeRowIndex() {
    // Try to set a row with negative index as header
    SetRowAsHeader.action(
        null,
        inputTable,
        -1.0, // Negative index
        true
    );
  }

  @Test
  public void testPreservesOriginalTable() {
    // Get the original schema
    List<String> originalHeader = new ArrayList<>();
    for (Schema schema : inputTable.getSchema()) {
      originalHeader.add(schema.getName());
    }

    // Set a row as header
    SetRowAsHeader.action(
        null,
        inputTable,
        0.0,
        true
    );

    // Verify the original table hasn't changed
    for (int i = 0; i < inputTable.getSchema().size(); i++) {
      Assert.assertEquals(inputTable.getSchema().get(i).getName(), originalHeader.get(i));
    }
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithEmptyTable() {
    // Create an empty table (no rows)
    Table emptyTable = new Table();
    emptyTable.setSchema(inputTable.getSchema());
    emptyTable.setRows(new ArrayList<>());

    // Try to set a row as header
    SetRowAsHeader.action(
        null,
        emptyTable,
        0.0,
        true
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to set a row as header on a null table
    SetRowAsHeader.action(
        null,
        null,
        0.0,
        true
    );
  }

}