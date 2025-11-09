package sumit.devtools.variables.string;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;
import org.apache.commons.lang3.SystemUtils;


@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description = "Get current user's username", name = "Username", label = "Username",
    variable_return_type = DataType.STRING)
public class Username {

  @VariableExecute
  public static StringValue getUsername() {
    try {
      String username = SystemUtils.USER_HOME;
      return new StringValue(username);
    } catch (Exception e) {
      throw new BotCommandException("Error retrieving username: " + e.getMessage(), e);
    }
  }

}