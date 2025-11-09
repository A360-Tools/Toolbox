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
import com.automationanywhere.commandsdk.annotations.sapbapi.Attribute;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.Base64;


@BotCommand
@CommandPkg(label = "Encode base64", name = "base64Encode", description = "Encodes provided string to Base64 string",
    group_label = "String",
    icon = "String.svg", node_label = "Encode {{sourceString}} to base64 and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign encoded text to",
    property_name = "base64Encode", property_description = "Encodes provided string to base64 string",
    property_type = DataType.STRING,
    property_return_type = DataType.STRING)

public class Base64Encode {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXTAREA)
      @Pkg(label = "Source string")
      @NotEmpty
      @Attribute
      String sourceString) {
    try {
      return new StringValue(Base64.getEncoder().encodeToString(sourceString.getBytes()));
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during base 64 encode: " + e.getMessage(), e);

    }
  }

}

