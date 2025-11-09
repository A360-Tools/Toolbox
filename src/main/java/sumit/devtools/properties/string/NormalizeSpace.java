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
import org.apache.commons.lang3.StringUtils;

@BotCommand
@CommandPkg(label = "Normalize space", name = "normalizeSpace", description = "Normalizes whitespace in a string",
    group_label = "String",
    icon = "String.svg", node_label =
    "Normalize whitespace in {{sourceString}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign normalized text to",
    property_name = "normalizeSpace", property_description = "Normalizes whitespace in a string", property_type =
    DataType.STRING,
    property_return_type = DataType.STRING)
public class NormalizeSpace {

  @Execute
  public static Value<String> normalize(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "Source string")
      @NotEmpty
      String sourceString) {
    try {
      return new StringValue(StringUtils.normalizeSpace(sourceString));
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during whitespace normalization: " + e.getMessage(), e);
    }
  }

}
