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
import sumit.devtools.actions.list.AddItem;

public class AddItemListTest {

  private List<Value> inputList;
  private Value itemToAdd;

  @BeforeMethod
  public void setUp() {
    inputList = new ArrayList<>();
    itemToAdd = new StringValue("NewItem");
  }

  @AfterMethod
  public void tearDown() {
    inputList = null;
    itemToAdd = null;
  }

  @Test
  public void testAddItemToEnd() {
    inputList.add(new StringValue("Item1"));
    ListValue result = AddItem.action(inputList, itemToAdd, "END", null);
    Assert.assertEquals(result.get().size(), 2);
    Assert.assertEquals(result.get().get(0).toString(), "Item1");
    Assert.assertEquals(result.get().get(1).toString(), "NewItem");
  }

  @Test
  public void testAddItemAtIndex() {
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item2"));
    ListValue result = AddItem.action(inputList, itemToAdd, "INDEX", 1.0);
    Assert.assertEquals(result.get().size(), 3);
    Assert.assertEquals(result.get().get(0).toString(), "Item1");
    Assert.assertEquals(result.get().get(1).toString(), "NewItem");
    Assert.assertEquals(result.get().get(2).toString(), "Item2");

  }

  @Test
  public void testAddItemToEmptyList() {
    ListValue result = AddItem.action(inputList, itemToAdd, "END", null);
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(result.get().get(0).toString(), "NewItem");
  }

  @Test
  public void testAddItemAtStart() {
    inputList.add(new StringValue("Item1"));
    ListValue result = AddItem.action(inputList, itemToAdd, "INDEX", 0.0);
    Assert.assertEquals(result.get().size(), 2);
    Assert.assertEquals(result.get().get(0).toString(), "NewItem");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testAddItemAtInvalidIndex() {
    inputList.add(new StringValue("Item1"));
    AddItem.action(inputList, itemToAdd, "INDEX", 2.0); // Index out of bounds
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testExceptionHandling() {
    // Simulate an exception scenario, for example, by passing a null inputList
    AddItem.action(null, itemToAdd, "END", null);
  }

}
