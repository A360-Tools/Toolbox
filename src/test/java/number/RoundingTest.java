package number;

import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sumit.devtools.actions.number.RoundNumber;

/**
 * @author Sumit Kumar
 */
public class RoundingTest {

  // Tests for "up" (CEILING) rounding
  @Test
  public void testRoundUpPositive_ZeroDecimalPlaces() {
    NumberValue result = RoundNumber.action(123.6, 0.0, "up");
    Assert.assertEquals(result.get().doubleValue(), 124.0);
  }

  @Test
  public void testRoundUpPositive_TwoDecimalPlaces_FractionUp() {
    NumberValue result = RoundNumber.action(123.456, 2.0, "up");
    Assert.assertEquals(result.get().doubleValue(), 123.46);
  }

  @Test
  public void testRoundUpPositive_TwoDecimalPlaces_FractionExact() {
    NumberValue result = RoundNumber.action(123.450, 2.0, "up");
    Assert.assertEquals(result.get().doubleValue(), 123.45);
  }

  @Test
  public void testRoundUpNegative_TwoDecimalPlaces() {
    NumberValue result = RoundNumber.action(-123.456, 2.0, "up");
    Assert.assertEquals(result.get().doubleValue(), -123.45);
  }

  @Test
  public void testRoundUpNegative_TwoDecimalPlaces_FractionZero() {
    NumberValue result = RoundNumber.action(-123.450, 2.0, "up");
    Assert.assertEquals(result.get().doubleValue(), -123.45);
  }


  // Tests for "down" (FLOOR) rounding
  @Test
  public void testRoundDownPositive_ZeroDecimalPlaces() {
    NumberValue result = RoundNumber.action(123.4, 0.0, "down");
    Assert.assertEquals(result.get().doubleValue(), 123.0);
  }

  @Test
  public void testRoundDownPositive_TwoDecimalPlaces_FractionDown() {
    NumberValue result = RoundNumber.action(123.456, 2.0, "down");
    Assert.assertEquals(result.get().doubleValue(), 123.45);
  }

  @Test
  public void testRoundDownPositive_TwoDecimalPlaces_FractionExact() {
    NumberValue result = RoundNumber.action(123.459, 2.0, "down");
    Assert.assertEquals(result.get().doubleValue(), 123.45);
  }

  @Test
  public void testRoundDownNegative_TwoDecimalPlaces() {
    NumberValue result = RoundNumber.action(-123.451, 2.0, "down");
    Assert.assertEquals(result.get().doubleValue(), -123.46);
  }

  @Test
  public void testRoundDownNegative_TwoDecimalPlaces_FractionZero() {
    NumberValue result = RoundNumber.action(-123.450, 2.0, "down");
    Assert.assertEquals(result.get().doubleValue(), -123.45);
  }

  // Tests for "normal" (HALF_UP which behaves as round half away from zero here)
  @DataProvider(name = "normalRoundingData")
  public Object[][] normalRoundingData() {
    return new Object[][]{
        // input, decimalPlaces, expected (negative halves round away from zero)
        {123.7, 0.0, 124.0},
        {123.5, 0.0, 124.0},
        {123.4, 0.0, 123.0},
        {123.456, 2.0, 123.46},
        {123.455, 2.0, 123.46},
        {123.454, 2.0, 123.45},
        {-123.7, 0.0, -124.0},
        {-123.5, 0.0, -124.0},
        {-123.4, 0.0, -123.0},
        {-123.456, 2.0, -123.46},
        {-123.455, 2.0, -123.46},
        {-123.454, 2.0, -123.45},
        {2.5, 0.0, 3.0},
        {-2.5, 0.0, -3.0},
        {0.0, 2.0, 0.0},
        {123.0, 2.0, 123.00},
        {1.234567, 4.0, 1.2346}
    };
  }

  @Test(dataProvider = "normalRoundingData")
  public void testRoundNormal(Double input, Double decimalPlaces, Double expected) {
    NumberValue result = RoundNumber.action(input, decimalPlaces, "normal");
    Assert.assertEquals(result.get().doubleValue(), expected,
        "Failed for input: " + input + ", decimalPlaces: " + decimalPlaces + ", rounding: normal");
  }

  @Test
  public void testRoundNormal_NegativeMidPointPrecision() {
    // Specifically for -1.235 to 2 places -> -1.24 (away from zero)
    NumberValue result = RoundNumber.action(-1.235, 2.0, "normal");
    Assert.assertEquals(result.get().doubleValue(), -1.24); // Corrected
  }


  // General tests
  @Test
  public void testRoundLargeNumber_Up() {
    NumberValue result = RoundNumber.action(1.0E15 + 0.6, 0.0, "up");
    Assert.assertEquals(result.get().doubleValue(), 1.0E15 + 1.0);
  }

  @Test
  public void testRoundLargeNumber_Normal() {
    NumberValue result = RoundNumber.action(1.0E15 + 0.4, 0.0, "normal");
    Assert.assertEquals(result.get().doubleValue(), 1.0E15);

    result = RoundNumber.action(1.0E15 + 0.5, 0.0, "normal");
    Assert.assertEquals(result.get().doubleValue(), 1.0E15 + 1.0);
  }


  @Test
  public void testRoundSmallNumber_Up() {
    NumberValue result = RoundNumber.action(0.0000123456, 8.0, "up");
    Assert.assertEquals(result.get().doubleValue(), 0.00001235);
  }

  @Test
  public void testRoundSmallNumber_Normal() {
    NumberValue result = RoundNumber.action(0.0000123456, 8.0, "normal");
    Assert.assertEquals(result.get().doubleValue(), 0.00001235);

    result = RoundNumber.action(0.0000123446, 8.0, "normal");
    Assert.assertEquals(result.get().doubleValue(), 0.00001234);
  }


  // Exception handling tests
  @DataProvider(name = "invalidInputsProvider")
  public Object[][] invalidInputsProvider() {
    return new Object[][]{
        {null, 2.0, "up", "Input number cannot be null."},
        {123.45, null, "up", "Decimal places cannot be null."},
        {123.45, 2.0, null, "Rounding type cannot be null."},
        {123.45, -1.0, "up", "Decimal places must be a non-negative integer. Received: -1"},
        // Will be
        // caught by explicit check or Precision.round
        // {123.45, 1.5, "up", "Decimal places must be an integer."}, // This case is tricky for unit tests
        // if @NumberInteger is not enforced
        // and intValue() truncates. Precision.round(123.45, 1, ...) would be valid.
        // Removing for now as it tests SDK annotation enforcement.
        {123.45, 2.0, "invalid_type", "Invalid rounding type specified: invalid_type"}
    };
  }

  @Test(dataProvider = "invalidInputsProvider", expectedExceptions = BotCommandException.class)
  public void testExceptionHandlingForInvalidInputs(Double inputNumber, Double decimalPlaces,
      String roundingType,
      String expectedMessagePart) {
    try {
      RoundNumber.action(inputNumber, decimalPlaces, roundingType);
    } catch (BotCommandException e) {
      Assert.assertTrue(e.getMessage().contains(expectedMessagePart),
          "Exception message check failed. Expected to contain: '" + expectedMessagePart
              + "'. Actual: '" + e.getMessage() + "'");
      throw e; // Re-throw to satisfy expectedExceptions
    } catch (Exception e) {
      Assert.fail("Unexpected exception type thrown: " + e.getClass().getName() + " with message: "
          + e.getMessage());
    }
    // If no exception is thrown, TestNG will fail because BotCommandException was expected.
  }

}