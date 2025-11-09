package sumit.devtools.properties.html;

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
import org.apache.commons.text.StringEscapeUtils;


@BotCommand
@CommandPkg(label = "Unescape for HTML", name = "unescapeHTML", description =
    "Unescapes a string containing entity " +
        "escapes to a string containing the actual Unicode characters corresponding to the escapes", group_label =
    "String",
    icon = "String.svg", node_label = "Unescape {{sourceString}} for HTML and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign unescaped HTML to",
    property_name = "unescapeHTML", property_description =
    "Unescapes a string containing entity escapes to a " +
        "string containing the actual Unicode characters corresponding to the escapes", property_type = DataType.STRING,
    property_return_type = DataType.STRING)

public class UnescapeHTML {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXTAREA)
      @Pkg(label = "Source string")
      @NotEmpty
      String sourceString) {
    try {
      return new StringValue(StringEscapeUtils.unescapeHtml4(sourceString));
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during HTML string unescape: " + e.getMessage(),
          e);

    }
  }

}

