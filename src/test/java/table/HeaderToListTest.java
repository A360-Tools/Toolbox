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
import sumit.devtools.actions.table.ConvertHeaderToList;

/**
 * @author Test Author
 */
public class HeaderToListTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {
    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Name"));
    schema.add(new Schema(" Age "));  // With whitespace for testing trim
    schema.add(new Schema("City"));
    inputTable.setSchema(schema);

    // Create rows (we won't use these for the test, but adding for completeness)
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("John"));
    row1Values.add(new StringValue("30"));
    row1Values.add(new StringValue("New York"));
    rows.add(new Row(row1Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testExtractHeadersWithoutTrim() {
    // Extract headers without trimming
    ListValue result = ConvertHeaderToList.action(
        inputTable,
        false  // Do not trim
    );

    // Verify the result
    List resultList = result.get();
    Assert.assertEquals(resultList.size(), 3);
    Assert.assertEquals(resultList.get(0).toString(), "Name");
    Assert.assertEquals(resultList.get(1).toString(), " Age ");  // Should keep the whitespace
    Assert.assertEquals(resultList.get(2).toString(), "City");
  }

  @Test
  public void testExtractHeadersWithTrim() {
    // Extract headers with trimming
    ListValue result = ConvertHeaderToList.action(
        inputTable,
        true  // Trim output values
    );

    // Verify the result
    List resultList = result.get();
    Assert.assertEquals(resultList.size(), 3);
    Assert.assertEquals(resultList.get(0).toString(), "Name");
    Assert.assertEquals(resultList.get(1).toString(), "Age");  // Should be trimmed
    Assert.assertEquals(resultList.get(2).toString(), "City");
  }

  @Test
  public void testExtractHeadersFromEmptyTable() {
    // Create a table with schema but no rows
    Table emptyTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Column1"));
    schema.add(new Schema("Column2"));
    emptyTable.setSchema(schema);
    emptyTable.setRows(new ArrayList<>());

    // Extract headers
    ListValue result = ConvertHeaderToList.action(
        emptyTable,
        false
    );

    // Verify the result
    List resultList = result.get();
    Assert.assertEquals(resultList.size(), 2);
    Assert.assertEquals(resultList.get(0).toString(), "Column1");
    Assert.assertEquals(resultList.get(1).toString(), "Column2");
  }

  @Test
  public void testExtractHeadersFromTableWithNoSchema() {
    // Create a table with no schema
    Table noSchemaTable = new Table();
    noSchemaTable.setSchema(new ArrayList<>());
    noSchemaTable.setRows(new ArrayList<>());

    // Extract headers
    ListValue result = ConvertHeaderToList.action(
        noSchemaTable,
        false
    );

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to extract headers from a null table
    ConvertHeaderToList.action(
        null,
        false
    );
  }

  @Test
  public void testExtractHeadersWithAllWhitespaceHeaders() {
    // Create a table with whitespace headers
    Table whitespaceTable = new Table();
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("   "));
    schema.add(new Schema("\t\t"));
    whitespaceTable.setSchema(schema);
    whitespaceTable.setRows(new ArrayList<>());

    // Extract headers with trim
    ListValue resultWithTrim = ConvertHeaderToList.action(
        whitespaceTable,
        true  // Trim output values
    );

    // Verify the trimmed result
    List trimmedList = resultWithTrim.get();
    Assert.assertEquals(trimmedList.size(), 2);
    Assert.assertEquals(trimmedList.get(0).toString(), "");
    Assert.assertEquals(trimmedList.get(1).toString(), "");

    // Extract headers without trim
    ListValue resultWithoutTrim = ConvertHeaderToList.action(
        whitespaceTable,
        false  // Do not trim
    );

    // Verify the untrimmed result
    List untrimmedList = resultWithoutTrim.get();
    Assert.assertEquals(untrimmedList.size(), 2);
    Assert.assertEquals(untrimmedList.get(0).toString(), "   ");
    Assert.assertEquals(untrimmedList.get(1).toString(), "\t\t");
  }

}