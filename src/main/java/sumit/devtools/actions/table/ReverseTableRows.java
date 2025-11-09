package sumit.devtools.actions.table;

/**
 * @author Sumit Kumar
 */

import static com.automationanywhere.commandsdk.model.AttributeType.HELP;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;

import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.table.Row;
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
import java.util.Collections;
import java.util.List;
import sumit.devtools.utils.TableUtil;

@BotCommand
@CommandPkg(label = "Reverse table", description = "Creates an independent copy of the table with rows in reverse order. The original table remains unchanged.", icon = "Table.svg",
    name = "reverseRowOrder", group_label = "Table",
    node_label = "Create reversed copy of {{inputTable}} and assign to {{returnTo}}", return_description =
    "Independent copy of table with reversed row order",
    return_required = true,
    return_type = TABLE)
public class ReverseTableRows {

  @Execute
  public static TableValue action(
      @Idx(index = "1", type = HELP)
      @Pkg(label = "Tip", description = "This action creates an independent copy. To update the original table, assign the output back to the same variable.")
      @Inject
      String help,

      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = TABLE)
      Table inputTable
  ) {
    try {
      Table Output = TableUtil.copyTable(inputTable);
      List<Row> rows = Output.getRows();
      Collections.reverse(rows);
      Output.setRows(rows);
      return new TableValue(Output);
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while reversing table rows: " + e.getMessage(),
          e);
    }
  }

}

