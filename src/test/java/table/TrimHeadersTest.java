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
import sumit.devtools.actions.table.TrimHeaders;

/**
 * @author Test Author
 */
public class TrimHeadersTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {

    // Create a sample table for testing
    inputTable = new Table();

    // Create schema with whitespace in header names
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("  Name  "));
    schema.add(new Schema("\tAge\t"));
    schema.add(new Schema(" City "));
    schema.add(new Schema("Country")); // No whitespace
    schema.add(new Schema("  \n  Department  \n  "));
    inputTable.setSchema(schema);

    // Create a row for completeness
    List<Row> rows = new ArrayList<>();
    List<Value> rowValues = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      rowValues.add(new StringValue("Value" + (i + 1)));
    }
    rows.add(new Row(rowValues));
    inputTable.setRows(rows);
  }

  @Test
  public void testTrimHeaders() {
    // Trim header whitespace
    TableValue result = TrimHeaders.action(
        null, // help parameter not used
        inputTable
    );

    Table resultTable = result.get();

    // Verify the headers are trimmed
    Assert.assertEquals(resultTable.getSchema().size(), 5);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Name");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "Age");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "City");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "Country");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "Department");

    // Verify the data is preserved
    Assert.assertEquals(resultTable.getRows().size(), 1);
    for (int i = 0; i < 5; i++) {
      Assert.assertEquals(resultTable.getRows().get(0).getValues().get(i).toString(),
          "Value" + (i + 1));
    }
  }

  @Test
  public void testTrimHeadersWithEmptyHeaders() {
    // Create a table with empty headers
    Table emptyHeaderTable = new Table();
    List<Schema> emptyHeaderSchema = new ArrayList<>();
    emptyHeaderSchema.add(new Schema("  "));
    emptyHeaderSchema.add(new Schema("\t\t"));
    emptyHeaderSchema.add(new Schema("\n\n"));
    emptyHeaderTable.setSchema(emptyHeaderSchema);

    // Add a row for completeness
    List<Row> rows = new ArrayList<>();
    List<Value> rowValues = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      rowValues.add(new StringValue("Value" + (i + 1)));
    }
    rows.add(new Row(rowValues));
    emptyHeaderTable.setRows(rows);

    // Trim header whitespace
    TableValue result = TrimHeaders.action(
        null,
        emptyHeaderTable
    );

    Table resultTable = result.get();

    // Verify the headers are trimmed to empty strings
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "");
  }

  @Test
  public void testTrimHeadersWithNoWhitespace() {
    // Create a table with headers that have no whitespace
    Table noWhitespaceTable = new Table();
    List<Schema> noWhitespaceSchema = new ArrayList<>();
    noWhitespaceSchema.add(new Schema("Name"));
    noWhitespaceSchema.add(new Schema("Age"));
    noWhitespaceSchema.add(new Schema("City"));
    noWhitespaceTable.setSchema(noWhitespaceSchema);

    // Trim header whitespace
    TableValue result = TrimHeaders.action(
        null,
        noWhitespaceTable
    );

    Table resultTable = result.get();

    // Verify the headers remain the same
    Assert.assertEquals(resultTable.getSchema().size(), 3);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Name");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "Age");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "City");
  }

  @Test
  public void testTrimHeadersWithEmptyTable() {
    // Create an empty table with schema but no rows
    Table emptyTable = new Table();
    emptyTable.setSchema(inputTable.getSchema());
    emptyTable.setRows(new ArrayList<>());

    // Trim header whitespace
    TableValue result = TrimHeaders.action(
        null,
        emptyTable
    );

    Table resultTable = result.get();

    // Verify the headers are trimmed
    Assert.assertEquals(resultTable.getSchema().size(), 5);
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "Name");

    // Verify there are no rows
    Assert.assertTrue(resultTable.getRows().isEmpty());
  }

  @Test
  public void testTrimHeadersPreservesOriginalTable() {
    // Get the original header names
    List<String> originalHeaders = new ArrayList<>();
    for (Schema schema : inputTable.getSchema()) {
      originalHeaders.add(schema.getName());
    }

    // Trim headers
    TrimHeaders.action(
        null,
        inputTable
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getSchema().size(), originalHeaders.size());
    for (int i = 0; i < inputTable.getSchema().size(); i++) {
      Assert.assertEquals(inputTable.getSchema().get(i).getName(), originalHeaders.get(i));
    }
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to trim headers of a null table
    TrimHeaders.action(
        null,
        null // Null table
    );
  }

  @Test
  public void testTrimHeadersWithNoSchema() {
    // Create a table with no schema
    Table noSchemaTable = new Table();
    noSchemaTable.setSchema(new ArrayList<>());

    // Trim header whitespace
    TableValue result = TrimHeaders.action(
        null,
        noSchemaTable
    );

    // Verify the result has no schema
    Assert.assertTrue(result.get().getSchema().isEmpty());
  }

  @Test
  public void testTrimHeadersWithSpecialWhitespaceCharacters() {
    // Create a table with headers containing various whitespace characters
    Table specialWhitespaceTable = new Table();
    List<Schema> specialSchema = new ArrayList<>();
    specialSchema.add(new Schema(" \t\n\r\f Name \t\n\r\f "));
    specialWhitespaceTable.setSchema(specialSchema);

    // Trim header whitespace
    TableValue result = TrimHeaders.action(
        null,
        specialWhitespaceTable
    );

    // Verify the header is trimmed
    Assert.assertEquals(result.get().getSchema().get(0).getName(), "Name");
  }

}