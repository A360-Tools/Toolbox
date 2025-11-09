package number;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.number.GetMinNumber;

public class MinNumberTest {

  @Test
  public void testMinNumberWithMixedValues() {
    List<Value> numbers = Arrays.asList(new NumberValue(-10), new NumberValue(2.5),
        new NumberValue(100),
        new NumberValue(-50));
    NumberValue result = GetMinNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), -50.0);
  }

  @Test
  public void testMinNumberWithAllPositive() {
    List<Value> numbers = Arrays.asList(new NumberValue(1), new NumberValue(2), new NumberValue(3));
    NumberValue result = GetMinNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), 1.0);
  }

  @Test
  public void testMinNumberWithAllNegative() {
    List<Value> numbers = Arrays.asList(new NumberValue(-1), new NumberValue(-2),
        new NumberValue(-3));
    NumberValue result = GetMinNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), -3.0);
  }

  @Test
  public void testMinNumberWithSingleElement() {
    List<Value> numbers = List.of(new NumberValue(42));
    NumberValue result = GetMinNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), 42.0);
  }

  @Test
  public void testMinNumberWithLargeNumbers() {
    List<Value> numbers = Arrays.asList(new NumberValue(Long.MAX_VALUE),
        new NumberValue(Long.MAX_VALUE - 1));
    NumberValue result = GetMinNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), (double) (Long.MAX_VALUE - 1));
  }

  @Test
  public void testMinNumberWithEdgeCases() {
    List<Value> numbers = Arrays.asList(new NumberValue(Double.MAX_VALUE),
        new NumberValue(-Double.MAX_VALUE));
    NumberValue result = GetMinNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), -Double.MAX_VALUE);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testMinNumberWithEmptyList() {
    List<Value> numbers = List.of();
    GetMinNumber.action(numbers);
  }

}
