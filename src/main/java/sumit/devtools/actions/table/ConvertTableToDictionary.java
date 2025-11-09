package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.NUMBER;
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.ANY;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sumit.devtools.utils.TableUtil;

@BotCommand
@CommandPkg(
    label = "Convert Table To Dictionary",
    name = "convertTableToDictionary",
    icon = "Table.svg",
    group_label = "Table",
    description = "Converts table column data to key-value pairs; repeated keys will be overwritten",
    node_label = "Convert table to dictionary from {{inputTable}} by {{valueSelectMethod}} and assign to {{returnTo}}",
    return_description = "Returns dictionary with keys(Key column values) and values(Value column values)",
    return_required = true,
    return_label = "Assign dictionary to",
    return_type = DataType.DICTIONARY, return_sub_type = ANY
)

public class ConvertTableToDictionary {

  @Execute
  public static DictionaryValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table", description = "Table whose columns are to be saved as key-value pair")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(index = "2", type = SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Header", value = "NAME")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Index", value = "INDEX"))})
      @Pkg(label = "Select key column by", description =
          "Column whose value is to be saved as keys of " +
              "dictionary", default_value = "NAME", default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String keySelectMethod,

      @Idx(index = "2.1.1", type = TEXT)
      @Pkg(label = "Column header")
      @NotEmpty
      String keyColNameInput,

      @Idx(index = "2.1.2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether column name comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name' matches 'name', 'NAME'). " +
              "When true: Case-sensitive (e.g., 'Name' does not match 'name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean keyColCaseSensitive,

      @Idx(index = "2.2.1", type = NUMBER)
      @Pkg(label = "Column index", description =
          "Index numbers start with 0. For example, first column has an " +
              "index of 0.")
      @NotEmpty
      @GreaterThanEqualTo("0")
      @NumberInteger
      Double keyColIndexInput,

      @Idx(index = "3", type = SELECT, options = {
          @Idx.Option(index = "3.1", pkg = @Pkg(label = "Header", value = "NAME")),
          @Idx.Option(index = "3.2", pkg = @Pkg(label = "Index", value = "INDEX"))})
      @Pkg(label = "Select value column by", description =
          "Column whose value is to be saved to as values of " +
              "dictionary", default_value = "NAME", default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String valueSelectMethod,

      @Idx(index = "3.1.1", type = TEXT)
      @Pkg(label = "Column header")
      @NotEmpty
      String valueColNameInput,

      @Idx(index = "3.1.2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether column name comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name' matches 'name', 'NAME'). " +
              "When true: Case-sensitive (e.g., 'Name' does not match 'name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean valueColCaseSensitive,

      @Idx(index = "3.2.1", type = NUMBER)
      @Pkg(label = "Column index", description =
          "Index numbers start with 0. For example, first column has an " +
              "index of 0.")
      @NotEmpty
      @GreaterThanEqualTo("0")
      @NumberInteger
      Double valColIndexInput
  ) {
    try {
      List<Schema> inputTableSchema = inputTable.getSchema();

      int keyColumnIndex;
      int valueColumnIndex;

      if (keySelectMethod.equalsIgnoreCase("NAME")) {
        keyColumnIndex = TableUtil.getColumnIndex(inputTableSchema, keyColNameInput, keyColCaseSensitive);
      } else {
        keyColumnIndex = keyColIndexInput.intValue();
      }

      if (valueSelectMethod.equalsIgnoreCase("NAME")) {
        valueColumnIndex = TableUtil.getColumnIndex(inputTableSchema, valueColNameInput, valueColCaseSensitive);
      } else {
        valueColumnIndex = valColIndexInput.intValue();
      }

      if (keyColumnIndex < 0) {
        throw new Exception("Key column not found");
      }
      if (valueColumnIndex < 0) {
        throw new Exception("Value column not found");
      }

      Map<String, Value> map = new LinkedHashMap<>();
      for (Row row : inputTable.getRows()) {
        map.put(row.getValues().get(keyColumnIndex).toString(),
            row.getValues().get(valueColumnIndex));
      }
      DictionaryValue dictValue = new DictionaryValue();
      dictValue.set(map);
      return dictValue;
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while converting table to dictionary: " + e.getMessage(), e);
    }

  }

}
