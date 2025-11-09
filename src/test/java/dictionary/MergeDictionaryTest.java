package dictionary;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.LinkedHashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.dictionary.MergeDictionary;

public class MergeDictionaryTest {

  private Map<String, Value> firstDict;
  private Map<String, Value> secondDict;

  @BeforeMethod
  public void setUp() {
    firstDict = new LinkedHashMap<>();
    secondDict = new LinkedHashMap<>();
  }

  @AfterMethod
  public void tearDown() {
    firstDict = null;
    secondDict = null;
  }

  @Test
  public void testMergeWithUniqueKeys() {
    firstDict.put("key1", new StringValue("value1"));
    secondDict.put("key2", new StringValue("value2"));

    DictionaryValue result = MergeDictionary.action(firstDict, secondDict);
    Assert.assertEquals(result.get().size(), 2);
    Assert.assertTrue(result.get().containsKey("key1"));
    Assert.assertTrue(result.get().containsKey("key2"));
  }

  @Test
  public void testMergeWithOverlappingKeys() {
    firstDict.put("key", new StringValue("value1"));
    secondDict.put("key", new StringValue("value2"));

    DictionaryValue result = MergeDictionary.action(firstDict, secondDict);
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(result.get().get("key").toString(), "value2");
  }

  @Test
  public void testMergeWithEmptyFirstDict() {
    secondDict.put("key", new StringValue("value"));

    DictionaryValue result = MergeDictionary.action(firstDict, secondDict);
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertTrue(result.get().containsKey("key"));
  }

  @Test
  public void testMergeWithEmptySecondDict() {
    firstDict.put("key", new StringValue("value"));

    DictionaryValue result = MergeDictionary.action(firstDict, secondDict);
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertTrue(result.get().containsKey("key"));
  }

  @Test
  public void testMergeWithBothEmptyDicts() {
    DictionaryValue result = MergeDictionary.action(firstDict, secondDict);
    Assert.assertTrue(result.get().isEmpty());
  }

}

