package sumit.devtools.actions.number;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThan;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sumit Kumar This BotCommand finds a subset of numbers from a given list whose values add
 * up to a specified target sum, within a given deviation threshold. It uses a recursive
 * backtracking algorithm to find the first such subset.
 */
@BotCommand
@CommandPkg(label = "Calculate Subset Sum",
    description =
        "Finds a subset of numbers whose values add up to a given target sum, within a specified " +
            "deviation threshold.",
    icon = "Number.svg",
    name = "calculateSumSubset",
    group_label = "Number",
    node_label =
        "Find a subset in {{inputList}} adding up to {{sum}} (threshold {{threshold}}) and assign to {{returnTo}}",
    return_description =
        "A list of numbers representing the first subset found that matches the sum criteria. " +
            "Returns an empty list if no such subset is found.",
    return_required = true,
    return_label = "Assign subset to",
    return_type = DataType.LIST,
    return_sub_type = DataType.NUMBER)
public class CalculateSumSubset {

  /**
   * Finds the first subset of numbers that sum up to the target value, within the given threshold.
   *
   * @param numbers   Array of numbers to choose from.
   * @param target    The target sum.
   * @param threshold The allowed deviation from the target sum.
   * @return A list containing the subset of numbers, or an empty list if no such subset is found.
   */
  private static List<BigDecimal> findSubsetSum(BigDecimal[] numbers, BigDecimal target,
      BigDecimal threshold) {
    List<BigDecimal> currentSubset = new ArrayList<>();
    boolean found = findSubsetSumRecursive(numbers, target, threshold, 0, currentSubset);
    if (found) {
      return currentSubset;
    } else {
      return new ArrayList<>(); // Return empty list if no subset is found
    }
  }

  /**
   * Recursive helper function to find a subset sum.
   *
   * @param numbers         The array of available numbers.
   * @param remainingTarget The remaining sum needed to reach the original target.
   * @param threshold       The allowed absolute deviation for the remainingTarget to be considered
   *                        a match (i.e., close to zero).
   * @param index           The current index in the numbers array to consider.
   * @param currentSubset   The subset being built.
   * @return true if a valid subset is found, false otherwise.
   */
  private static boolean findSubsetSumRecursive(BigDecimal[] numbers, BigDecimal remainingTarget,
      BigDecimal threshold,
      int index, List<BigDecimal> currentSubset) {
    // Success condition: current sum is within threshold of original target
    // This means remainingTarget (OriginalTarget - CurrentSum) should be close to zero.
    if (remainingTarget.abs().compareTo(threshold) <= 0) {
      return true; // Target reached within the threshold
    }

    // Base case for recursion: If all numbers are processed and target not met
    if (index == numbers.length) {
      return false; // End of numbers list, target not met
    }

    BigDecimal currentNumber = numbers[index];

    // Option 1: Include the current number in the subset
    currentSubset.add(currentNumber);
    if (findSubsetSumRecursive(numbers, remainingTarget.subtract(currentNumber), threshold,
        index + 1,
        currentSubset)) {
      return true; // Subset found including current number
    }

    // Option 2: Exclude the current number from the subset (backtrack)
    currentSubset.remove(currentSubset.size() - 1); // Backtrack
    return findSubsetSumRecursive(numbers, remainingTarget, threshold, index + 1,
        currentSubset); // Try next
    // without current
  }

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.LIST)
      @Pkg(label = "List of numbers", description = "The list of numbers from which to find a subset.")
      @NotEmpty
      @VariableType(value = DataType.NUMBER)
      List<Value> inputList,

      @Idx(index = "2", type = AttributeType.NUMBER)
      @Pkg(label = "Target sum", description =
          "The desired sum of the subset. Must be greater than 0 as per " +
              "current rule.")
      @NotEmpty
      @GreaterThan("0") // This rule implies sum cannot be 0 or negative.
      Double sum,

      @Idx(index = "3", type = AttributeType.NUMBER)
      @Pkg(label = "Sum deviation threshold", default_value = "0", default_value_type = DataType.NUMBER,
          description =
              "The maximum allowed absolute difference between the subset's sum and the target " +
                  "sum. Must be non-negative.")
      @GreaterThanEqualTo("0")
      @NotEmpty
      Double threshold
  ) {
    try {
      if (inputList == null) {
        throw new BotCommandException("Input list of numbers cannot be null.");
      }
      if (sum == null) {
        throw new BotCommandException("Target sum cannot be null.");
      }
      if (threshold == null) {
        throw new BotCommandException("Sum deviation threshold cannot be null.");
      }
      if (threshold < 0) {
        throw new BotCommandException("Sum deviation threshold cannot be negative.");
      }

      BigDecimal targetSum = BigDecimal.valueOf(sum);
      BigDecimal deviationThreshold = BigDecimal.valueOf(threshold);
      BigDecimal[] numbers = convertToBigDecimalArray(inputList);

      List<BigDecimal> subset = findSubsetSum(numbers, targetSum, deviationThreshold);

      List<Value> resultList = subset.stream()
          .map(num -> new NumberValue(num.doubleValue())) // Removed 'true' for auto-type
          .collect(Collectors.toList());

      ListValue resultListValue = new ListValue();
      resultListValue.set(resultList);
      return resultListValue;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while finding subset sum: " + e.getMessage(),
          e);
    }
  }

  private static BigDecimal[] convertToBigDecimalArray(List<Value> inputList) {
    if (inputList == null) { // Should be caught by action method, but good for utility method
      return new BigDecimal[0];
    }
    return inputList.stream()
        .map(value -> {
          if (value == null || value.get() == null) {
            throw new IllegalArgumentException(
                "List contains null values, which cannot be converted to " +
                    "numbers.");
          }
          try {
            if (value instanceof NumberValue) {
              // Ensure NumberValue actually contains a number.
              // Using getAsDouble() directly is fine for NumberValue.
              return BigDecimal.valueOf(((NumberValue) value).get());
            } else {
              // For other Value types, attempt to parse their string representation.
              // This might be risky if the string is not a valid number.
              return new BigDecimal(value.get().toString());
            }
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid number format in list for value: '" + value.get().toString() + "'", e);
          } catch (Exception e) { // Catch other potential issues with value.get() or conversion
            throw new IllegalArgumentException("Cannot convert value to number: " + value, e);
          }
        })
        .toArray(BigDecimal[]::new);
  }

}