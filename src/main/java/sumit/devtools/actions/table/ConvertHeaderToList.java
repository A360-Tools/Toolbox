package sumit.devtools.actions.table;

import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Table;
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
import java.util.ArrayList;
import java.util.List;

@BotCommand
@CommandPkg(
    label = "Convert Header To List",
    name = "convertHeaderToList",
    icon = "Table.svg",
    group_label = "Table",
    description = "Gets datatable headers to list",
    node_label = "Convert headers from {{inputTable}} and assign to {{returnTo}}",
    return_description = "Returns selected table headers as list",
    return_required = true,
    return_label = "Assign headers list to",
    return_type = DataType.LIST,
    return_sub_type = DataType.STRING
)

public class ConvertHeaderToList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table", description = "Table whose header is to be saved as list")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,
      @Idx(index = "2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Trim leading and trailing whitespace",
          description = "Controls whether to remove leading and trailing whitespace from header names. " +
              "When false (default): Keeps whitespace as-is (e.g., '  Name  ' remains '  Name  '). " +
              "When true: Removes whitespace (e.g., '  Name  ' → 'Name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean isStrip
  ) {
    try {
      List<Schema> inputTableSchema = inputTable.getSchema();
      ListValue retList = new ListValue();
      List<StringValue> output = new ArrayList<>();

      for (Schema schema : inputTableSchema) {
        if (isStrip) {
          output.add(new StringValue(schema.getName().strip()));
        } else {
          output.add(new StringValue(schema.getName()));
        }
      }

      retList.set(output);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException(
          "Error Occurred while extracting header to list: " + e.getMessage(), e);
    }
  }

}