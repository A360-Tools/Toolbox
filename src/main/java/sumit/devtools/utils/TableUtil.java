package sumit.devtools.utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sumit Kumar
 */
public class TableUtil {

  public static int getColumnIndex(List<Schema> schema, String columnName, boolean caseSensitive) {
    for (int i = 0; i < schema.size(); ++i) {
      String schemaName = schema.get(i).getName();
      boolean matches = caseSensitive
          ? schemaName.equals(columnName)
          : schemaName.equalsIgnoreCase(columnName);
      if (matches) {
        return i;
      }
    }

    return -1;
  }

  public static Table copyTable(Table sourceTable) {
    List<Schema> schemas = sourceTable.getSchema().stream()
        .map(schema -> new Schema(schema.getName(), schema.getType()))
        .collect(Collectors.toList());

    List<Row> rows = sourceTable.getRows().stream()
        .map(row -> {
          List<Value> valueList = new ArrayList<>();
          for (Value value : row.getValues()) {
            valueList.add(ValueUtil.deepCopyValue(value));
          }
          return new Row(valueList);
        })
        .collect(Collectors.toList());

    return new Table(schemas, rows);
  }

  public static Record copyRecord(Record sourceRecord) {
    List<Schema> schemas = sourceRecord.getSchema().stream()
        .map(schema -> new Schema(schema.getName(), schema.getType()))
        .collect(Collectors.toList());

    List<Value> valueList = new ArrayList<>();
    for (Value value : sourceRecord.getValues()) {
      valueList.add(ValueUtil.deepCopyValue(value));
    }

    return new Record(schemas, valueList);
  }

  public static List<Schema> convertRowToHeaderSchema(Row row) {
    List<Schema> schemaList = new ArrayList<>();
    for (Value value : row.getValues()) {
      schemaList.add(new Schema(value.get().toString()));
    }
    return schemaList;
  }

  public static void addMissingColumnValues(List<Row> rowList, int maxColumnCount) {
    for (Row row : rowList) {
      while (row.getValues().size() < maxColumnCount) {
        row.getValues().add(new StringValue());
      }
    }
  }

}
