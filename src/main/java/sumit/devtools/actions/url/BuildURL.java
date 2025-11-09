package sumit.devtools.actions.url;

import static com.automationanywhere.commandsdk.model.AttributeType.DICTIONARY;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;

import com.automationanywhere.botcommand.data.Value;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Build URL with parameters",
    description = "Builds a URL with query parameters from a base URL and a dictionary of parameters",
    icon = "Url.svg",
    name = "buildURL",
    group_label = "URL",
    node_label = "Build URL from {{baseUrl}} with parameters {{queryParams}} and assign to {{returnTo}}",
    return_description = "URL with query parameters",
    return_required = true,
    return_label = "Assign built URL to",
    return_type = DataType.STRING)
public class BuildURL {

  @Execute
  public static StringValue action(
      @Idx(index = "1", type = TEXT)
      @Pkg(label = "Base URL", description = "Base URL to append parameters to")
      @NotEmpty
      String baseUrl,

      @Idx(index = "2", type = DICTIONARY)
      @Pkg(label = "Query parameters", description = "Dictionary of query parameters to append")
      Map<String, Value> queryParams,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "URL-encode parameter values",
          description = "URL-encodes parameter values for safe transmission in URLs. " +
              "When false: Uses values as-is (e.g., 'Hello World' remains 'Hello World'). " +
              "When true (default): Encodes values (e.g., 'Hello World' → 'Hello%20World'). " +
              "Generally recommended to keep encoding enabled for special characters.",
          default_value = "true",
          default_value_type = BOOLEAN)
      @NotEmpty
      Boolean encodeValues
  ) {
    try {
      // If no query parameters provided, just return the base URL
      if (queryParams == null || queryParams.isEmpty()) {
        return new StringValue(baseUrl);
      }

      // Build the query string from the dictionary
      String queryString = queryParams.entrySet().stream()
          .map(entry -> {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";

            // Encode values if requested
            if (encodeValues != null && encodeValues) {
              value = URLEncoder.encode(value, StandardCharsets.UTF_8);
            }

            return key + "=" + value;
          })
          .collect(Collectors.joining("&"));

      // Parse the base URL
      URI baseUri = new URI(baseUrl);
      String scheme = baseUri.getScheme();
      String authority = baseUri.getAuthority();
      String path = baseUri.getPath();
      String fragment = baseUri.getFragment();
      String baseQuery = baseUri.getQuery();

      // Combine existing query with new query parameters
      String combinedQuery;
      if (baseQuery != null && !baseQuery.isEmpty()) {
        combinedQuery = baseQuery + "&" + queryString;
      } else {
        combinedQuery = queryString;
      }

      // Create the final URL string without double-encoding
      StringBuilder resultUrl = new StringBuilder();
      resultUrl.append(scheme).append("://").append(authority);

      if (path != null && !path.isEmpty()) {
        resultUrl.append(path);
      }

      if (!combinedQuery.isEmpty()) {
        resultUrl.append('?').append(combinedQuery);
      }

      if (fragment != null && !fragment.isEmpty()) {
        resultUrl.append('#').append(fragment);
      }

      return new StringValue(resultUrl.toString());

    } catch (URISyntaxException e) {
      throw new BotCommandException("Invalid base URL: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new BotCommandException("Error building URL: " + e.getMessage(), e);
    }
  }

}