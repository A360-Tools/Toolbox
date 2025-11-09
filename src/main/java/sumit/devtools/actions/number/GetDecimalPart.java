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
@CommandPkg(label = "Get decimal part",
    description = "Gets fractional part of a number",
    icon = "Number.svg",
    name = "getDecimalPart",
    group_label = "Number",
    node_label = "of {{inputNumber}} and assign to {{returnTo}}",
    return_description = "Fractional part of a number",
    return_required = true,
    //allowed_agent_targets = AllowedTarget.HEADLESS,
    multiple_returns = {
        @CommandPkg.Returns(return_label = "Assign decimal part as number to",
            return_name = "FractionAsNumber",
            return_description = "Fractional part of the number as a number",
            return_type = DataType.NUMBER),
        @CommandPkg.Returns(return_label = "Assign decimal part as string to",
            return_name = "FractionsAsString",
            return_description = "Fractional part of the number as a string",
            return_type = DataType.STRING)
    })
public class GetDecimalPart {

  @Execute
  public static DictionaryValue action(
      @Idx(index = "1", type = AttributeType.NUMBER)
      @Pkg(label = "Enter number to extract fractional part from")
      @NotEmpty
      Double inputNumber
  ) {
    try {
      // Calculate the fractional part
      double fractionPart = inputNumber - inputNumber.intValue();

      // Prepare return values
      Map<String, Value> returnValue = new HashMap<>();
      NumberValue numberValue = new NumberValue(fractionPart, true);
      returnValue.put("FractionAsNumber", numberValue);

      // Converting fraction part to string and ensuring it doesn't start with "0."
      String fractionAsString = new NumberValue(fractionPart, true).toString();
      fractionAsString = fractionAsString.indexOf('.') == -1 ? "0.0" : fractionAsString;
      returnValue.put("FractionAsString", new StringValue(fractionAsString));

      return new DictionaryValue(returnValue);

    } catch (Exception e) {
      throw new BotCommandException("Error occurred while getting decimal part: " + e.getMessage(),
          e);
    }


  }

}
