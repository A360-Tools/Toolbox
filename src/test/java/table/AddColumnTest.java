package table;

import com.automationanywhere.botcommand.data.Value;
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
import sumit.devtools.actions.table.AddColumn;

/**
 * @author Test Author
 */
public class AddColumnTest {

  private Table inputTable;
  private List<Value> columnValues;

  @BeforeMethod
  public void setUp() {
    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Col1"));
    schema.add(new Schema("Col2"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("R1C1"));
    row1Values.add(new StringValue("R1C2"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("R2C1"));
    row2Values.add(new StringValue("R2C2"));
    rows.add(new Row(row2Values));

    inputTable.setRows(rows);

    // Initialize column values for LIST insertion method
    columnValues = Arrays.asList(
        new StringValue("New Value 1"),
        new StringValue("New Value 2")
    );
  }

  @Test
  public void testAddColumnToEnd() {
    // Add column to the end of the table with default value
    TableValue result = AddColumn.action(
        null, // help param not used in test
        inputTable,
        "NewColumn",
        "END", // Add to end
        null, // position not needed for END
        "DEFAULT", // Use default value
        new StringValue("Default"), // Default value
        null // List not needed for DEFAULT
    );

    Table resultTable = result.get();

    // Verify schema
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "NewColumn");

    // Verify values
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(2).toString(), "Default");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(2).toString(), "Default");
  }

  @Test
  public void testAddColumnAtSpecificIndex() {
    // Add column at specific index (1) with default value
    TableValue result = AddColumn.action(
        null, // help param not used in test
        inputTable,
        "InsertedColumn",
        "INDEX", // Add at specific index
        1.0, // Position (index 1, which is the second column)
        "DEFAULT", // Use default value
        new StringValue("Inserted"), // Default value
        null // List not needed for DEFAULT
    );

    Table resultTable = result.get();

    // Verify schema
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "InsertedColumn");

    // Verify values and order
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Col1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "InsertedColumn");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "Col2");

    // Verify cell values
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "Inserted");
  }

  @Test
  public void testAddColumnWithList() {
    // Add column with list of values
    TableValue result = AddColumn.action(
        null, // help param not used in test
        inputTable,
        "ListColumn",
        "END", // Add to end
        null, // position not needed for END
        "LIST", // Use list of values
        null, // Default value not needed for LIST
        columnValues // List of values
    );

    Table resultTable = result.get();

    // Verify schema
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "ListColumn");

    // Verify values
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(2).toString(), "New Value 1");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(2).toString(), "New Value 2");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testAddColumnWithInsufficientListValues() {
    // Create a list with fewer values than table rows
    List<Value> insufficientValues = new ArrayList<>();
    insufficientValues.add(new StringValue("Only One Value"));

    // This should throw a BotCommandException due to insufficient values
    AddColumn.action(
        null,
        inputTable,
        "InsufficientValues",
        "END",
        null,
        "LIST",
        null,
        insufficientValues
    );
  }

  @Test
  public void testAddColumnPreservesOriginalTable() {
    // Create a copy of the original table for comparison
    int originalSchemaSize = inputTable.getSchema().size();

    // Add a column
    AddColumn.action(
        null,
        inputTable,
        "NewColumn",
        "END",
        null,
        "DEFAULT",
        new StringValue("Default"),
        null
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getSchema().size(), originalSchemaSize);
  }

}