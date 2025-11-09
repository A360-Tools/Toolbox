package sumit.devtools.actions.list;

import static com.automationanywhere.commandsdk.model.DataType.ANY;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import sumit.devtools.utils.ListUtil;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Shuffle list",
    description = "Creates an independent copy of the list with items in random order. The original list remains unchanged.",
    icon = "List.svg",
    name = "shuffleList",
    group_label = "List",
    node_label = "Create shuffled copy of {{inputList}} and assign to {{returnTo}}",
    return_description = "Independent copy of list with items in random order",
    return_required = true,
    return_label = "Assign shuffled list to",
    //allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST
)
public class ShuffleList {

  // Use a static SecureRandom instance for better randomness
  private static final Random RANDOM = new SecureRandom();

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> inputList
  ) {
    try {
      // Handle null input
      if (inputList == null) {
        inputList = new ArrayList<>();
      }

      // For empty lists, return empty list
      if (inputList.isEmpty()) {
        ListValue retList = new ListValue();
        retList.set(new ArrayList<>());
        return retList;
      }

      // For lists with only 1 element, return a copy as-is
      if (inputList.size() == 1) {
        List<Value> singleItemList = ListUtil.deepCopyList(inputList);
        ListValue retList = new ListValue();
        retList.set(singleItemList);
        return retList;
      }

      // Create a copy of the input list
      List<Value> shuffledList = ListUtil.deepCopyList(inputList);

      // For lists with 2 or more elements, ensure different order
      int maxAttempts = 100; // Prevent infinite loop in edge cases
      int attempts = 0;

      do {
        Collections.shuffle(shuffledList, RANDOM);
        attempts++;

        // Check if the order is different
        if (!isOrderSame(inputList, shuffledList)) {
          break;
        }

        // For very small lists or lists with many duplicate values,
        // it might be hard to get a different order randomly
        if (attempts >= maxAttempts) {
          // Force a different order by rotating or reversing
          forceDifferentOrder(inputList, shuffledList);
          break;
        }
      } while (true);

      // Create and return the result
      ListValue retList = new ListValue();
      retList.set(shuffledList);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while shuffling the list: " + e.getMessage(),
          e);
    }
  }

  /**
   * Checks if two lists have the same order
   */
  private static boolean isOrderSame(List<Value> list1, List<Value> list2) {
    if (list1.size() != list2.size()) {
      return false;
    }

    for (int i = 0; i < list1.size(); i++) {
      // Compare values at each position
      Value val1 = list1.get(i);
      Value val2 = list2.get(i);

      // If any position has different values, order is different
      if (!areValuesEqual(val1, val2)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Compares two Value objects for equality
   */
  private static boolean areValuesEqual(Value val1, Value val2) {
    if (val1 == null && val2 == null) {
      return true;
    }
    if (val1 == null || val2 == null) {
      return false;
    }

    // Compare the actual values
    Object obj1 = val1.get();
    Object obj2 = val2.get();

    if (obj1 == null && obj2 == null) {
      return true;
    }
    if (obj1 == null || obj2 == null) {
      return false;
    }

    return obj1.equals(obj2);
  }

  /**
   * Forces a different order when random shuffling fails This handles edge cases like lists with
   * many duplicates
   */
  private static void forceDifferentOrder(List<Value> original, List<Value> shuffled) {
    // Try rotating the list
    Collections.rotate(shuffled, 1);

    // If rotation results in the same order (all elements are identical),
    // try reversing
    if (isOrderSame(original, shuffled)) {
      Collections.reverse(shuffled);
    }

    // If still the same (shouldn't happen unless all elements are identical),
    // at least we tried our best
  }

}