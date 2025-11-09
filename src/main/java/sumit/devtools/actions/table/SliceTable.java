package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
import static com.automationanywhere.commandsdk.model.DataType.NUMBER;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.NumberValue;
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
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import sumit.devtools.utils.ValueUtil;


@BotCommand
@CommandPkg(label = "Extract table rows", description = "Creates an independent table copy containing only the specified rows. The original table remains unchanged.", icon = "Table.svg",
    name = "sliceTable", group_label = "Table",
    node_label = "Create independent copy with selected rows from {{inputTable}} and assign to {{returnTo}}", return_description =
    "Independent table copy with only the selected rows",
    return_required = true,
    return_type = TABLE)

public class SliceTable {

  @Execute
  public static TableValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base Table")
      @NotEmpty
      @VariableType(value = TABLE)
      Table inputTable,
      @Idx(index = "2", type = SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Row range", value = "RANGE")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Row index", value = "INDEX_LIST"))})
      @Pkg(label = "Select rows by", description = "Rows to be sliced", default_value = "RANGE",
          default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String selectMethod,

      @Idx(index = "2.1.1", type = AttributeType.NUMBER)
      @Pkg(label = "Start row index", description = "Starting row index (INCLUSIVE, 0-based). " +
          "The row at this index will be INCLUDED in the result. " +
          "Example: Start=0 means first row will be included.")
      @NotEmpty
      @GreaterThanEqualTo("0")
      Double startRowIndex,

      @Idx(index = "2.1.2", type = AttributeType.NUMBER)
      @Pkg(label = "End row index", description = "Ending row index (INCLUSIVE, 0-based). " +
          "The row at this index will be INCLUDED in the result. " +
          "Example: Start=0, End=2 extracts rows at indices 0, 1, and 2 (first 3 rows).")
      @NotEmpty
      @GreaterThanEqualTo("0")
      Double endRowIndex,

      @Idx(index = "2.2.1", type = AttributeType.LIST)
      @Pkg(label = "List of row indexes", description = "List of specific row indices to extract (0-based). " +
          "Each row at the specified index will be INCLUDED in the result. " +
          "Example: [0, 2, 4] extracts the 1st, 3rd, and 5th rows.")
      @NotEmpty
      @ListType(NUMBER)
      List<Value> indexList
  ) {
    try {

      List<Schema> copySchema = inputTable.getSchema().stream()
          .map(schema -> new Schema(schema.getName(), schema.getType()))
          .collect(Collectors.toList());

      List<Row> retRows;

      switch (selectMethod.toUpperCase()) {
        case "RANGE":
          int startRowIndexInt = startRowIndex.intValue();
          int endRowIndexInt = endRowIndex.intValue();
          int maxIndex = inputTable.getRows().size() - 1;
          endRowIndexInt = Math.min(endRowIndexInt, maxIndex);

          retRows = IntStream.rangeClosed(startRowIndexInt, endRowIndexInt)
              .mapToObj(i -> inputTable.getRows().get(i))
              .map(row -> {
                // Deep copy each value in the row
                List<Value> deepCopiedValues = new ArrayList<>();
                for (Value value : row.getValues()) {
                  deepCopiedValues.add(ValueUtil.deepCopyValue(value));
                }
                return new Row(deepCopiedValues);
              })
              .collect(Collectors.toList());

          break;
        case "INDEX_LIST":
          if (indexList == null) {
            throw new BotCommandException("No row index provided");
          }
          int[] intIndexList = indexList.stream()
              .map(value -> (new NumberValue(value.get())).getAsDouble())
              .mapToInt(Double::intValue)
              .toArray();

          retRows = Arrays.stream(intIndexList).sequential()
              .mapToObj(i -> inputTable.getRows().get(i))
              .map(row -> {
                // Deep copy each value in the row
                List<Value> deepCopiedValues = new ArrayList<>();
                for (Value value : row.getValues()) {
                  deepCopiedValues.add(ValueUtil.deepCopyValue(value));
                }
                return new Row(deepCopiedValues);
              })
              .collect(Collectors.toList());

          break;

        default:
          throw new BotCommandException("Invalid option: " + selectMethod);
      }

      Table retTable = new Table();
      retTable.setSchema(copySchema);
      retTable.setRows(retRows);
      return new TableValue(retTable);

    } catch (Exception e) {
      throw new BotCommandException("Error Occurred while slicing table: " + e.getMessage(), e);
    }


  }

}
