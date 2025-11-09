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
import sumit.devtools.actions.number.GetMaxNumber;

public class MaxNumberTest {

  @Test
  public void testMaxNumberWithMixedValues() {
    List<Value> numbers = Arrays.asList(new NumberValue(-10), new NumberValue(2.5),
        new NumberValue(100),
        new NumberValue(-50));
    NumberValue result = GetMaxNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), 100.0);
  }

  @Test
  public void testMaxNumberWithAllPositive() {
    List<Value> numbers = Arrays.asList(new NumberValue(1), new NumberValue(2), new NumberValue(3));
    NumberValue result = GetMaxNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), 3.0);
  }

  @Test
  public void testMaxNumberWithAllNegative() {
    List<Value> numbers = Arrays.asList(new NumberValue(-1), new NumberValue(-2),
        new NumberValue(-3));
    NumberValue result = GetMaxNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), -1.0);
  }

  @Test
  public void testMaxNumberWithSingleElement() {
    List<Value> numbers = List.of(new NumberValue(42));
    NumberValue result = GetMaxNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), 42.0);
  }

  @Test
  public void testMaxNumberWithLargeNumbers() {
    List<Value> numbers = Arrays.asList(new NumberValue(Long.MAX_VALUE),
        new NumberValue(Long.MAX_VALUE - 1));
    System.out.println(Long.MAX_VALUE);
    System.out.println(Long.MAX_VALUE - 1);
    NumberValue result = GetMaxNumber.action(numbers);
    System.out.println(result.getAsDouble());
    Assert.assertEquals(result.getAsDouble(), (double) Long.MAX_VALUE);
  }

  @Test
  public void testMaxNumberWithPrecision() {
    List<Value> numbers = Arrays.asList(new NumberValue(1.123456789), new NumberValue(1.123456788));
    NumberValue result = GetMaxNumber.action(numbers);
    Assert.assertEquals(result.getAsDouble(), 1.123456789);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testMaxNumberWithEmptyList() {
    List<Value> numbers = List.of();
    GetMaxNumber.action(numbers);
  }

}
