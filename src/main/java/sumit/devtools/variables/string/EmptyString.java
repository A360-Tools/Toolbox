package sumit.devtools.variables.string;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;


@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description = "Empty string value", name = "EmptyString", label = "Empty String value",
    variable_return_type = DataType.STRING)
public class EmptyString {

  @VariableExecute
  public static StringValue EmptyStringValue() {
    try {

      return new StringValue("");

    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during empty string creation: " + e.getMessage(), e);
    }

  }

}