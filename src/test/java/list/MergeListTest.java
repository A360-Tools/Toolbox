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
import sumit.devtools.actions.list.MergeList;

public class MergeListTest {

  private List<Value> firstList;
  private List<Value> secondList;

  @BeforeMethod
  public void setUp() {
    firstList = new ArrayList<>();
    secondList = new ArrayList<>();
  }

  @AfterMethod
  public void tearDown() {
    firstList = null;
    secondList = null;
  }

  @Test
  public void testMergeListWithUniqueItems() {
    // Prepare the input lists with unique items
    firstList.add(new StringValue("Item1"));
    firstList.add(new StringValue("Item2"));
    secondList.add(new StringValue("Item3"));
    secondList.add(new StringValue("Item4"));

    // Execute the action
    ListValue result = MergeList.action(firstList, secondList);

    // Verify the merged list size
    Assert.assertEquals(result.get().size(), 4);

    // Verify the merged list contains all items
    Set<String> resultStrings = ((List<Value>) result.get()).stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());
    Assert.assertTrue(resultStrings.containsAll(Set.of("Item1", "Item2", "Item3", "Item4")));
  }

  @Test
  public void testMergeListWithOverlappingItems() {
    // Prepare the input lists with overlapping items
    firstList.add(new StringValue("Item1"));
    firstList.add(new StringValue("Item2"));
    secondList.add(new StringValue("Item2"));
    secondList.add(new StringValue("Item3"));

    // Execute the action
    ListValue result = MergeList.action(firstList, secondList);

    // Verify the merged list size (expecting duplicates as it's a simple merge)
    Assert.assertEquals(result.get().size(), 4);
  }

  @Test
  public void testMergeListWithEmptyFirstList() {
    // Prepare the second list
    secondList.add(new StringValue("Item1"));
    secondList.add(new StringValue("Item2"));

    // Execute the action
    ListValue result = MergeList.action(firstList, secondList);

    // Verify the merged list is equal to the second list
    Assert.assertEquals(result.get().size(), 2);
  }

  @Test
  public void testMergeListWithEmptySecondList() {
    // Prepare the first list
    firstList.add(new StringValue("Item1"));
    firstList.add(new StringValue("Item2"));

    // Execute the action
    ListValue result = MergeList.action(firstList, secondList);

    // Verify the merged list is equal to the first list
    Assert.assertEquals(result.get().size(), 2);
  }

  @Test
  public void testMergeListWithBothEmptyLists() {
    // Execute the action
    ListValue result = MergeList.action(firstList, secondList);

    // Verify the merged list is empty
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testMergeListExceptionHandling() {
    // Force an exception by passing null instead of a valid list
    MergeList.action(null, secondList);
  }

}
