package table;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.table.ConvertColumnToList;

/**
 * @author Test Author
 */
public class ColumnToListTest {

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
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("John"));
    row1Values.add(new StringValue("30"));
    row1Values.add(new StringValue("New York"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("Alice"));
    row2Values.add(new StringValue("25"));
    row2Values.add(new StringValue("Boston"));
    rows.add(new Row(row2Values));

    // Row 3
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue("Bob"));
    row3Values.add(new StringValue("35"));
    row3Values.add(new StringValue("Chicago"));
    rows.add(new Row(row3Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testExtractColumnByName() {
    // Extract a column by name
    ListValue result = ConvertColumnToList.action(
        inputTable,
        "name", // Select by name
        "Name", // Column name
        false,  // Case-insensitive
        null    // Column index not used
    );

    // Verify the result
    List resultList = result.get();
    Assert.assertEquals(resultList.size(), 3);
    Assert.assertEquals(resultList.get(0).toString(), "John");
    Assert.assertEquals(resultList.get(1).toString(), "Alice");
    Assert.assertEquals(resultList.get(2).toString(), "Bob");
  }

  @Test
  public void testExtractColumnByIndex() {
    // Extract a column by index
    ListValue result = ConvertColumnToList.action(
        inputTable,
        "index", // Select by index
        null,    // Column name not used
        null,    // Case-insensitive (not applicable for index)
        1.0      // Index 1 corresponds to "Age"
    );

    // Verify the result
    List resultList = result.get();
    Assert.assertEquals(resultList.size(), 3);
    Assert.assertEquals(resultList.get(0).toString(), "30");
    Assert.assertEquals(resultList.get(1).toString(), "25");
    Assert.assertEquals(resultList.get(2).toString(), "35");
  }

  @Test
  public void testExtractNonExistentColumnByName() {
    // Try to extract a column with a non-existent name
    ListValue result = ConvertColumnToList.action(
        inputTable,
        "name",
        "NonExistentColumn",
        false,
        null
    );
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testExtractColumnWithInvalidIndex() {
    // Try to extract a column with an out-of-bounds index
    ListValue result = ConvertColumnToList.action(
        inputTable,
        "index",
        null,
        null,
        10.0  // Out of bounds index
    );
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testExtractColumnFromEmptyTable() {
    // Create an empty table
    Table emptyTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("EmptyColumn"));
    emptyTable.setSchema(schema);
    emptyTable.setRows(new ArrayList<>());

    // Extract a column from the empty table
    ListValue result = ConvertColumnToList.action(
        emptyTable,
        "name",
        "EmptyColumn",
        false,
        null
    );

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testExtractLastColumn() {
    // Extract the last column
    ListValue result = ConvertColumnToList.action(
        inputTable,
        "index",
        null,
        null,
        2.0  // Index of the last column
    );

    // Verify the result
    List resultList = result.get();
    Assert.assertEquals(resultList.size(), 3);
    Assert.assertEquals(resultList.get(0).toString(), "New York");
    Assert.assertEquals(resultList.get(1).toString(), "Boston");
    Assert.assertEquals(resultList.get(2).toString(), "Chicago");
  }

  @Test
  public void testCaseInsensitiveColumnName() {
    // Extract a column with case-insensitive name matching
    ListValue result = ConvertColumnToList.action(
        inputTable,
        "name",
        "name",  // Lower case, while schema has "Name"
        false,   // Case-insensitive
        null
    );

    // Verify the result
    List resultList = result.get();
    Assert.assertEquals(resultList.size(), 3);
    Assert.assertEquals(resultList.get(0).toString(), "John");
  }

  @Test
  public void testCaseSensitiveColumnName() {
    // Extract a column with case-sensitive name matching
    ListValue result = ConvertColumnToList.action(
        inputTable,
        "name",
        "name",  // Lower case, while schema has "Name"
        true,    // Case-sensitive
        null
    );

    // With case-sensitive matching, should return empty list
    Assert.assertTrue(result.get().isEmpty());
  }

}