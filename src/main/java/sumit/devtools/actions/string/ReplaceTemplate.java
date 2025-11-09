package sumit.devtools.actions.string;

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
import java.util.Map;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Replace Template",
    description = "Replaces occurrences of dictionary keys with their corresponding values in a text",
    icon = "String.svg",
    name = "replaceTemplate",
    group_label = "String",
    node_label = "Replace keys in {{templateString}} using {{dictionary}} and assign to {{returnTo}}",
    return_description = "Returns the string with replaced values",
    return_required = true,
    return_label = "Assign replaced text to",
    return_type = DataType.STRING)
public class ReplaceTemplate {

  @Execute
  public static Value<String> action(
      @Idx(index = "1", type = AttributeType.TEXTAREA)
      @Pkg(label = "Template text", description = "Text where dictionary keys will be replaced with values")
      @NotEmpty
      String templateString,

      @Idx(index = "2", type = AttributeType.DICTIONARY)
      @Pkg(label = "Dictionary with replacement values")
      @NotEmpty
      Map<String, Value> dictionary
  ) {
    try {
      String result = templateString;

      // Iterate through all keys in the dictionary
      for (Map.Entry<String, Value> entry : dictionary.entrySet()) {
        String key = entry.getKey();
        String replacement = getStringValue(entry.getValue());

        // Replace all occurrences of the key with its value
        result = result.replace(key, replacement);
      }

      return new StringValue(result);
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during text replacement: " + e.getMessage(), e);
    }
  }

  /**
   * Helper method to extract string value from different types of Value objects
   *
   * @param value The Value object to convert to a string
   * @return String representation of the value
   */
  private static String getStringValue(Value value) {
    if (value == null) {
      return "";
    }

    // For StringValue objects, we can directly get the string
    if (value instanceof StringValue) {
      return ((StringValue) value).get();
    }

    // For other types, convert to string representation
    return value.toString();
  }

}