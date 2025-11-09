package sumit.devtools.utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.Comparator;
import java.util.List;

public class SortUtil {

  public static void sortAscending(List<Value> listValue) {
    listValue.sort(getAscendingComparator(true));
  }

  public static void sortAscending(List<Value> listValue, boolean caseSensitive) {
    listValue.sort(getAscendingComparator(caseSensitive));
  }

  private static Comparator<Value> getAscendingComparator(boolean caseSensitive) {
    return (v1, v2) -> {
      if (v1 instanceof NumberValue && v2 instanceof NumberValue) {
        return Double.compare(((NumberValue) v1).get(), ((NumberValue) v2).get());
      } else if (v1 instanceof StringValue && v2 instanceof StringValue) {
        String s1 = ((StringValue) v1).get();
        String s2 = ((StringValue) v2).get();
        if (caseSensitive) {
          return s1.compareTo(s2);
        } else {
          return s1.compareToIgnoreCase(s2);
        }
      } else if (v1 instanceof DateTimeValue && v2 instanceof DateTimeValue) {
        return ((DateTimeValue) v1).get().compareTo(((DateTimeValue) v2).get());
      } else if (v1.getClass() != v2.getClass()) {
        throw new IllegalArgumentException(
            "Cannot compare values of different types " + v1.getClass() + " " +
                "and " + v2.getClass());
      } else {
        throw new UnsupportedOperationException(
            "Unsupported value types " + v1.getClass() + " and " + v2.getClass());
      }
    };
  }

  public static void sortDescending(List<Value> listValue) {
    listValue.sort(getDescendingComparator(true));
  }

  public static void sortDescending(List<Value> listValue, boolean caseSensitive) {
    listValue.sort(getDescendingComparator(caseSensitive));
  }

  private static Comparator<Value> getDescendingComparator(boolean caseSensitive) {
    return getAscendingComparator(caseSensitive).reversed();
  }

}