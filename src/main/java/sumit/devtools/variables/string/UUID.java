package sumit.devtools.variables.string;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;


@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description = "Universally Unique Identifier value", name = "UUID", label = "UUID value",
    variable_return_type = DataType.STRING)
public class UUID {

  @VariableExecute
  public static StringValue action(
  ) {
    try {
      return new StringValue(java.util.UUID.randomUUID().toString());
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during UUID generation: " + e.getMessage(), e);
    }

  }

}



