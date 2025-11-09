package table;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.table.SetTableSchema;

/**
 * @author Test Author
 */
@SuppressWarnings("rawtypes")
public class SetTableSchemaTest {

  private Table inputTable;
  private Table schemaTable;
  private Record inputRecord;

  @BeforeMethod
  public void setUp() {

    // Create a sample table for testing
    inputTable = new Table();

    // Create schema for input table
    List<Schema> inputSchema = new ArrayList<>();
    inputSchema.add(new Schema("OldCol1"));
    inputSchema.add(new Schema("OldCol2"));
    inputTable.setSchema(inputSchema);

    // Create rows for input table
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("Value1"));
    row1Values.add(new StringValue("Value2"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("Value3"));
    row2Values.add(new StringValue("Value4"));
    rows.add(new Row(row2Values));

    inputTable.setRows(rows);

    // Create a sample table to provide schema
    schemaTable = new Table();

    // Create schema for schema table
    List<Schema> schemaTableSchema = new ArrayList<>();
    schemaTableSchema.add(new Schema("NewCol1"));
    schemaTableSchema.add(new Schema("NewCol2"));
    schemaTableSchema.add(new Schema("NewCol3"));
    schemaTable.setSchema(schemaTableSchema);

    // Create a sample record to provide schema
    inputRecord = new Record();

    // Create schema for record
    List<Schema> recordSchema = new ArrayList<>();
    recordSchema.add(new Schema("RecCol1"));
    recordSchema.add(new Schema("RecCol2"));
    recordSchema.add(new Schema("RecCol3"));
    inputRecord.setSchema(recordSchema);

    // Create values for record
    List<Value> recordValues = new ArrayList<>();
    recordValues.add(new StringValue("RecValue1"));
    recordValues.add(new StringValue("RecValue2"));
    recordValues.add(new StringValue("RecValue3"));
    inputRecord.setValues(recordValues);
  }

  @Test
  public void testCopySchemaFromTable() {
    // Set schema from another table (overwrite mode)
    TableValue result = SetTableSchema.action(
        null, // help parameter not used
        "table", // Source of schema (table)
        null, // Record not used
        schemaTable, // Table to copy schema from
        inputTable, // Base table
        "overwrite_schema" // Overwrite schema
    );

    Table resultTable = result.get();

    // Verify the schema has been updated
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "NewCol1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "NewCol2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "NewCol3");

    // Verify the data is preserved
    Assert.assertEquals(resultTable.getRows().size(), 2);

    // Original table had fewer columns, so new column cells should be null or default
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "Value1");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "Value2");
  }

  @Test
  public void testCopySchemaFromRecord() {
    // Set schema from a record (overwrite mode)
    TableValue result = SetTableSchema.action(
        null, // help parameter not used
        "record", // Source of schema (record)
        inputRecord, // Record to copy schema from
        null, // Table not used
        inputTable, // Base table
        "overwrite_schema" // Overwrite schema
    );

    Table resultTable = result.get();

    // Verify the schema has been updated
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "RecCol1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "RecCol2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "RecCol3");
  }

  @Test
  public void testAppendSchemaFromTable() {
    // Append schema from another table
    TableValue result = SetTableSchema.action(
        null, // help parameter not used
        "table", // Source of schema (table)
        null, // Record not used
        schemaTable, // Table to copy schema from
        inputTable, // Base table
        "append_schema" // Append schema
    );

    Table resultTable = result.get();

    // Verify the schema has been appended (not overwritten)
    Assert.assertEquals(resultTable.getSchema().size(), 5); // 2 original + 3 new
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "OldCol1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "OldCol2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "NewCol1");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "NewCol2");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "NewCol3");
  }

  @Test
  public void testAppendSchemaFromRecord() {
    // Append schema from a record
    TableValue result = SetTableSchema.action(
        null, // help parameter not used
        "record", // Source of schema (record)
        inputRecord, // Record to copy schema from
        null, // Table not used
        inputTable, // Base table
        "append_schema" // Append schema
    );

    Table resultTable = result.get();

    // Verify the schema has been appended (not overwritten)
    Assert.assertEquals(resultTable.getSchema().size(), 5); // 2 original + 3 new
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "OldCol1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "OldCol2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "RecCol1");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "RecCol2");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "RecCol3");
  }

  @Test
  public void testAppendSchemaWithDuplicates() {
    // Create a table with some duplicate schema names
    Table duplicateSchemaTable = new Table();
    List<Schema> duplicateSchema = new ArrayList<>();
    duplicateSchema.add(new Schema("OldCol1")); // Duplicate of existing column
    duplicateSchema.add(new Schema("NewColumn"));
    duplicateSchemaTable.setSchema(duplicateSchema);

    // Append schema with duplicates
    TableValue result = SetTableSchema.action(
        null,
        "table",
        null,
        duplicateSchemaTable,
        inputTable,
        "append_schema"
    );

    Table resultTable = result.get();

    // Verify the schema has been appended correctly (duplicates are allowed)
    Assert.assertEquals(resultTable.getSchema().size(), 4); // 2 original + 1 new +1 duplicate
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "OldCol1");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "OldCol2");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "OldCol1");//duplicate
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "NewColumn");
  }

  @Test
  public void testPreservesOriginalTable() {
    // Get the original schema
    int originalSchemaSize = inputTable.getSchema().size();

    // Set schema from another table
    SetTableSchema.action(
        null,
        "table",
        null,
        schemaTable,
        inputTable,
        "overwrite_schema"
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getSchema().size(), originalSchemaSize);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithInvalidSourceType() {
    // Try to set schema with an invalid source type
    TableValue result = SetTableSchema.action(
        null,
        "invalid_source_type", // Invalid source type
        inputRecord,
        schemaTable,
        inputTable,
        "overwrite_schema"
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullInputTable() {
    // Try to set schema on a null table
    SetTableSchema.action(
        null,
        "table",
        null,
        schemaTable,
        null, // Null input table
        "overwrite_schema"
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullSourceTable() {
    // Try to set schema from a null source table
    SetTableSchema.action(
        null,
        "table",
        null,
        null, // Null source table
        inputTable,
        "overwrite_schema"
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullSourceRecord() {
    // Try to set schema from a null source record
    SetTableSchema.action(
        null,
        "record",
        null, // Null source record
        null,
        inputTable,
        "overwrite_schema"
    );
  }

}