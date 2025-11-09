package list;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.list.FindCommonItems;

public class FindCommonItemsTest {

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
  public void testFindCommonItemsWithOverlap() {
    // Prepare the input lists
    inputList1.add(new StringValue("Item1"));
    inputList1.add(new StringValue("Item2"));
    inputList1.add(new StringValue("Item3"));

    inputList2.add(new StringValue("Item2"));
    inputList2.add(new StringValue("Item3"));
    inputList2.add(new StringValue("Item4"));

    // Execute the action
    List<Value> result = FindCommonItems.action(inputList1, inputList2, false).get();

    // Process the result to extract string values
    Set<String> resultStrings = result.stream()
        .map(val -> val.get().toString())
        .collect(Collectors.toSet()); // Collect results into a Set<String>

    // Assertions
    Assert.assertEquals(resultStrings.size(), 2);
    Assert.assertTrue(resultStrings.contains("Item2"));
    Assert.assertTrue(resultStrings.contains("Item3"));
  }


  @Test
  public void testFindCommonItemsNoOverlap() {
    inputList1.add(new StringValue("Item1"));
    inputList2.add(new StringValue("Item2"));

    ListValue result = FindCommonItems.action(inputList1, inputList2, false);

    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testFindCommonItemsWithEmptyLists() {
    ListValue result = FindCommonItems.action(inputList1, inputList2, false);

    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testFindCommonItemsWithDuplicates() {
    inputList1.add(new StringValue("Item1"));
    inputList1.add(new StringValue("Item1"));

    inputList2.add(new StringValue("Item1"));
    inputList2.add(new StringValue("Item2"));

    List<Value> result = FindCommonItems.action(inputList1, inputList2, false).get();

    Set<String> resultStrings = result.stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());

    Assert.assertEquals(resultStrings.size(), 1);
    Assert.assertTrue(resultStrings.contains("Item1"));
  }

  @Test
  public void testCaseInsensitiveComparison() {
    // Test case-insensitive matching (default)
    inputList1.add(new StringValue("Apple"));
    inputList1.add(new StringValue("Banana"));
    inputList1.add(new StringValue("Cherry"));

    inputList2.add(new StringValue("apple"));
    inputList2.add(new StringValue("BANANA"));
    inputList2.add(new StringValue("Date"));

    List<Value> result = FindCommonItems.action(inputList1, inputList2, false).get();

    // Should find Apple/apple and Banana/BANANA as common
    Assert.assertEquals(result.size(), 2);
    Set<String> resultStrings = result.stream()
        .map(val -> val.get().toString())
        .collect(Collectors.toSet());
    Assert.assertTrue(resultStrings.contains("Apple"));
    Assert.assertTrue(resultStrings.contains("Banana"));
  }

  @Test
  public void testCaseSensitiveComparison() {
    // Test case-sensitive matching
    inputList1.add(new StringValue("Apple"));
    inputList1.add(new StringValue("Banana"));
    inputList1.add(new StringValue("Cherry"));

    inputList2.add(new StringValue("apple"));
    inputList2.add(new StringValue("Banana"));  // Only this matches
    inputList2.add(new StringValue("Date"));

    List<Value> result = FindCommonItems.action(inputList1, inputList2, true).get();

    // Should find only Banana (exact match)
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).get().toString(), "Banana");
  }

  @Test
  public void testCaseSensitiveNoMatch() {
    // Test case-sensitive with no matches
    inputList1.add(new StringValue("Apple"));
    inputList1.add(new StringValue("Banana"));

    inputList2.add(new StringValue("apple"));
    inputList2.add(new StringValue("banana"));

    List<Value> result = FindCommonItems.action(inputList1, inputList2, true).get();

    // No matches with case-sensitive comparison
    Assert.assertTrue(result.isEmpty());
  }

}
