package list;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.list.StripList;

/**
 * @author Sumit Kumar
 */
public class StripListTest {

  private List<StringValue> inputList;

  @BeforeMethod
  public void setUp() {
    inputList = new ArrayList<>();
  }

  @AfterMethod
  public void tearDown() {
    inputList = null;
  }

  @Test
  public void testStripListMultipleItems() {
    // Prepare the input list with multiple items containing whitespace
    inputList.add(new StringValue("  Item1  "));
    inputList.add(new StringValue("\tItem2\t"));
    inputList.add(new StringValue("\nItem3\n"));

    // Execute the action
    ListValue result = StripList.action(inputList);

    // Verify the list has whitespace stripped
    Assert.assertEquals(result.get().size(), 3);
    Assert.assertEquals(((Value) result.get().get(0)).get().toString(), "Item1");
    Assert.assertEquals(((Value) result.get().get(1)).get().toString(), "Item2");
    Assert.assertEquals(((Value) result.get().get(2)).get().toString(), "Item3");

    // Ensure the original list remains unchanged
    Assert.assertEquals(inputList.get(0).get(), "  Item1  ");
    Assert.assertEquals(inputList.get(1).get(), "\tItem2\t");
    Assert.assertEquals(inputList.get(2).get(), "\nItem3\n");
  }

  @Test
  public void testStripListSingleItem() {
    // Prepare the input list with a single item with whitespace
    inputList.add(new StringValue("  SingleItem  "));

    // Execute the action
    ListValue result = StripList.action(inputList);

    // Verify the list has whitespace stripped
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(((Value) result.get().get(0)).get().toString(), "SingleItem");
  }

  @Test
  public void testStripListEmpty() {
    // Execute the action on an empty list
    ListValue result = StripList.action(inputList);

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testStripListOnlyWhitespace() {
    // Prepare the input list with items containing only whitespace
    inputList.add(new StringValue("   "));
    inputList.add(new StringValue("\t\t"));
    inputList.add(new StringValue("\n\n"));

    // Execute the action
    ListValue result = StripList.action(inputList);

    // Verify all items are now empty strings
    Assert.assertEquals(result.get().size(), 3);
    Assert.assertEquals(((Value) result.get().get(0)).get().toString(), "");
    Assert.assertEquals(((Value) result.get().get(1)).get().toString(), "");
    Assert.assertEquals(((Value) result.get().get(2)).get().toString(), "");
  }

  @Test
  public void testStripListPreservesInnerWhitespace() {
    // Prepare the input list with items containing inner whitespace
    inputList.add(new StringValue("  Hello World  "));
    inputList.add(new StringValue("\tMultiple   Spaces\t"));

    // Execute the action
    ListValue result = StripList.action(inputList);

    // Verify outer whitespace is stripped but inner whitespace is preserved
    Assert.assertEquals(result.get().size(), 2);
    Assert.assertEquals(((Value) result.get().get(0)).get().toString(), "Hello World");
    Assert.assertEquals(((Value) result.get().get(1)).get().toString(), "Multiple   Spaces");
  }

  @Test
  public void testStripListMixedContent() {
    // Prepare the input list with mixed content
    inputList.add(new StringValue("NoWhitespace"));
    inputList.add(new StringValue("  WithWhitespace  "));
    inputList.add(new StringValue(""));

    // Execute the action
    ListValue result = StripList.action(inputList);

    // Verify appropriate stripping
    Assert.assertEquals(result.get().size(), 3);
    Assert.assertEquals(((Value) result.get().get(0)).get().toString(), "NoWhitespace");
    Assert.assertEquals(((Value) result.get().get(1)).get().toString(), "WithWhitespace");
    Assert.assertEquals(((Value) result.get().get(2)).get().toString(), "");
  }

}