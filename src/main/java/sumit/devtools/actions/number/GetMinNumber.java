package sumit.devtools.actions.number;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.Comparator;
import java.util.List;

/**
 * @author Sumit Kumar
 */


@BotCommand
@CommandPkg(label = "Get Minimum Number",
    description = "Gets minimum number from a list of numbers",
    icon = "Number.svg",
    name = "getMinNumber",
    group_label = "Number",
    node_label = "Find minimum number in list {{numbers}} and assign to {{returnTo}}",
    return_label = "Assign minimum number to",
    //allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.NUMBER,
    return_required = true)
public class GetMinNumber {

  @Execute
  public static NumberValue action(
      @Idx(index = "1", type = AttributeType.LIST)
      @Pkg(label = "List of numbers")
      @NotEmpty
      @ListType(DataType.NUMBER)
      List<Value> numbers
  ) {
    try {
      double result = numbers.stream()
          .map(Value::getAsDouble)
          .min(Comparator.naturalOrder())
          .orElseThrow(() -> new BotCommandException("Input list is empty"));

      return new NumberValue(result, true);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error Occurred while getting minimum number: " + e.getMessage(), e);
    }
  }

}
