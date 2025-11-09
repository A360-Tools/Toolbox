package sumit.devtools.actions.file;

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
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Get text from file",
    description = "Reads file content as text",
    icon = "File.svg",
    name = "getTextFromFile",
    group_label = "File",
    node_label = "Read text from {{filePath}} and assign to {{returnTo}}",
    return_description = "File content as text",
    return_required = true,
    return_label = "Assign file content to",
    return_type = DataType.STRING
)
public class GetTextFromFile {

  @Execute
  public static StringValue action(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "File path", description = "Path to the file to read")
      @NotEmpty
      String filePath,

      @Idx(index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "UTF-8", value = "UTF-8")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "ISO-8859-1", value = "ISO-8859-1")),
          @Idx.Option(index = "2.3", pkg = @Pkg(label = "US-ASCII", value = "US-ASCII")),
          @Idx.Option(index = "2.4", pkg = @Pkg(label = "UTF-16", value = "UTF-16")),
          @Idx.Option(index = "2.5", pkg = @Pkg(label = "UTF-16BE", value = "UTF-16BE")),
          @Idx.Option(index = "2.6", pkg = @Pkg(label = "UTF-16LE", value = "UTF-16LE")),
          @Idx.Option(index = "2.7", pkg = @Pkg(label = "UTF-32", value = "UTF-32")),
          @Idx.Option(index = "2.8", pkg = @Pkg(label = "UTF-32BE", value = "UTF-32BE")),
          @Idx.Option(index = "2.9", pkg = @Pkg(label = "UTF-32LE", value = "UTF-32LE"))
      })
      @Pkg(label = "Character Set", default_value = "UTF-8", default_value_type = DataType.STRING)
      @NotEmpty
      String charsetName,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Trim leading spaces",
          description = "Removes whitespace from the beginning of the file content. " +
              "When false (default): Keeps leading whitespace as-is. " +
              "When true: Removes all leading whitespace.",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      Boolean trimLeadingSpaces,

      @Idx(index = "4", type = AttributeType.BOOLEAN)
      @Pkg(label = "Trim trailing spaces",
          description = "Removes whitespace from the end of the file content. " +
              "When false (default): Keeps trailing whitespace as-is. " +
              "When true: Removes all trailing whitespace.",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      Boolean trimTrailingSpaces
  ) {
    try {
      // Validate file path
      File file = new File(filePath);
      if (!file.exists() || !file.isFile()) {
        throw new BotCommandException("File not found at specified path: " + filePath);
      }

      if (!file.canRead()) {
        throw new BotCommandException("Cannot read file (check permissions): " + filePath);
      }

      // Read file content with specified charset
      Charset charset = Charset.forName(charsetName);
      String content = Files.readString(Paths.get(filePath), charset);

      // Apply trimming if requested
      if (Boolean.TRUE.equals(trimLeadingSpaces) && Boolean.TRUE.equals(trimTrailingSpaces)) {
        content = content.strip();
      } else if (Boolean.TRUE.equals(trimLeadingSpaces)) {
        content = content.stripLeading();
      } else if (Boolean.TRUE.equals(trimTrailingSpaces)) {
        content = content.stripTrailing();
      }

      return new StringValue(content);
    } catch (Exception e) {
      throw new BotCommandException("Error reading file: " + e.getMessage(), e);
    }
  }

}