package table;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.table.ConvertTableToDictionary;

/**
 * @author Test Author
 */
public class TableToDictionaryTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {
    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("ID"));       // Will be used as key
    schema.add(new Schema("Name"));     // Will be used as value
    schema.add(new Schema("Department"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("1"));
    row1Values.add(new StringValue("John"));
    row1Values.add(new StringValue("IT"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("2"));
    row2Values.add(new StringValue("Alice"));
    row2Values.add(new StringValue("HR"));
    rows.add(new Row(row2Values));

    // Row 3
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue("3"));
    row3Values.add(new StringValue("Bob"));
    row3Values.add(new StringValue("Finance"));
    rows.add(new Row(row3Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testTableToDictionaryByColumnName() {
    // Convert table to dictionary using column names
    DictionaryValue result = ConvertTableToDictionary.action(
        inputTable,
        "NAME",           // Select key column by name
        "ID",             // Key column name
        false,            // Case-insensitive for key
        null,             // Key column index not used
        "NAME",           // Select value column by name
        "Name",           // Value column name
        false,            // Case-insensitive for value
        null              // Value column index not used
    );

    Map<String, Value> resultMap = result.get();

    // Verify the dictionary
    Assert.assertEquals(resultMap.size(), 3);
    Assert.assertEquals(resultMap.get("1").toString(), "John");
    Assert.assertEquals(resultMap.get("2").toString(), "Alice");
    Assert.assertEquals(resultMap.get("3").toString(), "Bob");
  }

  @Test
  public void testTableToDictionaryByColumnIndex() {
    // Convert table to dictionary using column indices
    DictionaryValue result = ConvertTableToDictionary.action(
        inputTable,
        "INDEX",          // Select key column by index
        null,             // Key column name not used
        null,             // Key column index (ID)
        0.0,
        "INDEX",          // Select value column by index
        null,             // Value column name not used
        null,
        1.0               // Value column index (Name)
    );

    Map<String, Value> resultMap = result.get();

    // Verify the dictionary
    Assert.assertEquals(resultMap.size(), 3);
    Assert.assertEquals(resultMap.get("1").toString(), "John");
    Assert.assertEquals(resultMap.get("2").toString(), "Alice");
    Assert.assertEquals(resultMap.get("3").toString(), "Bob");
  }

  @Test
  public void testTableToDictionaryMixedMethod() {
    // Convert table to dictionary using name for key and index for value
    DictionaryValue result = ConvertTableToDictionary.action(
        inputTable,
        "NAME",           // Select key column by name
        "ID",             // Key column name
        false,            // Case-insensitive for key
        null,             // Key column index not used
        "INDEX",          // Select value column by index
        null,             // Value column name not used
        null,             // Case-insensitive for value (not applicable for INDEX)
        2.0               // Value column index (Department)
    );

    Map<String, Value> resultMap = result.get();

    // Verify the dictionary
    Assert.assertEquals(resultMap.size(), 3);
    Assert.assertEquals(resultMap.get("1").toString(), "IT");
    Assert.assertEquals(resultMap.get("2").toString(), "HR");
    Assert.assertEquals(resultMap.get("3").toString(), "Finance");
  }

  @Test
  public void testTableToDictionaryWithDuplicateKeys() {
    // Create a table with duplicate keys
    Table duplicateKeyTable = new Table();
    duplicateKeyTable.setSchema(inputTable.getSchema());

    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("1"));
    row1Values.add(new StringValue("John"));
    row1Values.add(new StringValue("IT"));
    rows.add(new Row(row1Values));

    // Row 2 (duplicate key)
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("1"));  // Duplicate key
    row2Values.add(new StringValue("Alice"));
    row2Values.add(new StringValue("HR"));
    rows.add(new Row(row2Values));

    duplicateKeyTable.setRows(rows);

    // Convert table to dictionary
    DictionaryValue result = ConvertTableToDictionary.action(
        duplicateKeyTable,
        "NAME",
        "ID",
        false,
        null,
        "NAME",
        "Name",
        false,
        null
    );

    Map<String, Value> resultMap = result.get();

    // Verify the dictionary (later entry should overwrite earlier one)
    Assert.assertEquals(resultMap.size(), 1);
    Assert.assertEquals(resultMap.get("1").toString(), "Alice");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testTableToDictionaryWithInvalidKeyColumnName() {
    // Try to convert with a non-existent key column name
    ConvertTableToDictionary.action(
        inputTable,
        "NAME",
        "NonExistentColumn", // Invalid column name
        false,
        null,
        "NAME",
        "Name",
        false,
        null
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testTableToDictionaryWithInvalidValueColumnName() {
    // Try to convert with a non-existent value column name
    ConvertTableToDictionary.action(
        inputTable,
        "NAME",
        "ID",
        false,
        null,
        "NAME",
        "NonExistentColumn", // Invalid column name
        false,
        null
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testTableToDictionaryWithInvalidKeyColumnIndex() {
    // Try to convert with an out-of-bounds key column index
    ConvertTableToDictionary.action(
        inputTable,
        "INDEX",
        null,
        null,
        10.0, // Out of bounds index
        "NAME",
        "Name",
        false,
        null
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testTableToDictionaryWithInvalidValueColumnIndex() {
    // Try to convert with an out-of-bounds value column index
    ConvertTableToDictionary.action(
        inputTable,
        "NAME",
        "ID",
        false,
        null,
        "INDEX",
        null,
        null,
        10.0 // Out of bounds index
    );
  }

  @Test
  public void testTableToDictionaryWithEmptyTable() {
    // Create an empty table (no rows)
    Table emptyTable = new Table();
    emptyTable.setSchema(inputTable.getSchema());
    emptyTable.setRows(new ArrayList<>());

    // Convert empty table to dictionary
    DictionaryValue result = ConvertTableToDictionary.action(
        emptyTable,
        "NAME",
        "ID",
        false,
        null,
        "NAME",
        "Name",
        false,
        null
    );

    // Verify the result is an empty dictionary
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to convert a null table to dictionary
    ConvertTableToDictionary.action(
        null, // Null table
        "NAME",
        "ID",
        false,
        null,
        "NAME",
        "Name",
        false,
        null
    );
  }

  @Test
  public void testWithSameColumnForKeyAndValue() {
    // Try to convert with the same column for both key and value
    ConvertTableToDictionary.action(
        inputTable,
        "NAME",
        "ID",
        false,
        null,
        "NAME",
        "ID", // Same as key column
        false,
        null
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithInvalidKeySelectMethod() {
    // Try to convert with an invalid key selection method
    ConvertTableToDictionary.action(
        inputTable,
        "INVALID_METHOD", // Invalid method
        "ID",
        false,
        null,
        "NAME",
        "Name",
        false,
        null
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithInvalidValueSelectMethod() {
    // Try to convert with an invalid value selection method
    ConvertTableToDictionary.action(
        inputTable,
        "NAME",
        "ID",
        false,
        null,
        "INVALID_METHOD", // Invalid method
        "Name",
        false,
        null
    );
  }

  @Test
  public void testCaseInsensitiveColumnName() {
    // Test case-insensitive column name matching
    DictionaryValue result = ConvertTableToDictionary.action(
        inputTable,
        "NAME",
        "id",  // lowercase, while schema has "ID"
        false, // Case-insensitive for key
        null,
        "NAME",
        "name",  // lowercase, while schema has "Name"
        false,   // Case-insensitive for value
        null
    );

    Map<String, Value> resultMap = result.get();
    Assert.assertEquals(resultMap.size(), 3);
    Assert.assertEquals(resultMap.get("1").toString(), "John");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testCaseSensitiveColumnName() {
    // Test case-sensitive column name matching (should fail)
    ConvertTableToDictionary.action(
        inputTable,
        "NAME",
        "id",  // lowercase, while schema has "ID"
        true,  // Case-sensitive for key (should fail to find "id")
        null,
        "NAME",
        "Name",
        false, // Case-insensitive for value
        null
    );
  }

}