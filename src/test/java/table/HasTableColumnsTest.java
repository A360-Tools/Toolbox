package table;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.botcore.api.dto.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.conditionals.table.HasTableColumns;

/**
 * @author Sumit Kumar
 */
public class HasTableColumnsTest {

  private Table testTable;
  private List<StringValue> columnNames;

  @BeforeMethod
  public void setUp() {
    // Create a test table with columns: "Name", "Email", "Age"
    testTable = new Table();
    List<Schema> schema = Arrays.asList(
        new Schema("Name", AttributeType.STRING),
        new Schema("Email", AttributeType.STRING),
        new Schema("Age", AttributeType.NUMBER)
    );
    testTable.setSchema(schema);
    testTable.setRows(new ArrayList<>());

    columnNames = new ArrayList<>();
  }

  @Test
  public void testHasAllColumnsExist_CaseInsensitive() {
    // Test with all columns that exist (case-insensitive)
    columnNames.add(new StringValue("name"));
    columnNames.add(new StringValue("EMAIL"));
    columnNames.add(new StringValue("Age"));

    Boolean result = HasTableColumns.validate(testTable, "CONTAINS", columnNames, false);
    Assert.assertTrue(result, "Should return true when all columns exist (case-insensitive)");
  }

  @Test
  public void testHasAllColumnsExist_CaseSensitive() {
    // Test with exact case match
    columnNames.add(new StringValue("Name"));
    columnNames.add(new StringValue("Email"));
    columnNames.add(new StringValue("Age"));

    Boolean result = HasTableColumns.validate(testTable, "CONTAINS", columnNames, true);
    Assert.assertTrue(result, "Should return true when all columns exist (case-sensitive match)");
  }

  @Test
  public void testHasAllColumnsDoNotExist_CaseSensitive() {
    // Test with wrong case (should fail with case-sensitive)
    columnNames.add(new StringValue("name")); // lowercase, but schema has "Name"
    columnNames.add(new StringValue("Email"));

    Boolean result = HasTableColumns.validate(testTable, "CONTAINS", columnNames, true);
    Assert.assertFalse(result,
        "Should return false when column case doesn't match (case-sensitive)");
  }

  @Test
  public void testHasSomeColumnsMissing() {
    // Test with some columns that don't exist
    columnNames.add(new StringValue("Name"));
    columnNames.add(new StringValue("Phone")); // Doesn't exist
    columnNames.add(new StringValue("Age"));

    Boolean result = HasTableColumns.validate(testTable, "CONTAINS", columnNames, false);
    Assert.assertFalse(result, "Should return false when some columns don't exist");
  }

  @Test
  public void testHasSingleColumn() {
    // Test with single column
    columnNames.add(new StringValue("Email"));

    Boolean result = HasTableColumns.validate(testTable, "CONTAINS", columnNames, false);
    Assert.assertTrue(result, "Should return true when single column exists");
  }

  @Test
  public void testDoesNotHaveCondition_AllColumnsExist() {
    // Test "NOTCONTAINS" when all columns exist
    columnNames.add(new StringValue("Name"));
    columnNames.add(new StringValue("Email"));

    Boolean result = HasTableColumns.validate(testTable, "NOTCONTAINS", columnNames, false);
    Assert.assertFalse(result,
        "Should return false for NOTCONTAINS when all columns exist");
  }

  @Test
  public void testDoesNotHaveCondition_SomeColumnsMissing() {
    // Test "NOTCONTAINS" when some columns don't exist
    columnNames.add(new StringValue("Name"));
    columnNames.add(new StringValue("Phone")); // Doesn't exist

    Boolean result = HasTableColumns.validate(testTable, "NOTCONTAINS", columnNames, false);
    Assert.assertTrue(result,
        "Should return true for NOTCONTAINS when some columns don't exist");
  }

  @Test
  public void testHasNonExistentColumns() {
    // Test with all non-existent columns
    columnNames.add(new StringValue("Address"));
    columnNames.add(new StringValue("Phone"));

    Boolean result = HasTableColumns.validate(testTable, "CONTAINS", columnNames, false);
    Assert.assertFalse(result, "Should return false when all columns don't exist");
  }

  @Test
  public void testCaseInsensitiveMatching() {
    // Test with various case combinations
    columnNames.add(new StringValue("NAME"));
    columnNames.add(new StringValue("email"));
    columnNames.add(new StringValue("aGe"));

    Boolean result = HasTableColumns.validate(testTable, "CONTAINS", columnNames, false);
    Assert.assertTrue(result,
        "Should return true with case-insensitive matching for various cases");
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*Input table or table schema cannot be null.*")
  public void testNullTable() {
    columnNames.add(new StringValue("Name"));
    HasTableColumns.validate(null, "CONTAINS", columnNames, false);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*Column names list cannot be null or empty.*")
  public void testNullColumnNames() {
    HasTableColumns.validate(testTable, "CONTAINS", null, false);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*Column names list cannot be null or empty.*")
  public void testEmptyColumnNames() {
    List<StringValue> emptyList = new ArrayList<>();
    HasTableColumns.validate(testTable, "CONTAINS", emptyList, false);
  }

  @Test
  public void testTableWithNoSchema() {
    Table emptyTable = new Table();
    emptyTable.setSchema(new ArrayList<>());
    emptyTable.setRows(new ArrayList<>());

    columnNames.add(new StringValue("Name"));

    Boolean result = HasTableColumns.validate(emptyTable, "CONTAINS", columnNames, false);
    Assert.assertFalse(result, "Should return false when table has no schema");
  }

  @Test
  public void testTableWithSpecialCharactersInColumnNames() {
    // Create a table with special characters in column names
    Table specialTable = new Table();
    List<Schema> schema = Arrays.asList(
        new Schema("First Name", AttributeType.STRING),
        new Schema("Email_Address", AttributeType.STRING),
        new Schema("Age-Years", AttributeType.NUMBER)
    );
    specialTable.setSchema(schema);
    specialTable.setRows(new ArrayList<>());

    columnNames.add(new StringValue("First Name"));
    columnNames.add(new StringValue("Email_Address"));

    Boolean result = HasTableColumns.validate(specialTable, "CONTAINS", columnNames, false);
    Assert.assertTrue(result, "Should handle column names with special characters");
  }
}
