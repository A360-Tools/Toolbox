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
import org.apache.commons.text.WordUtils;

@BotCommand
@CommandPkg(label = "Change text case", name = "changeCase", description = "Changes text case to uppercase, lowercase, title case, or sentence case", group_label =
    "String",
    icon = "String.svg", node_label = "Change {{text}} to {{caseOption}} and assign to {{returnTo}}",
    return_type = DataType.STRING, return_required = true, return_label = "Assign changed text to")
public class ChangeCase {

  public static String convertToSentenceCase(String inputString) {
    if (inputString.isEmpty()) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    boolean terminalCharacterEncountered = true; // Set to true to capitalize the first character

    for (int i = 0; i < inputString.length(); i++) {
      char currentChar = inputString.charAt(i);

      if (terminalCharacterEncountered) {
        if (Character.isWhitespace(currentChar)) {
          result.append(currentChar);
        } else {
          result.append(Character.toUpperCase(currentChar));
          terminalCharacterEncountered = false;
        }
      } else {
        result.append(Character.toLowerCase(currentChar));
      }

      char[] terminalCharacters = {'.', '?', '!'};
      for (char terminalChar : terminalCharacters) {
        if (currentChar == terminalChar) {
          terminalCharacterEncountered = true;
          break;
        }
      }
    }

    return result.toString();
  }

  @Execute
  public static Value<String> changeCase(
      @Idx(index = "1", type = AttributeType.TEXT)
      @Pkg(label = "Text to modify")
      @NotEmpty
      String text,

      @Idx(index = "2", type = AttributeType.RADIO, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Uppercase", value = "UPPERCASE")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Lowercase", value = "LOWERCASE")),
          @Idx.Option(index = "2.3", pkg = @Pkg(label = "Title case", value = "TITLECASE")),
          @Idx.Option(index = "2.4", pkg = @Pkg(label = "Sentence case", value = "SENTENCECASE"))
      })
      @Pkg(label = "Case Option", default_value = "UPPERCASE", default_value_type = DataType.STRING)
      @NotEmpty
      String caseOption) {
    try {
      String changedText;
      switch (caseOption) {
        case "UPPERCASE":
          changedText = text.toUpperCase();
          break;
        case "LOWERCASE":
          changedText = text.toLowerCase();
          break;
        case "TITLECASE":
          changedText = WordUtils.capitalizeFully(text);
          break;
        case "SENTENCECASE":
          changedText = convertToSentenceCase(text);
          break;
        default:
          throw new BotCommandException("Invalid case option: " + caseOption);
      }
      return new StringValue(changedText);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during text case change. Error: " + e.getMessage(), e);
    }
  }

}

