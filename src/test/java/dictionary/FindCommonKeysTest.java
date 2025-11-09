package dictionary;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.dictionary.FindCommonKeys;

public class FindCommonKeysTest {

  @Test
  public void testWithCommonKeys() {
    // Setup dictionaries with common keys
    Map<String, Value> firstDict = new HashMap<>();
    firstDict.put("key1",
        new StringValue("value1")); // Assuming StringValue is a valid implementation of Value
    firstDict.put("key2", new StringValue("value2"));
    Map<String, Value> secondDict = new HashMap<>();
    secondDict.put("key2", new StringValue("value2"));
    secondDict.put("key3", new StringValue("value3"));

    // Execute action and assume it returns List<Value<?>> of common values
    List<Value> commonValues = FindCommonKeys.action(firstDict, secondDict).get();

    // Check if 'key2' is among the values associated with common keys
    boolean value2IsCommon = commonValues.stream()
        .anyMatch(value -> "key2".equals(value.get().toString()));

    // Assert 'value2' is found among the values of common keys
    Assert.assertTrue(value2IsCommon, "'key2' should be found among common keys.");
  }

  @Test
  public void testWithNoCommonKeys() {
    // Setup dictionaries with no common keys
    Map<String, Value> firstDict = new HashMap<>();
    firstDict.put("key1", new StringValue("value1"));
    Map<String, Value> secondDict = new HashMap<>();
    secondDict.put("key2", new StringValue("value2"));

    // Execute action
    ListValue result = FindCommonKeys.action(firstDict, secondDict);

    // Assert no common keys
    Assert.assertTrue(result.get().isEmpty(), "No common keys should be found.");
  }

  @Test
  public void testWithEmptyDictionaries() {
    // Setup empty dictionaries
    Map<String, Value> firstDict = new HashMap<>();
    Map<String, Value> secondDict = new HashMap<>();

    // Execute action
    ListValue result = FindCommonKeys.action(firstDict, secondDict);

    // Assert result is empty
    Assert.assertTrue(result.get().isEmpty(),
        "Result should be empty for empty input dictionaries.");
  }

}
