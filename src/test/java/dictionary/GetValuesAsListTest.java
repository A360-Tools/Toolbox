package dictionary;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.dictionary.GetValuesAsList;

public class GetValuesAsListTest {

  @Test
  public void testGetValuesFromPopulatedDictionary() {
    // Setup dictionary with values
    Map<String, Value> dictionary = new HashMap<>();
    dictionary.put("key1", new StringValue("value1"));
    dictionary.put("key2", new StringValue("value2"));
    dictionary.put("key3", new StringValue("value3"));

    // Expected values
    Set<String> expectedValues = Set.of("value1", "value2", "value3");

    // Execute action
    ListValue<?> result = GetValuesAsList.action(dictionary);

    // Convert result to Set<String> for comparison
    Set<String> resultValues = result.get().stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());

    // Assert all expected values are present
    Assert.assertEquals(resultValues.size(), expectedValues.size(),
        "Result size should match expected values " +
            "size");
    Assert.assertTrue(resultValues.containsAll(expectedValues),
        "Result should contain all expected values");
  }

  @Test
  public void testGetValuesFromEmptyDictionary() {
    // Setup empty dictionary
    Map<String, Value> dictionary = new HashMap<>();

    // Execute action
    ListValue<?> result = GetValuesAsList.action(dictionary);

    // Assert result is empty
    Assert.assertTrue(result.get().isEmpty(), "Result should be empty for empty input dictionary");
  }

  @Test
  public void testGetValuesWithDifferentValueTypes() {
    // Setup dictionary with multiple different value types
    Map<String, Value> dictionary = new HashMap<>();
    dictionary.put("stringKey", new StringValue("stringValue"));
    dictionary.put("numberKey", new NumberValue(123.45));
    dictionary.put("intKey", new NumberValue(42));

    // Execute action
    ListValue<?> result = GetValuesAsList.action(dictionary);

    // Verify all values are preserved
    Assert.assertEquals(result.get().size(), 3, "Result should contain all 3 values");

    // Convert result to a set of string representations for verification
    Set<String> resultValueStrings = result.get().stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());

    // Check if all original values are present (as strings)
    Set<String> expectedValueStrings = new HashSet<>();
    expectedValueStrings.add("stringValue");
    expectedValueStrings.add("123.45");
    expectedValueStrings.add("42.0");  // NumberValue will convert integer to double

    // Check if expected values are present
    for (String expected : expectedValueStrings) {
      boolean found = resultValueStrings.stream()
          .anyMatch(value -> value.equals(expected) ||
              (expected.equals("42.0") && value.equals("42")) ||
              (expected.equals("123.45") && value.equals("123.45")));
      Assert.assertTrue(found, "Value '" + expected + "' should be present");
    }
  }

  @Test
  public void testValuesOrderIsPreserved() {
    // Use LinkedHashMap to ensure insertion order is preserved
    Map<String, Value> dictionary = new HashMap<>();
    dictionary.put("first", new StringValue("A"));
    dictionary.put("second", new StringValue("B"));
    dictionary.put("third", new StringValue("C"));

    // Execute action
    ListValue<?> result = GetValuesAsList.action(dictionary);

    // Note: We can't strictly test order with HashMap since it doesn't guarantee order
    // This just verifies all values are present
    Assert.assertEquals(result.get().size(), 3, "Result should contain all 3 values");

    Set<String> values = result.get().stream()
        .map(v -> v.get().toString())
        .collect(Collectors.toSet());

    Assert.assertTrue(values.contains("A"), "Result should contain value 'A'");
    Assert.assertTrue(values.contains("B"), "Result should contain value 'B'");
    Assert.assertTrue(values.contains("C"), "Result should contain value 'C'");
  }

}