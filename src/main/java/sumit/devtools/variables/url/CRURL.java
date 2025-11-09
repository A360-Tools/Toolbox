package sumit.devtools.variables.url;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;


@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description = "Get control room url", name = "CRURL", label = "CR URL", variable_return_type =
    DataType.STRING)
public class CRURL {

  @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
  private GlobalSessionContext globalSessionContext;


  public void setGlobalSessionContext(GlobalSessionContext globalSessionContext) {
    this.globalSessionContext = globalSessionContext;
  }

  @VariableExecute
  public StringValue getCRURL() {
    try {

      String token = this.globalSessionContext.getCrUrl();
      return new StringValue(token);

    } catch (Exception e) {
      throw new BotCommandException("Error occurred during token extraction: " + e.getMessage(), e);
    }

  }

}