package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.NUMBER;
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.ANY;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
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
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NumberInteger;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import sumit.devtools.utils.TableUtil;

@SuppressWarnings("rawtypes")
@BotCommand
@CommandPkg(
    label = "Convert Column To List",
    name = "convertColumnToList",
    icon = "Table.svg",
    group_label = "Table",
    description = "Gets datatable column to list",
    node_label = "Convert column from {{inputTable}} by {{selectMethod}} |{{colIndex}} || {{colName}}| and assign to {{returnTo}}",
    return_description = "Returns selected table column as list",
    return_required = true,
    return_label = "Assign column values to",
    return_type = DataType.LIST,
    return_sub_type = ANY
)
public class ConvertColumnToList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table", description = "Table whose column is to be saved as list")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(index = "2", type = SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Header", value = "name")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Index", value = "index"))})
      @Pkg(label = "Select column by", description = "Column whose value is to be saved to list",
          default_value = "name", default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String selectMethod,

      @Idx(index = "2.1.1", type = TEXT)
      @Pkg(label = "Column header")
      @NotEmpty
      String colName,

      @Idx(index = "2.1.2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether column name comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name' matches 'name', 'NAME'). " +
              "When true: Case-sensitive (e.g., 'Name' does not match 'name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean caseSensitive,

      @Idx(index = "2.2.1", type = NUMBER)
      @Pkg(label = "Column index", description =
          "Index numbers start with 0. For example, first column has an " +
              "index of 0.")
      @NotEmpty
      @GreaterThanEqualTo("0")
      @NumberInteger
      Double colIndex
  ) {
    try {

      if (inputTable == null) {
        throw new BotCommandException("Input table cannot be null.");
      }

      List<Schema> inputTableSchema = inputTable.getSchema();
      if (inputTableSchema == null) {
        inputTableSchema = new ArrayList<>();
      }

      ListValue retList = new ListValue();
      List<Value> output = new ArrayList<>();
      int schemaIndex;

      if ("name".equals(selectMethod)) {
        if (colName == null || colName.trim().isEmpty()) {
          throw new BotCommandException(
              "Column header name cannot be empty when selecting by name.");
        }
        schemaIndex = TableUtil.getColumnIndex(inputTableSchema, colName, caseSensitive);

      } else if ("index".equals(selectMethod)) {
        if (colIndex == null) {
          throw new BotCommandException("Column index cannot be null when selecting by index.");
        }
        schemaIndex = colIndex.intValue();

      } else {
        throw new BotCommandException("Invalid column selection method: " + selectMethod);
      }

      List<Row> rows = Objects.requireNonNullElse(inputTable.getRows(), new ArrayList<>());

      for (Row row : rows) {
        if (row == null) {
          continue;
        }
        List<Value> rowValues = row.getValues();
        if (rowValues == null) {
          continue;
        }
        if (schemaIndex >= 0 && schemaIndex < rowValues.size()) {
          output.add(rowValues.get(schemaIndex));
        }
      }

      retList.set(output);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while extracting column to list: " + e.getMessage(), e);
    }
  }

}