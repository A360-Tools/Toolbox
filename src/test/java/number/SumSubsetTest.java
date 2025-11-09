package number;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.number.CalculateSumSubset;

/**
 * @author Claude (Updated by AI assistant)
 */
public class SumSubsetTest {

  private List<Value> inputList;

  @BeforeMethod
  public void setUp() {
    inputList = new ArrayList<>();
  }

  @Test
  public void testExactSum() {
    inputList.add(new NumberValue(1.0));
    inputList.add(new NumberValue(2.0));
    inputList.add(new NumberValue(3.0));
    inputList.add(new NumberValue(4.0));
    inputList.add(new NumberValue(5.0));

    double targetSum = 9.0;
    ListValue result = CalculateSumSubset.action(inputList, targetSum, 0.0);
    Set<Double> resultValues = toDoubleSet(result);

    // Expected first found subset is [1.0, 3.0, 5.0]
    Assert.assertEquals(resultValues.size(), 3,
        "ExactSum: Subset size should be 3 for the first found subset [1," +
            "3,5].");
    Assert.assertTrue(resultValues.contains(1.0),
        "ExactSum: Subset should contain 1.0 for the first found subset" +
            " [1,3,5].");
    Assert.assertTrue(resultValues.contains(3.0),
        "ExactSum: Subset should contain 3.0 for the first found subset" +
            " [1,3,5].");
    Assert.assertTrue(resultValues.contains(5.0),
        "ExactSum: Subset should contain 5.0 for the first found subset" +
            " [1,3,5].");

    double actualSum = calculateSum(resultValues);
    Assert.assertEquals(actualSum, targetSum, 0.00001,
        "ExactSum: Sum of elements (" + actualSum + ") should " +
            "match target sum (" + targetSum + ").");
  }

  private Set<Double> toDoubleSet(ListValue listValue) {
    if (listValue == null || listValue.get() == null) {
      return new HashSet<>();
    }
    return (Set<Double>) listValue.get().stream()
        .map(v -> ((NumberValue) v).get())
        .collect(Collectors.toSet());
  }

  private double calculateSum(Set<Double> values) {
    return values.stream().mapToDouble(Double::doubleValue).sum();
  }

  @Test
  public void testApproximateSum() {
    inputList.add(new NumberValue(1.1));
    inputList.add(new NumberValue(2.2));
    inputList.add(new NumberValue(3.3));
    inputList.add(new NumberValue(4.4));

    double targetSum = 6.5;
    double threshold = 0.15;
    // Expected first found subset: [1.1, 2.2, 3.3], sum = 6.6. |6.6 - 6.5| = 0.1 <= 0.15
    ListValue result = CalculateSumSubset.action(inputList, targetSum, threshold);
    Set<Double> resultValues = toDoubleSet(result);

    Assert.assertFalse(resultValues.isEmpty(),
        "ApproximateSum: An approximate subset should be found, list " +
            "should not be empty.");
    Assert.assertEquals(resultValues.size(), 3,
        "ApproximateSum: Subset size should be 3 for the first found " +
            "subset [1.1, 2.2, 3.3].");
    Assert.assertTrue(resultValues.contains(1.1), "ApproximateSum: Subset should contain 1.1.");
    Assert.assertTrue(resultValues.contains(2.2), "ApproximateSum: Subset should contain 2.2.");
    Assert.assertTrue(resultValues.contains(3.3), "ApproximateSum: Subset should contain 3.3.");

    double actualSum = calculateSum(resultValues);
    Assert.assertTrue(Math.abs(actualSum - targetSum) <= threshold + 0.00001,
        // add epsilon for double comparison
        "ApproximateSum: Sum (" + actualSum + ") is not within threshold (" + threshold
            + ") of target (" + targetSum + "). Difference: " + Math.abs(actualSum - targetSum));
  }

  @Test
  public void testNoMatchingSubset() {
    inputList.add(new NumberValue(10.0));
    inputList.add(new NumberValue(20.0));
    inputList.add(new NumberValue(30.0));

    ListValue result = CalculateSumSubset.action(inputList, 15.0, 0.0);
    Assert.assertTrue(result.get().isEmpty(),
        "NoMatchingSubset: Result list should be empty when no subset " +
            "matches the criteria.");
  }

  @Test
  public void testWithNegativeNumbers() {
    inputList.add(new NumberValue(5.0));
    inputList.add(new NumberValue(-2.0));
    inputList.add(new NumberValue(3.0));
    inputList.add(new NumberValue(-1.0));

    double targetSum = 2.0; // Example: first found should be [5, -2, -1]
    ListValue result = CalculateSumSubset.action(inputList, targetSum, 0.0);
    Set<Double> resultValues = toDoubleSet(result);

    Assert.assertFalse(resultValues.isEmpty(), "WithNegativeNumbers: A subset should be found.");
    double actualSum = calculateSum(resultValues);
    Assert.assertEquals(actualSum, targetSum, 0.00001,
        "WithNegativeNumbers: Sum of elements (" + actualSum + ") " +
            "should match target sum (" + targetSum + ").");

    Assert.assertEquals(resultValues.size(), 3,
        "WithNegativeNumbers: Expected subset size 3 for [5, -2, -1].");
    Assert.assertTrue(resultValues.contains(5.0),
        "WithNegativeNumbers: Subset should contain 5.0.");
    Assert.assertTrue(resultValues.contains(-2.0),
        "WithNegativeNumbers: Subset should contain -2.0.");
    Assert.assertTrue(resultValues.contains(-1.0),
        "WithNegativeNumbers: Subset should contain -1.0.");
  }

  @Test
  public void testWithEmptyList() {
    ListValue result = CalculateSumSubset.action(inputList, 5.0, 0.0);
    Assert.assertTrue(result.get().isEmpty(),
        "WithEmptyList: Result list should be empty for an empty input list" +
            ".");
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp =
          "Error occurred while finding subset sum: Input list of numbers cannot " +
              "be null\\.")
  public void testExceptionForNullList() {
    CalculateSumSubset.action(null, 5.0, 0.0);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = "Error occurred while finding subset sum: Target sum cannot be null\\.")
  public void testExceptionForNullSum() {
    inputList.add(new NumberValue(1.0));
    CalculateSumSubset.action(inputList, null, 0.0);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp =
          "Error occurred while finding subset sum: Sum deviation threshold " +
              "cannot be null\\.")
  public void testExceptionForNullThreshold() {
    inputList.add(new NumberValue(1.0));
    CalculateSumSubset.action(inputList, 5.0, null);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp =
          "Error occurred while finding subset sum: Sum deviation threshold " +
              "cannot be negative\\.")
  public void testExceptionForNegativeThreshold() {
    inputList.add(new NumberValue(1.0));
    CalculateSumSubset.action(inputList, 5.0, -1.0);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp =
          "Error occurred while finding subset sum: Invalid number format in list" +
              " for value: 'abc'")
  public void testInvalidNumberFormatInList() {
    inputList.add(new NumberValue(1.0));
    StringValue nonNumericValue = new StringValue("abc");
    inputList.add(nonNumericValue);
    CalculateSumSubset.action(inputList, 5.0, 0.0);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp =
          "Error occurred while finding subset sum: List contains null values, " +
              "which cannot be converted to numbers\\.")
  public void testNullValueInList() {
    inputList.add(new NumberValue(1.0));
    inputList.add(null);
    CalculateSumSubset.action(inputList, 5.0, 0.0);
  }

}