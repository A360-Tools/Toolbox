package sumit.devtools.properties.csv;

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
@CommandPkg(label = "Unescape for CSV", name = "unescapeCSV", description =
    "Returns a String value for an unescaped " +
        "CSV column", group_label = "String",
    icon = "String.svg", node_label =
    "Unescape {{sourceString}} for CSV column and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign unescaped CSV to",
    property_name = "unescapeCSV", property_description = "Returns a String value for an unescaped CSV column",
    property_type = DataType.STRING,
    property_return_type = DataType.STRING)

public class UnescapeCSV {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXTAREA)
      @Pkg(label = "Source string")
      @NotEmpty
      String sourceString) {
    try {
      return new StringValue(StringEscapeUtils.unescapeCsv(sourceString));
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during CSV string unescape: " + e.getMessage(),
          e);

    }
  }

}

