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
import sumit.devtools.actions.table.SliceColumns;

/**
 * @author Test Author
 */
public class SliceColumnsTest {

  private Table inputTable;
  private List<Value> indexList;
  private List<StringValue> columnNameList;

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
    schema.add(new Schema("Occupation"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("John"));
    row1Values.add(new StringValue("30"));
    row1Values.add(new StringValue("New York"));
    row1Values.add(new StringValue("USA"));
    row1Values.add(new StringValue("Engineer"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("Alice"));
    row2Values.add(new StringValue("25"));
    row2Values.add(new StringValue("Boston"));
    row2Values.add(new StringValue("USA"));
    row2Values.add(new StringValue("Doctor"));
    rows.add(new Row(row2Values));

    inputTable.setRows(rows);

    // Create index list for testing
    indexList = Arrays.asList(
        new NumberValue(0), // Name
        new NumberValue(2), // City
        new NumberValue(4)  // Occupation
    );

    // Create column name list for testing
    columnNameList = Arrays.asList(
        new StringValue("Name"),
        new StringValue("City"),
        new StringValue("Occupation")
    );
  }

  @Test
  public void testSliceColumnsByIndex() {
    // Extract columns by index
    TableValue result = SliceColumns.action(
        inputTable,
        "INDEX_LIST", // Select by index
        indexList,    // List of indexes to extract
        null          // Column names not used
    );

    Table resultTable = result.get();

    // Verify the schema
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Name");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "City");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "Occupation");

    // Verify the data
    Assert.assertEquals(resultTable.getRows().size(), 2);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "John");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "New York");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(2).toString(), "Engineer");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "Alice");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(1).toString(), "Boston");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(2).toString(), "Doctor");
  }

  @Test
  public void testSliceColumnsByName() {
    // Extract columns by name
    TableValue result = SliceColumns.action(
        inputTable,
        "COLUMN_NAMES", // Select by name
        null,           // Indexes not used
        columnNameList  // List of column names to extract
    );

    Table resultTable = result.get();

    // Verify the schema
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Name");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "City");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "Occupation");

    // Verify the data
    Assert.assertEquals(resultTable.getRows().size(), 2);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "John");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "New York");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(2).toString(), "Engineer");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithNullIndexList() {
    // Try to extract columns with a null index list
    SliceColumns.action(
        inputTable,
        "INDEX_LIST",
        null, // Null index list
        null
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithNullColumnNameList() {
    // Try to extract columns with a null column name list
    SliceColumns.action(
        inputTable,
        "COLUMN_NAMES",
        null,
        null // Null column name list
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithInvalidIndex() {
    // Create an index list with an out-of-bounds index
    List<Value> invalidIndexList = Arrays.asList(
        new NumberValue(0),  // Valid
        new NumberValue(10)  // Invalid (out of bounds)
    );

    // Try to extract columns with an invalid index
    SliceColumns.action(
        inputTable,
        "INDEX_LIST",
        invalidIndexList,
        null
    );
  }

  @Test
  public void testSliceWithInvalidColumnName() {
    // Create a column name list with a non-existent column
    List<StringValue> invalidColumnNameList = Arrays.asList(
        new StringValue("Name"),           // Valid
        new StringValue("NonExistentColumn") // Invalid
    );

    // Try to extract columns with an invalid column name
    TableValue result = SliceColumns.action(
        inputTable,
        "COLUMN_NAMES",
        null,
        invalidColumnNameList
    );
    Table resultTable = result.get();
    Assert.assertEquals(resultTable.getSchema().size(), 1);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Name");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "John");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSliceWithInvalidMethod() {
    // Try to extract columns with an invalid method
    SliceColumns.action(
        inputTable,
        "INVALID_METHOD", // Invalid method
        indexList,
        null
    );
  }

  @Test
  public void testSliceAllColumns() {
    // Create an index list with all column indexes
    List<Value> allIndexesList = Arrays.asList(
        new NumberValue(0),
        new NumberValue(1),
        new NumberValue(2),
        new NumberValue(3),
        new NumberValue(4)
    );

    // Extract all columns
    TableValue result = SliceColumns.action(
        inputTable,
        "INDEX_LIST",
        allIndexesList,
        null
    );

    Table resultTable = result.get();

    // Verify all columns are extracted
    Assert.assertEquals(resultTable.getSchema().size(), 5);
    Assert.assertEquals(resultTable.getRows().size(), 2);
  }

  @Test
  public void testExtractSingleColumn() {
    // Create an index list with a single index
    List<Value> singleIndexList = List.of(
        new NumberValue(1) // Age
    );

    // Extract a single column
    TableValue result = SliceColumns.action(
        inputTable,
        "INDEX_LIST",
        singleIndexList,
        null
    );

    Table resultTable = result.get();

    // Verify a single column is extracted
    Assert.assertEquals(resultTable.getSchema().size(), 1);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Age");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "30");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "25");
  }

  @Test
  public void testPreservesOriginalTable() {
    // Get the original schema size
    int originalSchemaSize = inputTable.getSchema().size();

    // Slice columns
    SliceColumns.action(
        inputTable,
        "INDEX_LIST",
        indexList,
        null
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getSchema().size(), originalSchemaSize);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to slice columns from a null table
    SliceColumns.action(
        null, // Null table
        "INDEX_LIST",
        indexList,
        null
    );
  }

}