package record;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.record.UpdateCell;

/**
 * @author Claude
 */
public class UpdateCellTest {

  private Record testRecord;

  @BeforeMethod
  public void setUp() {
    // Create a test record with some initial schema and values
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Name"));
    schema.add(new Schema("Age"));
    schema.add(new Schema("Email"));

    List<Value> values = new ArrayList<>();
    values.add(new StringValue("John Doe"));
    values.add(new StringValue("30"));
    values.add(new StringValue("john.doe@example.com"));

    testRecord = new Record();
    testRecord.setSchema(schema);
    testRecord.setValues(values);
  }

  @Test
  public void testUpdateCellByName() {
    // Test updating a cell by column name
    RecordValue result = UpdateCell.action(
        testRecord,
        "name",
        "Name",
        false,  // Case-insensitive
        null,
        new StringValue("Jane Smith")
    );

    Record modifiedRecord = result.get();
    Assert.assertEquals(modifiedRecord.getSchema().size(), 3);
    Assert.assertEquals(modifiedRecord.getSchema().get(0).getName(), "Name");
    Assert.assertEquals(modifiedRecord.getValues().get(0).toString(), "Jane Smith");

    // Verify other cells remain unchanged
    Assert.assertEquals(modifiedRecord.getValues().get(1).toString(), "30");
    Assert.assertEquals(modifiedRecord.getValues().get(2).toString(), "john.doe@example.com");
  }

  @Test
  public void testUpdateCellByIndex() {
    // Test updating a cell by column index
    RecordValue result = UpdateCell.action(
        testRecord,
        "index",
        null,
        null,  // Case-insensitive (not applicable for index)
        1.0,   // Index 1 corresponds to "Age"
        new StringValue("35")
    );

    Record modifiedRecord = result.get();
    Assert.assertEquals(modifiedRecord.getSchema().size(), 3);
    Assert.assertEquals(modifiedRecord.getValues().get(1).toString(), "35");

    // Verify other cells remain unchanged
    Assert.assertEquals(modifiedRecord.getValues().get(0).toString(), "John Doe");
    Assert.assertEquals(modifiedRecord.getValues().get(2).toString(), "john.doe@example.com");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellByInvalidName() {
    // Test updating a cell with a non-existent column name
    UpdateCell.action(
        testRecord,
        "name",
        "NonExistentColumn",
        false,
        null,
        new StringValue("Test Value")
    );
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testUpdateCellByInvalidIndex() {
    // Test updating a cell with an out-of-bounds column index
    UpdateCell.action(
        testRecord,
        "index",
        null,
        null,
        5.0, // Out of bounds index
        new StringValue("Test Value")
    );
  }

  @Test
  public void testUpdateCellPreservesOriginalRecord() {
    // Test that the original record is not modified
    Record originalRecord = new Record();
    originalRecord.setSchema(testRecord.getSchema());
    originalRecord.setValues(testRecord.getValues());

    UpdateCell.action(
        testRecord,
        "name",
        "Name",
        false,
        null,
        new StringValue("Modified Name")
    );

    // Verify original record remains unchanged
    Assert.assertEquals(testRecord.getValues().get(0).toString(), "John Doe");
  }

  @Test
  public void testCaseInsensitiveColumnName() {
    // Test case-insensitive column name matching
    RecordValue result = UpdateCell.action(
        testRecord,
        "name",
        "name",  // lowercase, while schema has "Name"
        false,   // Case-insensitive
        null,
        new StringValue("Jane Smith")
    );

    Record modifiedRecord = result.get();
    Assert.assertEquals(modifiedRecord.getValues().get(0).toString(), "Jane Smith");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testCaseSensitiveColumnName() {
    // Test case-sensitive column name matching (should fail)
    UpdateCell.action(
        testRecord,
        "name",
        "name",  // lowercase, while schema has "Name"
        true,    // Case-sensitive (should fail to find "name")
        null,
        new StringValue("Jane Smith")
    );
  }

}
