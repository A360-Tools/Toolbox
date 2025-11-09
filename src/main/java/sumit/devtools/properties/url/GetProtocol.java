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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;


@BotCommand
@CommandPkg(label = "Get URL protocol", name = "getURLProtocol", description =
    "Extracts the protocol/scheme from a " +
        "URL",
    group_label = "URL",
    icon = "Url.svg", node_label = "Get protocol from {{sourceString}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign protocol to",
    property_name = "getURLProtocol", property_description = "Extracts the protocol/scheme from a URL",
    property_type = DataType.STRING,
    property_return_type = DataType.STRING)
public class GetProtocol {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "URL string")
      @NotEmpty
      String sourceString) {
    try {
      URI uri = new URI(sourceString);
      String scheme = uri.getScheme();
      // Return empty if no scheme found
      return new StringValue(Objects.requireNonNullElse(scheme, ""));
    } catch (URISyntaxException e) {
      return new StringValue("");  // Return empty for invalid URLs
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while extracting protocol: " + e.getMessage(),
          e);
    }
  }

}