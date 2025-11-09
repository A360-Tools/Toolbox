package sumit.devtools.actions.url;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Get query parameters",
    description = "Extracts query parameters from a URL and returns them as a dictionary",
    icon = "Url.svg",
    name = "getQueryParameters",
    group_label = "URL",
    node_label = "Extract query parameters from {{urlString}} and assign to {{returnTo}}",
    return_description = "Dictionary with query parameters",
    return_required = true,
    return_label = "Assign query parameters to",
    return_type = DataType.DICTIONARY)
public class GetQueryParameters {

  @Execute
  public static DictionaryValue action(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "URL string")
      @NotEmpty
      String urlString,

      @Idx(index = "2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Decode parameter names",
          description = "Decodes URL-encoded parameter names. " +
              "When false (default): Keeps names as-is (e.g., 'user%20name' remains 'user%20name'). " +
              "When true: Decodes names (e.g., 'user%20name' → 'user name').",
          default_value = "false",
          default_value_type = BOOLEAN)
      @NotEmpty
      Boolean decodeName,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Decode parameter values",
          description = "Decodes URL-encoded parameter values. " +
              "When false: Keeps values as-is (e.g., 'Hello%20World' remains 'Hello%20World'). " +
              "When true (default): Decodes values (e.g., 'Hello%20World' → 'Hello World').",
          default_value = "true",
          default_value_type = BOOLEAN)
      @NotEmpty
      Boolean decodeValues
  ) {
    Map<String, Value> queryParams = new LinkedHashMap<>();
    try {
      URI uri = new URI(urlString);
      String rawQuery = uri.getRawQuery();
      if (rawQuery != null) {
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
          String[] kv = pair.split("=", 2);
          if (kv[0].isBlank()) {
            continue;
          }
          String name = kv[0];
          String val = kv.length > 1 ? kv[1] : "";
          if (decodeName != null && decodeName) {
            name = URLDecoder.decode(name, UTF_8);
          }
          if (decodeValues != null && decodeValues) {
            val = URLDecoder.decode(val, UTF_8);
          }
          queryParams.put(name, new StringValue(val));
        }
      }
      return new DictionaryValue(queryParams);
    } catch (URISyntaxException e) {
      throw new BotCommandException("Invalid URL: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new BotCommandException("Error extracting query parameters: " + e.getMessage(), e);
    }
  }

}