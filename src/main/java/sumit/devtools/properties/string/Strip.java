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
@CommandPkg(label = "Strip whitespace", name = "strip", description = "Removes all leading and trailing whitespace",
    group_label = "String",
    icon = "String.svg", node_label =
    "Remove all leading and trailing whitespace from {{sourceString}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign trimmed text to",
    property_name = "strip", property_description = "Removes all leading and trailing whitespace", property_type
    = DataType.STRING,
    property_return_type = DataType.STRING)

public class Strip {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "Source string")
      @NotEmpty
      String sourceString) {
    try {
      return new StringValue(sourceString.strip());
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during string strip operation: " + e.getMessage(), e);

    }
  }

}

