package number;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.number.GetIntegerPart;

public class GetIntegerPartTest {

  @Test
  public void testPositiveNumberWithFraction() {
    Double inputNumber = 123.456;
    DictionaryValue result = GetIntegerPart.action(inputNumber);

    validateIntegralPart(result, 123, "123");
  }

  // Helper method to validate the integral part in both formats
  private void validateIntegralPart(DictionaryValue result, int expectedNumber,
      String expectedString) {
    NumberValue numberValue = (NumberValue) result.get().get("IntegralAsNumber");
    StringValue stringValue = (StringValue) result.get().get("IntegralAsString");

    Assert.assertEquals(numberValue.get().intValue(), expectedNumber,
        "Integral part as Number is incorrect.");
    Assert.assertEquals(stringValue.get(), expectedString, "Integral part as String is incorrect.");
  }

  @Test
  public void testNegativeNumberWithFraction() {
    Double inputNumber = -123.456;
    DictionaryValue result = GetIntegerPart.action(inputNumber);

    validateIntegralPart(result, -123, "-123");
  }

  @Test
  public void testWholeNumber() {
    Double inputNumber = 123.0;
    DictionaryValue result = GetIntegerPart.action(inputNumber);

    validateIntegralPart(result, 123, "123");
  }

  @Test
  public void testZero() {
    Double inputNumber = 0.0;
    DictionaryValue result = GetIntegerPart.action(inputNumber);

    validateIntegralPart(result, 0, "0");
  }

  @Test
  public void testMaxValue() {
    Double inputNumber = Double.MAX_VALUE;
    DictionaryValue result = GetIntegerPart.action(inputNumber);

    validateIntegralPart(result, (int) Double.MAX_VALUE, String.valueOf((int) Double.MAX_VALUE));
  }

  @Test
  public void testMinValue() {
    Double inputNumber = Double.MIN_VALUE;
    DictionaryValue result = GetIntegerPart.action(inputNumber);

    validateIntegralPart(result, (int) Double.MIN_VALUE, String.valueOf((int) Double.MIN_VALUE));
  }

}
