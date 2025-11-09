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
import sumit.devtools.actions.list.ChunkList;

public class ChunkListTest {

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
  public void testChunkListWithValidSize() {
    // Prepare data
    for (int i = 1; i <= 5; i++) {
      inputList.add(new StringValue("Item" + i));
    }

    // Chunk size 2
    ListValue result = ChunkList.action(inputList, 2.0);
    List<Value> listValuesfirstList = ((ListValue) result.get().get(0)).get();
    List<Value> listValuesSecondList = ((ListValue) result.get().get(1)).get();
    List<Value> listValuesThirdList = ((ListValue) result.get().get(2)).get();
    Assert.assertEquals(result.get().size(), 3); // Should create 3 chunks
    Assert.assertEquals(listValuesfirstList.size(), 2); // First chunk should have 2 items
    Assert.assertEquals(listValuesSecondList.size(), 2); // First chunk should have 2 items
    Assert.assertEquals(listValuesThirdList.size(), 1); // Last chunk should have 1 item
  }

  @Test
  public void testChunkListWithSizeOne() {
    // Prepare data
    inputList.add(new StringValue("Item1"));
    inputList.add(new StringValue("Item2"));

    ListValue result = ChunkList.action(inputList, 1.0);
    Assert.assertEquals(result.get().size(), 2); // Should create 2 chunks
  }

  @Test
  public void testChunkListWithSizeLargerThanList() {
    // Prepare data
    inputList.add(new StringValue("Item1"));

    ListValue result = ChunkList.action(inputList, 5.0);
    Assert.assertEquals(result.get().size(), 1); // Should create 1 chunk
    Assert.assertEquals(((ListValue) result.get().get(0)).get().size(),
        1); // The only chunk should have 1 item
  }

  @Test
  public void testChunkEmptyList() {
    ListValue result = ChunkList.action(inputList, 2.0);
    Assert.assertTrue(result.get().isEmpty()); // Should return an empty list of chunks
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testChunkWithInvalidSizeZero() {
    inputList.add(new StringValue("Item1"));
    ChunkList.action(inputList, 0.0); // Expecting an exception due to invalid chunk size
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testChunkWithInvalidSizeNegative() {
    inputList.add(new StringValue("Item1"));
    ChunkList.action(inputList, -1.0); // Expecting an exception due to invalid chunk size
  }

}
