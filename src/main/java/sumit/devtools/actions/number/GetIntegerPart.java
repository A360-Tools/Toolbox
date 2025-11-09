package sumit.devtools.actions.number;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Get integral part",
    description = "Gets integral part of a number",
    icon = "Number.svg",
    name = "getIntegerPart",
    group_label = "Number",
    node_label = "of {{inputNumber}} and assign to {{returnTo}}",
    return_description = "Integral part of a number",
    return_required = true,
    //allowed_agent_targets = AllowedTarget.HEADLESS,
    multiple_returns = {
        @CommandPkg.Returns(return_label = "Assign integral part as number to",
            return_name = "IntegralAsNumber",
            return_description = "Integral part of the number as a number",
            return_type = DataType.NUMBER),
        @CommandPkg.Returns(return_label = "Assign integral part as string to",
            return_name = "IntegralAsString",
            return_description = "Integral part of the number as a string",
            return_type = DataType.STRING)
    })
public class GetIntegerPart {

  @Execute
  public static DictionaryValue action(
      @Idx(index = "1", type = AttributeType.NUMBER)
      @Pkg(label = "Enter number to extract integral part from")
      @NotEmpty
      Double inputNumber
  ) {
    try {
      // Extracting the integral part
      int integralPart = inputNumber.intValue();

      // Prepare return values
      Map<String, Value> returnValue = new HashMap<>();
      NumberValue numberValue = new NumberValue(integralPart, true);
      returnValue.put("IntegralAsNumber", numberValue);

      String integralPartAsString = new NumberValue(integralPart, true).toString();
      returnValue.put("IntegralAsString", new StringValue(integralPartAsString));

      return new DictionaryValue(returnValue);

    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while getting the integral part: " + e.getMessage(), e);
    }
  }

}
