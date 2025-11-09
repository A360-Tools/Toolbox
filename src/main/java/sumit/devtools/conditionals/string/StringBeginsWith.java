package sumit.devtools.conditionals.string;

/**
 * @author Sumit Kumar
 */

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.ConditionTest;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;


@BotCommand(commandType = BotCommand.CommandType.Condition)
@CommandPkg(description = "Checks if string begins with given prefix",
    name = "stringBeginsWith",
    label = "String begins with",
    icon = "String.svg",
    node_label = "String {{sourcestring}} begins with {{prefix}}")
public class StringBeginsWith {

  @ConditionTest
  public static Boolean validate(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "Source String")
      @NotEmpty
      String sourcestring,

      @Idx(index = "2", type = TEXT)
      @Pkg(label = "Prefix")
      @NotEmpty
      String prefix,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether the comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Hello' matches 'hello'). " +
              "When true: Case-sensitive (e.g., 'Hello' does not match 'hello').",
          default_value = "false",
          default_value_type = BOOLEAN)
      @NotEmpty
      Boolean isMatchCase
  ) {
    try {
      if (isMatchCase) {
        return sourcestring.startsWith(prefix);
      }
      return sourcestring.regionMatches(true, 0, prefix, 0, prefix.length());
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking if string begins with prefix. Error: " + e.getMessage(),
          e);
    }
  }

}