package sumit.devtools.properties.xml;

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
@CommandPkg(label = "Escape for XML", name = "escapeXML", description = "Escapes string for XML", group_label =
    "String",
    icon = "String.svg", node_label = "Escape {{sourceString}} for XML and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign escaped XML to",
    property_name = "escapeXML", property_description = "Escapes string for XML", property_type = DataType.STRING,
    property_return_type = DataType.STRING)

public class EscapeXML {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = TEXTAREA)
      @Pkg(label = "Source string")
      @NotEmpty
      String sourceString) {
    try {
      return new StringValue(StringEscapeUtils.escapeXml11(sourceString));
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during XML string escape: " + e.getMessage(),
          e);

    }
  }

}