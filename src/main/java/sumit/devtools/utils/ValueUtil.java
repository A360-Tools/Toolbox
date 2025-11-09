package sumit.devtools.utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.BooleanValue;
import com.automationanywhere.botcommand.data.impl.CredentialObject;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.data.impl.FormValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.SessionValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.impl.WindowValue;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.data.model.table.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for deep copying Value objects.
 *
 * @author Sumit Kumar
 */
public class ValueUtil {

  /**
   * Creates a deep copy of a Value object.
   * For primitive types (String, Number, Boolean, DateTime, File, Credential), creates new instances.
   * For complex types (List, Dictionary, Table, Record), recursively deep copies all contained values.
   * For immutable reference types (Session, Window, Form), returns the original reference.
   *
   * @param value The Value to deep copy
   * @return A deep copy of the value, or the original reference for immutable types
   */
  public static Value deepCopyValue(Value value) {
    if (value == null) {
      return null;
    }

    // Immutable/Reference types - return original reference
    if (value instanceof SessionValue || value instanceof WindowValue || value instanceof FormValue) {
      return value;
    }

    // Primitive types - copy via constructor
    if (value instanceof StringValue) {
      return new StringValue(((StringValue) value).get());
    }
    if (value instanceof NumberValue) {
      return new NumberValue(((NumberValue) value).get());
    }
    if (value instanceof BooleanValue) {
      return new BooleanValue(((BooleanValue) value).get());
    }
    if (value instanceof DateTimeValue) {
      return new DateTimeValue(((DateTimeValue) value).get());
    }
    if (value instanceof FileValue) {
      return new FileValue(((FileValue) value).get());
    }
    if (value instanceof CredentialObject) {
      return new CredentialObject(((CredentialObject) value).get().getInsecureString());
    }

    // Complex/Container types - recursive deep copy
    if (value instanceof ListValue) {
      ListValue listValue = (ListValue) value;
      List<Value> originalList = listValue.get();
      List<Value> copiedList = new ArrayList<>();
      for (Value item : originalList) {
        copiedList.add(deepCopyValue(item));
      }
      ListValue newListValue = new ListValue();
      newListValue.set(copiedList);
      return newListValue;
    }

    if (value instanceof DictionaryValue) {
      DictionaryValue dictValue = (DictionaryValue) value;
      Map<String, Value> originalMap = dictValue.get();
      Map<String, Value> copiedMap = new HashMap<>();
      for (Map.Entry<String, Value> entry : originalMap.entrySet()) {
        copiedMap.put(entry.getKey(), deepCopyValue(entry.getValue()));
      }
      DictionaryValue newDictValue = new DictionaryValue();
      newDictValue.set(copiedMap);
      return newDictValue;
    }

    if (value instanceof TableValue) {
      TableValue tableValue = (TableValue) value;
      Table copiedTable = TableUtil.copyTable(tableValue.get());
      return new TableValue(copiedTable);
    }

    if (value instanceof RecordValue) {
      RecordValue recordValue = (RecordValue) value;
      Record copiedRecord = TableUtil.copyRecord(recordValue.get());
      return new RecordValue(copiedRecord.getSchema(), copiedRecord.getValues());
    }

    // Fallback for unknown types - return original reference
    return value;
  }
}
