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
import sumit.devtools.actions.list.RemoveEmptyItemsList;

public class RemoveEmptyItemsListTest {

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
  public void testRemoveEmptyValuesMixedValues() {
    // Prepare the input list with mixed values (non-empty, empty, and null)
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue(""));
    inputList.add(null); // This should be considered as an empty value and removed
    inputList.add(new StringValue("Item2"));
    inputList.add(
        new StringValue(" ")); // This should also be considered as an empty value and removed

    // Execute the action
    ListValue result = RemoveEmptyItemsList.action(inputList);

    // Verify the list size after removing empty values
    Assert.assertEquals(result.get().size(), 2);
    Set<String> resultStrings = ((List<Value>) result.get()).stream()
        .map(value -> value.get().toString())
        .collect(Collectors.toSet());
    // Verify the list contains only the non-empty items
    Assert.assertTrue(resultStrings.contains("Item1"));
    Assert.assertTrue(resultStrings.contains("Item2"));
  }

  @Test
  public void testRemoveEmptyValuesAllEmpty() {
    // Prepare the input list with all empty values
    inputList.add(new StringValue(""));
    inputList.add(new StringValue(" "));
    inputList.add(null);

    // Execute the action
    ListValue result = RemoveEmptyItemsList.action(inputList);

    // Verify the list is empty after removing empty values
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testRemoveEmptyValuesNoEmptyValues() {
    // Prepare the input list with no empty values
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item2"));

    // Execute the action
    ListValue result = RemoveEmptyItemsList.action(inputList);

    // Verify the list remains unchanged
    Assert.assertEquals(result.get().size(), 2);
  }

  @Test
  public void testRemoveEmptyValuesEmptyList() {
    // Execute the action on an empty list
    ListValue result = RemoveEmptyItemsList.action(inputList);

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testRemoveEmptyValuesExceptionHandling() {
    // Force an exception by passing null instead of a valid list
    RemoveEmptyItemsList.action(null);
  }

}
