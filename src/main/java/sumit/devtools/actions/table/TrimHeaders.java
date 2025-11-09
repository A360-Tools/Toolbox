package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.HELP;

import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Inject;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import sumit.devtools.utils.TableUtil;

@BotCommand
@CommandPkg(
    label = "Trim headers",
    name = "TrimTableSchema",
    icon = "Table.svg",
    group_label = "Table",
    description = "Creates an independent copy of the table with trimmed headers. The original table remains unchanged.",
    node_label = "Create copy of {{inputTable}} with trimmed headers and assign to {{returnTo}}",
    return_description = "Independent copy of table with trimmed headers",
    return_required = true,
    return_label = "Assign modified table to",
    return_type = DataType.TABLE
)

public class TrimHeaders {

  @Execute
  public static TableValue action(
      @Idx(index = "1", type = HELP)
      @Pkg(label = "Tip", description = "This action creates an independent copy. To update the original table, assign the output back to the same variable.")
      @Inject
      String help,

      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable
  ) {
    try {
      Table Output = TableUtil.copyTable(inputTable);
      for (Schema schema : Output.getSchema()) {
        schema.setName(schema.getName().strip());
      }
      return new TableValue(Output);
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while trimming headers: " + e.getMessage(), e);
    }

  }

}
