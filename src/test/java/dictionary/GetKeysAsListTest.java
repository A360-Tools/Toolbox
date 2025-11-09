package dictionary;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.dictionary.GetKeysAsList;

public class GetKeysAsListTest {

  @Test
  public void testGetKeysFromPopulatedDictionary() {
    // Setup dictionary with keys
    Map<String, Value> dictionary = new HashMap<>();
    dictionary.put("key1", new StringValue("value1"));
    dictionary.put("key2", new StringValue("value2"));
    dictionary.put("key3", new StringValue("value3"));

    // Expected keys
    Set<String> expectedKeys = Set.of("key1", "key2", "key3");

    // Execute action
    ListValue<?> result = GetKeysAsList.action(dictionary);

    // Convert result to Set<String> for comparison
    Set<String> resultKeys = result.get().stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());

    // Assert all expected keys are present
    Assert.assertEquals(resultKeys.size(), expectedKeys.size(),
        "Result size should match expected keys size");
    Assert.assertTrue(resultKeys.containsAll(expectedKeys),
        "Result should contain all expected keys");
  }

  @Test
  public void testGetKeysFromEmptyDictionary() {
    // Setup empty dictionary
    Map<String, Value> dictionary = new HashMap<>();

    // Execute action
    ListValue<?> result = GetKeysAsList.action(dictionary);

    // Assert result is empty
    Assert.assertTrue(result.get().isEmpty(), "Result should be empty for empty input dictionary");
  }

  @Test
  public void testGetKeysPreservesAllKeys() {
    // Setup dictionary with multiple different keys
    Map<String, Value> dictionary = new HashMap<>();
    dictionary.put("numeric1", new StringValue("1"));
    dictionary.put("specialChar#", new StringValue("special"));
    dictionary.put("UPPERCASE", new StringValue("upper"));
    dictionary.put("lowercase", new StringValue("lower"));
    dictionary.put("mixed_Case_123", new StringValue("mixed"));

    // Execute action
    ListValue<?> result = GetKeysAsList.action(dictionary);

    // Verify all keys are preserved
    Assert.assertEquals(result.get().size(), 5, "Result should contain all 5 keys");

    // Convert result to Set<String> for verification
    Set<String> resultKeys = result.get().stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());

    // Check if all original keys are present
    Assert.assertTrue(resultKeys.contains("numeric1"), "Key 'numeric1' should be present");
    Assert.assertTrue(resultKeys.contains("specialChar#"), "Key 'specialChar#' should be present");
    Assert.assertTrue(resultKeys.contains("UPPERCASE"), "Key 'UPPERCASE' should be present");
    Assert.assertTrue(resultKeys.contains("lowercase"), "Key 'lowercase' should be present");
    Assert.assertTrue(resultKeys.contains("mixed_Case_123"),
        "Key 'mixed_Case_123' should be present");
  }

}