package sumit.devtools.actions.table;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import sumit.devtools.utils.ValueUtil;


@BotCommand
@CommandPkg(label = "Extract table columns", description = "Creates an independent table copy containing only the specified columns. The original table remains unchanged.", icon =
    "Table" +
        ".svg", name = "sliceColumns", group_label = "Table",
    node_label = "Create copy of {{inputTable}} with selected columns and assign to {{returnTo}}", return_description =
    "Independent table copy with only selected columns",
    return_required = true,
    return_label = "Assign sliced table to",
    return_type = DataType.TABLE)

public class SliceColumns {

  @Execute
  public static TableValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,
      @Idx(index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Column index", value = "INDEX_LIST")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Column names", value = "COLUMN_NAMES"))})
      @Pkg(label = "Select columns by", description = "Columns to be sliced", default_value = "INDEX_LIST",
          default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String selectMethod,

      @Idx(index = "2.1.1", type = AttributeType.LIST)
      @Pkg(label = "List of column indexes", description = "List of specific column indices to extract (0-based). " +
          "Each column at the specified index will be INCLUDED in the result. " +
          "Example: [0, 2, 3] extracts the 1st, 3rd, and 4th columns.")
      @NotEmpty
      @ListType(DataType.NUMBER)
      List<Value> indexList,

      @Idx(index = "2.2.1", type = AttributeType.LIST)
      @Pkg(label = "List of column names", description = "Names of the columns to extract. " +
          "All specified columns will be INCLUDED in the result. " +
          "Example: ['Name', 'Email', 'Phone'] extracts only these three columns.")
      @NotEmpty
      @ListType(DataType.STRING)
      List<StringValue> columnNameList
  ) {
    try {
      List<Schema> inputSchema = inputTable.getSchema();
      List<Schema> copySchema = new ArrayList<>();
      List<Row> retRows;
      switch (selectMethod.toUpperCase()) {
        case "INDEX_LIST":
          if (indexList == null) {
            throw new BotCommandException("No column index provided");
          }
          int[] columnIndexArray = indexList.stream()
              .map(value -> (new NumberValue(value.get())).getAsDouble())
              .mapToInt(Double::intValue)
              .toArray();

          copySchema = Arrays.stream(columnIndexArray)
              .mapToObj(i -> {
                Schema originalSchema = inputSchema.get(i);
                return new Schema(originalSchema.getName(), originalSchema.getType());
              })
              .collect(Collectors.toList());

          retRows = inputTable.getRows().parallelStream()
              .map(row -> {
                List<Value> extractedValues = Arrays.stream(columnIndexArray)
                    .mapToObj(row.getValues()::get)
                    .map(ValueUtil::deepCopyValue)  // Deep copy each value
                    .collect(Collectors.toList());
                return new Row(extractedValues);
              })
              .collect(Collectors.toList());

          break;

        case "COLUMN_NAMES":
          if (columnNameList == null) {
            throw new BotCommandException("No column names provided");
          }
          Map<String, Integer> schemaNameToIndexMap = new HashMap<>();
          for (int i = 0; i < inputSchema.size(); i++) {
            schemaNameToIndexMap.put(inputSchema.get(i).getName(), i);
          }

          for (Value columnName : columnNameList) {
            if (schemaNameToIndexMap.containsKey(columnName.get().toString())) {
              int index = schemaNameToIndexMap.get(columnName.toString());
              copySchema.add(new Schema(inputSchema.get(index).getName(),
                  inputSchema.get(index).getType()));
            }
          }

          List<Schema> finalCopySchema = copySchema;
          retRows = inputTable.getRows().parallelStream()
              .map(row -> {
                List<Value> extractedValues = finalCopySchema.stream()
                    .map(schema -> {
                      int index = schemaNameToIndexMap.get(schema.getName());
                      return row.getValues().get(index);
                    })
                    .map(ValueUtil::deepCopyValue)  // Deep copy each value
                    .collect(Collectors.toList());
                return new Row(extractedValues);
              })
              .collect(Collectors.toList());

          break;
        default:
          throw new Exception("Invalid select method: " + selectMethod);
      }

      Table retTable = new Table();
      retTable.setSchema(copySchema);
      retTable.setRows(retRows);
      return new TableValue(retTable);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error Occurred while extracting table columns: " + e.getMessage(), e);
    }

  }

}
