package sumit.devtools.variables.record;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.List;


@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description = "Empty record", name = "EmptyRecord", label = "Empty Record", variable_return_type =
    DataType.RECORD)
public class EmptyRecord {

  @VariableExecute
  public static RecordValue EmptyRecordValue() {
    try {
      List<Value> valueList = new ArrayList<>();
      List<Schema> schemaList = new ArrayList<>();
      return new RecordValue(schemaList, valueList);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during empty record creation: " + e.getMessage(), e);
    }

  }

}