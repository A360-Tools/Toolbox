package sumit.devtools.properties.string;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXTAREA;

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
import java.util.Base64;


@BotCommand
@CommandPkg(label = "Decode base64", name = "base64Decode", description = "Decodes provided Base64 to plain string",
    group_label = "String",
    icon = "String.svg", node_label = "Decode base64 string {{sourceString}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign decoded text to",
    property_name = "base64Decode", property_description = "Decodes provided base64 to plain string",
    property_type = DataType.STRING,
    property_return_type = DataType.STRING)

public class Base64Decode {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXTAREA)
      @Pkg(label = "Base64 encoded string")
      @NotEmpty
      String sourceString) {
    try {
      return new StringValue(new String(Base64.getDecoder().decode(sourceString)));
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during base 64 decode: " + e.getMessage(), e);

    }
  }

}

