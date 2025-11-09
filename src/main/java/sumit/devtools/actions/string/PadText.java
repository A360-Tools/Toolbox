package sumit.devtools.actions.string;

import static com.automationanywhere.commandsdk.model.DataType.STRING;

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
import org.apache.commons.lang3.StringUtils;

@BotCommand
@CommandPkg(label = "Pad Text", name = "padText", description = "Pads text on the left or right side with a specified string to reach a target length", group_label =
    "String",
    icon = "String.svg", node_label =
    "Pad {{padDirection}} with {{padString}} to length {{finalLength}} for " +
        "{{sourceText}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign padded text to")
public class PadText {

  @Execute
  public static Value<String> padText(

      @Idx(index = "1", type = AttributeType.RADIO, options = {
          @Idx.Option(index = "1.1", pkg = @Pkg(label = "Left", value = "LEFT")),
          @Idx.Option(index = "1.2", pkg = @Pkg(label = "Right", value = "RIGHT")),
      })
      @Pkg(label = "Pad Direction", default_value = "LEFT", default_value_type = STRING)
      @NotEmpty
      String padDirection,
      @Idx(index = "2", type = AttributeType.TEXT)
      @Pkg(label = "Original text")
      @NotEmpty
      String sourceText,

      @Idx(index = "3", type = AttributeType.TEXT)
      @Pkg(label = "Text to pad")
      @NotEmpty
      String padString,

      @Idx(index = "4", type = AttributeType.NUMBER)
      @Pkg(label = "Final length")
      @NotEmpty
      Number finalLength

  ) {
    try {
      String paddedText;
      if (padDirection.equalsIgnoreCase("Left")) {
        paddedText = StringUtils.leftPad(sourceText, finalLength.intValue(), padString);
      } else {
        paddedText = StringUtils.rightPad(sourceText, finalLength.intValue(), padString);
      }
      return new StringValue(paddedText);
    } catch (Exception e) {
      throw new BotCommandException("Error occurred during text padding. Error: " + e.getMessage(),
          e);
    }
  }

}
