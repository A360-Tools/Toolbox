package sumit.devtools.utils;

import com.automationanywhere.botcommand.data.Value;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for list operations.
 *
 * @author Sumit Kumar
 */
public class ListUtil {

  /**
   * Creates a deep copy of a list of Values.
   * Each Value in the list is deep copied using ValueUtil.deepCopyValue().
   *
   * @param sourceList The list to deep copy
   * @return A new list containing deep copies of all values from the source list
   */
  public static List<Value> deepCopyList(List<Value> sourceList) {
    if (sourceList == null) {
      return null;
    }

    List<Value> copiedList = new ArrayList<>();
    for (Value value : sourceList) {
      copiedList.add(ValueUtil.deepCopyValue(value));
    }
    return copiedList;
  }
}
