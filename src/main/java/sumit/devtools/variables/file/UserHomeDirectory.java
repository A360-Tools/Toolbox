package sumit.devtools.variables.file; // Assuming the same package structure

import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;
import org.apache.commons.lang3.SystemUtils;

@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description = "Gets the absolute path to the current user's home directory.",
    name = "UserHomeDir",
    label = "User Home Directory",
    variable_return_type = DataType.FILE)
public class UserHomeDirectory {

  @VariableExecute
  public static FileValue getUserHomeDir() {
    try {

      return new FileValue(SystemUtils.USER_HOME);

    } catch (Exception e) {
      throw new BotCommandException("Error retrieving user home directory: " + e.getMessage(), e);
    }
  }

}
