package list;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.list.ReverseList;

public class ReverseListTest {

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
  public void testReverseListMultipleItems() {
    // Prepare the input list with multiple items
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item2"));
    inputList.add(new StringValue("Item3"));

    // Execute the action
    ListValue result = ReverseList.action(inputList);

    // Verify the list is reversed
    Assert.assertEquals(result.get().size(), 3);
    Assert.assertEquals(((Value) result.get().get(0)).get().toString(), "Item3");
    Assert.assertEquals(((Value) result.get().get(1)).get().toString(), "Item2");
    Assert.assertEquals(((Value) result.get().get(2)).get().toString(), "Item1");

    // Ensure the original list remains unchanged
    Assert.assertEquals(inputList.get(0).get().toString(), "Item1");
    Assert.assertEquals(inputList.get(1).get().toString(), "Item2");
    Assert.assertEquals(inputList.get(2).get().toString(), "Item3");
  }

  @Test
  public void testReverseListSingleItem() {
    // Prepare the input list with a single item
    inputList.add(new StringValue("Item1"));

    // Execute the action
    ListValue result = ReverseList.action(inputList);

    // Verify the list remains unchanged as there's only one item
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(((Value) result.get().get(0)).get().toString(), "Item1");
  }

  @Test
  public void testReverseListEmpty() {
    // Execute the action on an empty list
    ListValue result = ReverseList.action(inputList);

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

}

