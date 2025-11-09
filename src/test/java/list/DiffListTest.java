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
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.list.DiffList;

public class DiffListTest {

  private List<Value> inputList1;
  private List<Value> inputList2;

  @BeforeMethod
  public void setUp() {
    inputList1 = new ArrayList<>();
    inputList2 = new ArrayList<>();
  }

  @AfterMethod
  public void tearDown() {
    inputList1 = null;
    inputList2 = null;
  }

  @Test
  public void testListDifferenceWithUniqueValues() {
    // Prepare data
    inputList1.add(new StringValue("Item1"));
    inputList1.add(new StringValue("Item2"));
    inputList2.add(new StringValue("Item3"));
    inputList2.add(new StringValue("Item4"));

    // Execute action
    List<Value> result = DiffList.action(inputList1, inputList2, false).get();

    // Initialize a set to store the string representations of the result values
    List<String> resultStrings = new ArrayList<>();

    // Iterate over the result list and add the string representations to the set
    for (Value value : result) {
      resultStrings.add(value.get().toString());
    }

    // Assertions
    Assert.assertEquals(resultStrings.size(), 2);
    Assert.assertTrue(resultStrings.contains("Item1"));
    Assert.assertTrue(resultStrings.contains("Item2"));
  }


  @Test
  public void testListDifferenceWithOverlappingValues() {
    inputList1.add(new StringValue("Item1"));
    inputList1.add(new StringValue("Item2"));
    inputList2.add(new StringValue("Item2"));
    inputList2.add(new StringValue("Item3"));

    // Execute action
    List<Value> result = DiffList.action(inputList1, inputList2, false).get();

    // Initialize a set to store the string representations of the result values
    List<String> resultStrings = new ArrayList<>();

    // Iterate over the result list and add the string representations to the set
    for (Value value : result) {
      resultStrings.add(value.get().toString());
    }
    Assert.assertEquals(resultStrings.size(), 1);
    Assert.assertTrue(resultStrings.contains("Item1"));
  }

  @Test
  public void testListDifferenceWithEmptyFirstList() {
    inputList2.add(new StringValue("Item1"));

    ListValue result = DiffList.action(inputList1, inputList2, false);
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testListDifferenceWithEmptySecondList() {
    inputList1.add(new StringValue("Item1"));
    inputList1.add(new StringValue("Item2"));

    // Execute action
    List<Value> result = DiffList.action(inputList1, inputList2, false).get();

    // Initialize a set to store the string representations of the result values
    List<String> resultStrings = new ArrayList<>();

    // Iterate over the result list and add the string representations to the set
    for (Value value : result) {
      resultStrings.add(value.get().toString());
    }
    Assert.assertEquals(resultStrings.size(), 2);
    Assert.assertTrue(resultStrings.contains("Item1"));
    Assert.assertTrue(resultStrings.contains("Item2"));
  }

  @Test
  public void testListDifferenceWithDuplicateValues() {
    inputList1.add(new StringValue("Item1"));
    inputList1.add(new StringValue("Item1"));
    inputList2.add(new StringValue("Item2"));

    // Execute action
    List<Value> result = DiffList.action(inputList1, inputList2, false).get();

    // Initialize a set to store the string representations of the result values
    List<String> resultStrings = new ArrayList<>();

    // Iterate over the result list and add the string representations to the set
    for (Value value : result) {
      resultStrings.add(value.get().toString());
    }
    Assert.assertEquals(resultStrings.size(), 2);
    Assert.assertTrue(resultStrings
        .stream()
        .allMatch(item -> item.equals("Item1")));
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testExceptionHandling() {
    // Simulate an exception scenario, for example, by passing a null inputList1
    DiffList.action(null, inputList2, false);
  }

  @Test
  public void testCaseInsensitiveDifference() {
    // Test case-insensitive difference (default)
    inputList1.add(new StringValue("Apple"));
    inputList1.add(new StringValue("Banana"));
    inputList1.add(new StringValue("Cherry"));

    inputList2.add(new StringValue("apple"));  // Should match Apple
    inputList2.add(new StringValue("CHERRY")); // Should match Cherry

    List<Value> result = DiffList.action(inputList1, inputList2, false).get();

    // Should return only Banana (Apple and Cherry matched case-insensitively)
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).get().toString(), "Banana");
  }

  @Test
  public void testCaseSensitiveDifference() {
    // Test case-sensitive difference
    inputList1.add(new StringValue("Apple"));
    inputList1.add(new StringValue("Banana"));
    inputList1.add(new StringValue("Cherry"));

    inputList2.add(new StringValue("apple"));  // Should NOT match Apple
    inputList2.add(new StringValue("Banana")); // Should match Banana

    List<Value> result = DiffList.action(inputList1, inputList2, true).get();

    // Should return Apple and Cherry (only Banana matched exactly)
    Assert.assertEquals(result.size(), 2);
    List<String> resultStrings = new ArrayList<>();
    for (Value value : result) {
      resultStrings.add(value.get().toString());
    }
    Assert.assertTrue(resultStrings.contains("Apple"));
    Assert.assertTrue(resultStrings.contains("Cherry"));
  }

}
