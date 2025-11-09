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
import java.util.stream.IntStream;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.dictionary.FindUncommonKeys;

public class FindUncommonKeysTest {

  @Test
  public void testUncommonKeysInBothDictionaries() {
    // Setup dictionaries with a common key and unique keys
    Map<String, Value> firstDict = new HashMap<>();
    firstDict.put("key1",
        new StringValue("value1")); // Assuming StringValue is a valid implementation of Value
    firstDict.put("commonKey", new StringValue("commonValue"));

    Map<String, Value> secondDict = new HashMap<>();
    secondDict.put("key2", new StringValue("value2"));
    secondDict.put("commonKey", new StringValue("commonValue"));

    // Execute action and assume it returns ListValue of uncommon keys
    ListValue<?> result = FindUncommonKeys.action(firstDict, secondDict);

    // Convert ListValue to Stream and then to a Set<String> of keys
    Set<String> resultKeys = IntStream.range(0, result.get().size())
        .mapToObj(result::get) // Stream<Value>
        .map(value -> value.get().toString()) // Stream<String>
        .collect(Collectors.toSet());

    // Assert both unique keys ('key1' and 'key2') are present in the result
    Assert.assertTrue(resultKeys.containsAll(Set.of("key1", "key2")),
        "Both unique keys should be present in " +
            "the result.");

    // Assert the common key ('commonKey') is not present in the result
    Assert.assertFalse(resultKeys.contains("commonKey"),
        "Common key should not be present in the result.");
  }

  @Test
  public void testNoUncommonKeys() {
    Map<String, Value> firstDict = new HashMap<>();
    firstDict.put("commonKey", new StringValue("commonValue"));

    Map<String, Value> secondDict = new HashMap<>();
    secondDict.put("commonKey", new StringValue("commonValue"));

    ListValue result = FindUncommonKeys.action(firstDict, secondDict);
    Assert.assertTrue(result.get().isEmpty(),
        "No uncommon keys should be present when all keys are common.");
  }

  @Test
  public void testEmptyDictionaries() {
    Map<String, Value> firstDict = new HashMap<>();
    Map<String, Value> secondDict = new HashMap<>();

    ListValue result = FindUncommonKeys.action(firstDict, secondDict);
    Assert.assertTrue(result.get().isEmpty(),
        "Result should be empty when both dictionaries are empty.");
  }

}
