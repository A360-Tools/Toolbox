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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@BotCommand
@CommandPkg(label = "Encode URL", name = "encodeURL", description = "Encodes a string for use in a URL",
    group_label = "URL",
    icon = "Url.svg", node_label = "Encode {{sourceString}} for URL and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign encoded URL to",
    property_name = "encodeURL", property_description = "Encodes a string for use in a URL",
    property_type = DataType.STRING,
    property_return_type = DataType.STRING)
public class EncodeURL {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "String to encode")
      @NotEmpty
      String sourceString) {
    try {
      String encodedUrl = URLEncoder.encode(sourceString, StandardCharsets.UTF_8);
      return new StringValue(encodedUrl);
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during URL encoding: " + e.getMessage(), e);
    }
  }

}