package list;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.list.RemoveDuplicatesList;

public class RemoveDuplicatesListTest {

  private List<Value> inputList;

  @BeforeMethod
  public void setUp() {
    inputList = new ArrayList<>();
  }

  @AfterMethod
  public void tearDown() {
    inputList = null;
  }

  @Test
  public void testRemoveDuplicatesWithDuplicates() {
    // Prepare the input list with duplicates
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item2"));

    // Execute the action
    ListValue result = RemoveDuplicatesList.action(inputList, false);

    // Verify the list size after removing duplicates
    Assert.assertEquals(result.get().size(), 2);

    // Verify the list contains unique items
    Set<String> resultStrings = ((List<Value>) result.get()).stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());
    Assert.assertTrue(resultStrings.containsAll(Set.of("Item1", "Item2")));
  }

  @Test
  public void testRemoveDuplicatesNoDuplicates() {
    // Prepare the input list without duplicates
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item2"));

    // Execute the action
    ListValue result = RemoveDuplicatesList.action(inputList, false);

    // Verify the list remains unchanged
    Assert.assertEquals(result.get().size(), 2);
  }

  @Test
  public void testRemoveDuplicatesAllDuplicates() {
    // Prepare the input list with all elements as duplicates
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item1"));

    // Execute the action
    ListValue result = RemoveDuplicatesList.action(inputList, false);

    // Verify the list size after removing duplicates
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(((List<Value>) result.get()).get(0).get().toString(), "Item1");
  }

  @Test
  public void testRemoveDuplicatesEmptyList() {
    // Execute the action on an empty list
    ListValue result = RemoveDuplicatesList.action(inputList, false);

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testRemoveDuplicatesExceptionHandling() {
    // Force an exception by passing null instead of a valid list
    RemoveDuplicatesList.action(null, false);
  }

  @Test
  public void testCaseInsensitiveRemoveDuplicates() {
    // Test case-insensitive duplicate removal (default)
    inputList.add(new StringValue("Apple"));
    inputList.add(new StringValue("apple"));
    inputList.add(new StringValue("APPLE"));
    inputList.add(new StringValue("Banana"));

    ListValue result = RemoveDuplicatesList.action(inputList, false);

    // Should keep only first occurrence ("Apple" and "Banana")
    Assert.assertEquals(result.get().size(), 2);
    List<Value> resultList = (List<Value>) result.get();
    Assert.assertEquals(resultList.get(0).get().toString(), "Apple");
    Assert.assertEquals(resultList.get(1).get().toString(), "Banana");
  }

  @Test
  public void testCaseSensitiveRemoveDuplicates() {
    // Test case-sensitive duplicate removal
    inputList.add(new StringValue("Apple"));
    inputList.add(new StringValue("apple"));
    inputList.add(new StringValue("APPLE"));
    inputList.add(new StringValue("Banana"));

    ListValue result = RemoveDuplicatesList.action(inputList, true);

    // Should keep all different case variations
    Assert.assertEquals(result.get().size(), 4);
    List<Value> resultList = (List<Value>) result.get();
    Assert.assertEquals(resultList.get(0).get().toString(), "Apple");
    Assert.assertEquals(resultList.get(1).get().toString(), "apple");
    Assert.assertEquals(resultList.get(2).get().toString(), "APPLE");
    Assert.assertEquals(resultList.get(3).get().toString(), "Banana");
  }

}
