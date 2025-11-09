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
import sumit.devtools.actions.table.NormalizeHeaders;

/**
 * Test class for NormalizeHeaders action.
 *
 * @author Test Author
 */
public class NormalizeHeadersTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {
    // Create a sample table for testing
    inputTable = new Table();

    // Create schema with various whitespace and case patterns
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("  First  Name  "));
    schema.add(new Schema("\tLast\t\tName\t"));
    schema.add(new Schema(" EMAIL ADDRESS "));
    schema.add(new Schema("PhoneNumber"));
    schema.add(new Schema("  CITY  NAME  "));
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
  public void testNormalizeSpacesOnly() {
    // Normalize spaces only
    TableValue result = NormalizeHeaders.action(
        inputTable,
        true,   // normalizeSpaces
        false,  // removeAllWhitespace
        "NONE", // convertCase
        null    // help parameter
    );

    Table resultTable = result.get();

    // Verify headers are trimmed and multiple spaces collapsed
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "First Name");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "Last Name");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "EMAIL ADDRESS");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PhoneNumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "CITY NAME");
  }

  @Test
  public void testRemoveAllWhitespaceOnly() {
    // Remove all whitespace only
    TableValue result = NormalizeHeaders.action(
        inputTable,
        false,  // normalizeSpaces
        true,   // removeAllWhitespace
        "NONE", // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify all whitespace is removed
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "FirstName");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "LastName");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "EMAILADDRESS");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PhoneNumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "CITYNAME");
  }

  @Test
  public void testConvertToLowercaseOnly() {
    // Convert to lowercase only
    TableValue result = NormalizeHeaders.action(
        inputTable,
        false,        // normalizeSpaces
        false,        // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify all letters are lowercase (whitespace preserved)
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "  first  name  ");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "\tlast\t\tname\t");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), " email address ");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "phonenumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "  city  name  ");
  }

  @Test
  public void testConvertToUppercaseOnly() {
    // Convert to uppercase only
    TableValue result = NormalizeHeaders.action(
        inputTable,
        false,        // normalizeSpaces
        false,        // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify all letters are uppercase (whitespace preserved)
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "  FIRST  NAME  ");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "\tLAST\t\tNAME\t");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), " EMAIL ADDRESS ");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PHONENUMBER");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "  CITY  NAME  ");
  }

  @Test
  public void testNormalizeAndRemoveWhitespace() {
    // Both normalize and remove whitespace (remove should override normalize)
    TableValue result = NormalizeHeaders.action(
        inputTable,
        true,   // normalizeSpaces
        true,   // removeAllWhitespace
        "NONE", // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify all whitespace is removed (normalize then remove)
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "FirstName");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "LastName");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "EMAILADDRESS");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PhoneNumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "CITYNAME");
  }

  @Test
  public void testNormalizeAndLowercase() {
    // Normalize spaces and convert to lowercase
    TableValue result = NormalizeHeaders.action(
        inputTable,
        true,         // normalizeSpaces
        false,        // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify spaces are normalized and case is lowercase
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "first name");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "last name");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "email address");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "phonenumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "city name");
  }

  @Test
  public void testNormalizeAndUppercase() {
    // Normalize spaces and convert to uppercase
    TableValue result = NormalizeHeaders.action(
        inputTable,
        true,         // normalizeSpaces
        false,        // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify spaces are normalized and case is uppercase
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "FIRST NAME");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "LAST NAME");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "EMAIL ADDRESS");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PHONENUMBER");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "CITY NAME");
  }

  @Test
  public void testRemoveWhitespaceAndLowercase() {
    // Remove all whitespace and convert to lowercase
    TableValue result = NormalizeHeaders.action(
        inputTable,
        false,        // normalizeSpaces
        true,         // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify whitespace is removed and case is lowercase
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "firstname");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "lastname");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "emailaddress");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "phonenumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "cityname");
  }

  @Test
  public void testRemoveWhitespaceAndUppercase() {
    // Remove all whitespace and convert to uppercase
    TableValue result = NormalizeHeaders.action(
        inputTable,
        false,        // normalizeSpaces
        true,         // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify whitespace is removed and case is uppercase
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "FIRSTNAME");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "LASTNAME");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "EMAILADDRESS");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PHONENUMBER");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "CITYNAME");
  }


  @Test
  public void testAllOptionsEnabledWithLowercase() {
    // All options enabled with lowercase
    TableValue result = NormalizeHeaders.action(
        inputTable,
        true,         // normalizeSpaces
        true,         // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify all transformations are applied
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "firstname");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "lastname");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "emailaddress");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "phonenumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "cityname");
  }

  @Test
  public void testAllOptionsEnabledWithUppercase() {
    // All options enabled with uppercase
    TableValue result = NormalizeHeaders.action(
        inputTable,
        true,         // normalizeSpaces
        true,         // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify all transformations are applied with uppercase
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "FIRSTNAME");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "LASTNAME");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), "EMAILADDRESS");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PHONENUMBER");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "CITYNAME");
  }


  @Test
  public void testNoOptionsEnabled() {
    // No options enabled - headers should remain unchanged
    TableValue result = NormalizeHeaders.action(
        inputTable,
        false,  // normalizeSpaces
        false,  // removeAllWhitespace
        "NONE", // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify headers are unchanged
    Assert.assertEquals(resultTable.getSchema().get(0).getName(), "  First  Name  ");
    Assert.assertEquals(resultTable.getSchema().get(1).getName(), "\tLast\t\tName\t");
    Assert.assertEquals(resultTable.getSchema().get(2).getName(), " EMAIL ADDRESS ");
    Assert.assertEquals(resultTable.getSchema().get(3).getName(), "PhoneNumber");
    Assert.assertEquals(resultTable.getSchema().get(4).getName(), "  CITY  NAME  ");
  }

  @Test
  public void testPreservesOriginalTable() {
    // Store original header names
    List<String> originalHeaders = new ArrayList<>();
    for (Schema schema : inputTable.getSchema()) {
      originalHeaders.add(schema.getName());
    }

    // Normalize headers
    NormalizeHeaders.action(
        inputTable,
        true,         // normalizeSpaces
        true,         // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );

    // Verify the original table hasn't changed
    Assert.assertEquals(inputTable.getSchema().size(), originalHeaders.size());
    for (int i = 0; i < inputTable.getSchema().size(); i++) {
      Assert.assertEquals(inputTable.getSchema().get(i).getName(), originalHeaders.get(i));
    }
  }

  @Test
  public void testPreservesDataRows() {
    // Normalize headers
    TableValue result = NormalizeHeaders.action(
        inputTable,
        true,         // normalizeSpaces
        true,         // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );

    Table resultTable = result.get();

    // Verify data rows are preserved
    Assert.assertEquals(resultTable.getRows().size(), 1);
    for (int i = 0; i < 5; i++) {
      Assert.assertEquals(resultTable.getRows().get(0).getValues().get(i).toString(),
          "Value" + (i + 1));
    }
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to normalize headers of a null table
    NormalizeHeaders.action(
        null,
        true,
        false,
        "NONE",
        null
    );
  }

  @Test
  public void testWithEmptyTable() {
    // Create an empty table (no rows)
    Table emptyTable = new Table();
    emptyTable.setSchema(inputTable.getSchema());
    emptyTable.setRows(new ArrayList<>());

    // Normalize headers
    TableValue result = NormalizeHeaders.action(
        emptyTable,
        true,         // normalizeSpaces
        true,         // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );

    // Verify headers are normalized even with no rows
    Assert.assertEquals(result.get().getSchema().get(0).getName(), "firstname");
    Assert.assertTrue(result.get().getRows().isEmpty());
  }

  @Test
  public void testWithEmptyHeaders() {
    // Create a table with empty/whitespace-only headers
    Table emptyHeaderTable = new Table();
    List<Schema> emptyHeaderSchema = new ArrayList<>();
    emptyHeaderSchema.add(new Schema("   "));
    emptyHeaderSchema.add(new Schema("\t\t"));
    emptyHeaderSchema.add(new Schema(""));
    emptyHeaderTable.setSchema(emptyHeaderSchema);

    // Normalize spaces
    TableValue result = NormalizeHeaders.action(
        emptyHeaderTable,
        true,   // normalizeSpaces
        false,  // removeAllWhitespace
        "NONE", // convertCase
        null
    );

    // Verify empty headers are handled
    Assert.assertEquals(result.get().getSchema().get(0).getName(), "");
    Assert.assertEquals(result.get().getSchema().get(1).getName(), "");
    Assert.assertEquals(result.get().getSchema().get(2).getName(), "");
  }

  @Test
  public void testWithNoSchema() {
    // Create a table with no schema
    Table noSchemaTable = new Table();
    noSchemaTable.setSchema(new ArrayList<>());

    // Normalize headers
    TableValue result = NormalizeHeaders.action(
        noSchemaTable,
        true,
        true,
        "UPPERCASE",
        null
    );

    // Verify result has no schema
    Assert.assertTrue(result.get().getSchema().isEmpty());
  }

  @Test
  public void testWithSpecialWhitespaceCharacters() {
    // Create a table with headers containing various whitespace characters
    Table specialTable = new Table();
    List<Schema> specialSchema = new ArrayList<>();
    specialSchema.add(new Schema(" \t\n\r\f Name \t\n\r\f "));
    specialSchema.add(new Schema("A\u00A0B")); // Non-breaking space
    specialTable.setSchema(specialSchema);

    // Normalize spaces
    TableValue result = NormalizeHeaders.action(
        specialTable,
        true,   // normalizeSpaces
        false,  // removeAllWhitespace
        "NONE", // convertCase
        null
    );

    // Verify special whitespace is normalized
    Assert.assertEquals(result.get().getSchema().get(0).getName(), "Name");
    // Non-breaking space behavior depends on Java's \s regex pattern
    String normalized = result.get().getSchema().get(1).getName();
    Assert.assertTrue(normalized.contains("A") && normalized.contains("B"));
  }

  @Test
  public void testOrderOfOperations() {
    // Create a table to test order: normalize → remove → convert case
    Table testTable = new Table();
    List<Schema> testSchema = new ArrayList<>();
    testSchema.add(new Schema("  TEST  HEADER  "));
    testTable.setSchema(testSchema);

    // Test 1: normalize → lowercase (should be "test header")
    TableValue result1 = NormalizeHeaders.action(
        testTable,
        true,         // normalizeSpaces
        false,        // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );
    Assert.assertEquals(result1.get().getSchema().get(0).getName(), "test header");

    // Test 2: normalize → remove → lowercase (should be "testheader")
    TableValue result2 = NormalizeHeaders.action(
        testTable,
        true,         // normalizeSpaces
        true,         // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );
    Assert.assertEquals(result2.get().getSchema().get(0).getName(), "testheader");

    // Test 3: remove → lowercase (should be "testheader")
    TableValue result3 = NormalizeHeaders.action(
        testTable,
        false,        // normalizeSpaces
        true,         // removeAllWhitespace
        "LOWERCASE",  // convertCase
        null
    );
    Assert.assertEquals(result3.get().getSchema().get(0).getName(), "testheader");

    // Test 4: normalize → uppercase (should be "TEST HEADER")
    TableValue result4 = NormalizeHeaders.action(
        testTable,
        true,         // normalizeSpaces
        false,        // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );
    Assert.assertEquals(result4.get().getSchema().get(0).getName(), "TEST HEADER");

    // Test 5: normalize → remove → uppercase (should be "TESTHEADER")
    TableValue result5 = NormalizeHeaders.action(
        testTable,
        true,         // normalizeSpaces
        true,         // removeAllWhitespace
        "UPPERCASE",  // convertCase
        null
    );
    Assert.assertEquals(result5.get().getSchema().get(0).getName(), "TESTHEADER");

    // Test 6: no case conversion (should preserve "  TEST  HEADER  ")
    TableValue result6 = NormalizeHeaders.action(
        testTable,
        false,  // normalizeSpaces
        false,  // removeAllWhitespace
        "NONE", // convertCase
        null
    );
    Assert.assertEquals(result6.get().getSchema().get(0).getName(), "  TEST  HEADER  ");
  }

}
