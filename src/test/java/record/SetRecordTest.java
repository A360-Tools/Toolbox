package record;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.record.SetRecord;

/**
 * @author Claude
 */
public class SetRecordTest {

  private Record testRecord;
  private List<Value> customValuesList;

  @BeforeMethod
  public void setUp() {
    // Create a test record with some initial schema and values
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("Name"));
    schema.add(new Schema("Age"));

    List<Value> values = new ArrayList<>();
    values.add(new StringValue("John Doe"));
    values.add(new StringValue("30"));

    testRecord = new Record();
    testRecord.setSchema(schema);
    testRecord.setValues(values);

    // Create a list of custom values to set
    customValuesList = new ArrayList<>();
  }

  @Test
  public void testSetExistingColumn() {
    // Test setting a value for an existing column
    Map<String, Value> customValues1 = new HashMap<>();
    customValues1.put("NAME", new StringValue("Name"));
    customValues1.put("VALUE", new StringValue("Jane Smith"));

    DictionaryValue dictValue1 = new DictionaryValue(customValues1);
    customValuesList.add(dictValue1);

    RecordValue result = SetRecord.action(testRecord, customValuesList, true, false, null);

    Record modifiedRecord = result.get();
    Assert.assertEquals(modifiedRecord.getSchema().size(), 2);
    Assert.assertEquals(modifiedRecord.getValues().get(0).toString(), "Jane Smith");
    Assert.assertEquals(modifiedRecord.getValues().get(1).toString(), "30");
  }

  @Test
  public void testAddNewColumn() {
    // Test adding a new column
    Map<String, Value> customValues = new HashMap<>();
    customValues.put("NAME", new StringValue("Email"));
    customValues.put("VALUE", new StringValue("john.doe@example.com"));

    DictionaryValue dictValue = new DictionaryValue(customValues);
    customValuesList.add(dictValue);

    RecordValue result = SetRecord.action(testRecord, customValuesList, true, false, null);

    Record modifiedRecord = result.get();
    Assert.assertEquals(modifiedRecord.getSchema().size(), 3);
    Assert.assertEquals(modifiedRecord.getSchema().get(2).getName(), "Email");
    Assert.assertEquals(modifiedRecord.getValues().get(2).toString(), "john.doe@example.com");
  }

  @Test
  public void testAddMultipleColumns() {
    // Test adding multiple columns at once
    Map<String, Value> customValues1 = new HashMap<>();
    customValues1.put("NAME", new StringValue("Email"));
    customValues1.put("VALUE", new StringValue("john.doe@example.com"));

    Map<String, Value> customValues2 = new HashMap<>();
    customValues2.put("NAME", new StringValue("Phone"));
    customValues2.put("VALUE", new StringValue("123-456-7890"));

    DictionaryValue dictValue1 = new DictionaryValue(customValues1);
    DictionaryValue dictValue2 = new DictionaryValue(customValues2);
    customValuesList.add(dictValue1);
    customValuesList.add(dictValue2);

    RecordValue result = SetRecord.action(testRecord, customValuesList, true, false, null);

    Record modifiedRecord = result.get();
    Assert.assertEquals(modifiedRecord.getSchema().size(), 4);
    Assert.assertEquals(modifiedRecord.getSchema().get(2).getName(), "Email");
    Assert.assertEquals(modifiedRecord.getSchema().get(3).getName(), "Phone");
    Assert.assertEquals(modifiedRecord.getValues().get(2).toString(), "john.doe@example.com");
    Assert.assertEquals(modifiedRecord.getValues().get(3).toString(), "123-456-7890");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testAddMissingColumnWithoutAddOption() {
    // Test attempting to add a new column when addIfMissing is false
    Map<String, Value> customValues = new HashMap<>();
    customValues.put("NAME", new StringValue("Email"));
    customValues.put("VALUE", new StringValue("john.doe@example.com"));

    DictionaryValue dictValue = new DictionaryValue(customValues);
    customValuesList.add(dictValue);

    SetRecord.action(testRecord, customValuesList, false, false, null);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testEmptyColumnName() {
    // Test with an empty column name
    Map<String, Value> customValues = new HashMap<>();
    customValues.put("NAME", new StringValue(""));
    customValues.put("VALUE", new StringValue("test value"));

    DictionaryValue dictValue = new DictionaryValue(customValues);
    customValuesList.add(dictValue);

    SetRecord.action(testRecord, customValuesList, true, false, null);
  }

  @Test
  public void testCaseInsensitiveColumnName() {
    // Test case-insensitive column name matching
    Map<String, Value> customValues = new HashMap<>();
    customValues.put("NAME", new StringValue("name")); // lowercase, while schema has "Name"
    customValues.put("VALUE", new StringValue("Jane Smith"));

    DictionaryValue dictValue = new DictionaryValue(customValues);
    customValuesList.add(dictValue);

    RecordValue result = SetRecord.action(testRecord, customValuesList, true, false, null);

    Record modifiedRecord = result.get();
    Assert.assertEquals(modifiedRecord.getValues().get(0).toString(), "Jane Smith");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testCaseSensitiveColumnName() {
    // Test case-sensitive column name matching (should fail to find "name")
    Map<String, Value> customValues = new HashMap<>();
    customValues.put("NAME", new StringValue("name")); // lowercase, while schema has "Name"
    customValues.put("VALUE", new StringValue("Jane Smith"));

    DictionaryValue dictValue = new DictionaryValue(customValues);
    customValuesList.add(dictValue);

    SetRecord.action(testRecord, customValuesList, false, true, null); // Case-sensitive, should fail
  }

}