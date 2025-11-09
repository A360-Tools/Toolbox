package number;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.number.GetDecimalPart;

public class GetDecimalPartTest {

  @Test
  public void testGetDecimalPartPositiveNumber() {
    // Prepare input number with a fractional part
    Double inputNumber = 123.456;

    // Execute the action
    DictionaryValue result = GetDecimalPart.action(inputNumber);

    // Extract return values
    Map<String, Value> returnValue = result.get();
    Double fractionAsNumber = ((NumberValue) returnValue.get("FractionAsNumber")).get();
    String fractionAsString = ((StringValue) returnValue.get("FractionAsString")).get();

    // Verify the fractional part as a number
    Assert.assertEquals(fractionAsNumber, 0.456, "The fractional part as a number is incorrect.");

    // Verify the fractional part as a string
    Assert.assertEquals(fractionAsString, "0.456", "The fractional part as a string is incorrect.");
  }

  @Test
  public void testGetDecimalPartNegativeNumber() {
    // Prepare input number with a fractional part and negative value
    Double inputNumber = -123.456;

    // Execute the action
    DictionaryValue result = GetDecimalPart.action(inputNumber);

    // Extract return values
    Map<String, Value> returnValue = result.get();
    Double fractionAsNumber = ((NumberValue) returnValue.get("FractionAsNumber")).get();
    String fractionAsString = ((StringValue) returnValue.get("FractionAsString")).get();

    // Verify the fractional part as a number
    Assert.assertEquals(fractionAsNumber, -0.456,
        "The fractional part as a number is incorrect for a negative " +
            "number.");

    // Verify the fractional part as a string
    Assert.assertEquals(fractionAsString, "-0.456",
        "The fractional part as a string is incorrect for a negative " +
            "number.");
  }

  @Test
  public void testGetDecimalPartNoFractionalPart() {
    // Prepare input number without a fractional part
    Double inputNumber = 123.0;

    // Execute the action
    DictionaryValue result = GetDecimalPart.action(inputNumber);

    // Extract return values
    Map<String, Value> returnValue = result.get();
    Double fractionAsNumber = ((NumberValue) returnValue.get("FractionAsNumber")).get();
    String fractionAsString = ((StringValue) returnValue.get("FractionAsString")).get();

    // Verify the fractional part as a number
    Assert.assertEquals(fractionAsNumber, 0.0,
        "The fractional part as a number is incorrect for a number without" +
            " a fractional part.");

    // Verify the fractional part as a string
    Assert.assertEquals(fractionAsString, "0.0",
        "The fractional part as a string is incorrect for a number " +
            "without a fractional part.");
  }

  // Additional test cases for edge cases, invalid inputs, and exception handling...
}
