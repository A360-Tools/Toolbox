package sumit.devtools.properties.url;

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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@BotCommand
@CommandPkg(label = "Decode URL", name = "decodeURL", description = "Decodes a URL-encoded string",
    group_label = "URL",
    icon = "Url.svg", node_label = "Decode URL-encoded {{sourceString}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign decoded URL to",
    property_name = "decodeURL", property_description = "Decodes a URL-encoded string",
    property_type = DataType.STRING,
    property_return_type = DataType.STRING)
public class DecodeURL {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "URL-encoded string")
      @NotEmpty
      String sourceString) {
    try {
      String decodedUrl = URLDecoder.decode(sourceString, StandardCharsets.UTF_8);
      return new StringValue(decodedUrl);
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during URL decoding: " + e.getMessage(), e);
    }
  }

}