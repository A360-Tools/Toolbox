package sumit.devtools.variables.table;

import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.List;


@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description = "Empty table", name = "EmptyTable", label = "Empty Table", variable_return_type =
    DataType.TABLE)
public class EmptyTable {

  @VariableExecute
  public static TableValue EmptyTableValue() {
    try {
      List<Row> rowList = new ArrayList<>();
      List<Schema> schemaList = new ArrayList<>();
      Table Output = new Table(schemaList, rowList);
      return new TableValue(Output);

    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during empty datatable creation: " + e.getMessage(), e);
    }

  }

}