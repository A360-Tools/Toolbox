package sumit.devtools.properties.string;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.DataType;


@BotCommand
@CommandPkg(label = "Strip leading whitespace", name = "stripLeading", description = "Removes all leading whitespace"
    , group_label = "String",
    icon = "String.svg", node_label =
    "Remove all leading whitespace from {{sourceString}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign trimmed text to",
    property_name = "stripLeading", property_description = "Removes all leading whitespace", property_type =
    DataType.STRING,
    property_return_type = DataType.STRING)

public class StripLeading {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "Source string")
      @NotEmpty
      String sourceString) {
    try {
      return new StringValue(sourceString.stripLeading());
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during strip strip leading whitespace operation: " + e.getMessage(), e);

    }

  }

}

