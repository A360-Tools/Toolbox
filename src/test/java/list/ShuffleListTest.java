package list;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.list.ShuffleList;

public class ShuffleListTest {

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
  public void testShuffleListMultipleItems() {
    // Prepare the input list with multiple items
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item2"));
    inputList.add(new StringValue("Item3"));
    inputList.add(new NumberValue("3"));

    // Execute the action
    ListValue result1 = ShuffleList.action(inputList);
    ListValue result2 = ShuffleList.action(inputList);

    // Convert the original and result lists to sets of strings for comparison
    Set<String> originalSet = inputList.stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.toSet());
    Set<String> resultSet1 = ((List<Value>) result1.get()).stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.toSet());
    Set<String> resultSet2 = ((List<Value>) result2.get()).stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.toSet());

    // Verify the shuffled lists contain the same elements as the original
    Assert.assertEquals(resultSet1, originalSet,
        "The first shuffled list does not contain the same elements as " +
            "the original list.");
    Assert.assertEquals(resultSet2, originalSet,
        "The second shuffled list does not contain the same elements as " +
            "the original list.");

    // Convert the result lists to strings for order comparison
    String resultString1 = ((List<Value>) result1.get()).stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.joining(","));
    String resultString2 = ((List<Value>) result2.get()).stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.joining(","));

    // With only 3 items, there's a 1/6 chance two shuffles could be the same
    // Instead, let's verify that at least one shuffle is different from the original
    String originalString = inputList.stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.joining(","));

    boolean atLeastOneDifferent =
        !resultString1.equals(originalString) || !resultString2.equals(originalString);
    Assert.assertTrue(atLeastOneDifferent,
        "Both shuffles resulted in the same order as the original, which indicates shuffling may not be working.");
  }

  @Test
  public void testShuffleListSingleItem() {
    // Prepare the input list with a single item
    inputList.add(new StringValue("Item1"));

    // Execute the action
    ListValue result = ShuffleList.action(inputList);

    // Verify the shuffled list remains unchanged as there's only one item
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(result.get().get(0).toString(), "Item1");
  }

  @Test
  public void testShuffleListEmpty() {
    // Execute the action on an empty list
    ListValue result = ShuffleList.action(inputList);

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

}
