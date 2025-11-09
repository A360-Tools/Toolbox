package sumit.devtools.properties.file;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.FileValue;
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
import org.apache.commons.io.FilenameUtils;

@BotCommand
@CommandPkg(label = "Get file name with extension",
    name = "getFileName",
    description = "Gets file name with extension from a given file",
    group_label = "Path",
    icon = "Path.svg",
    node_label = "Get file name with extension from {{filePath}} and assign to {{returnTo}}",
    return_type = DataType.STRING,
    return_required = true,
    return_label = "Assign complete file name to",
    property_name = "getCompleteFileName",
    property_description = "Gets file name with extension from a given file",
    property_type = DataType.FILE,
    property_return_type = DataType.STRING)

public class GetFileNameWithExtension {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "File")
      @NotEmpty
      FileValue filePath) {
    try {
      return new StringValue(FilenameUtils.getName(filePath.get()));
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during 'file name with extension' extraction: " + e.getMessage(), e);
    }
  }

}