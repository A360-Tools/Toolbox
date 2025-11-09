package sumit.devtools.properties.file;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.nio.file.Paths;

@BotCommand
@CommandPkg(label = "Get parent folder",
    name = "getParentFolder",
    description = "Gets parent directory of a file as a File type",
    group_label = "Path",
    icon = "Path.svg",
    node_label = "Get parent folder from {{filePath}} and assign to {{returnTo}}",
    return_type = DataType.FILE,
    return_required = true,
    return_label = "Assign parent folder to",
    property_name = "getParentFolder",
    property_description = "Gets parent directory of a file as a File type",
    property_type = DataType.FILE,
    property_return_type = DataType.FILE)

public class GetParentFile {

  @Execute
  public static Value<String> convert(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "File")
      @NotEmpty
      FileValue filePath) {
    try {
      String parentPath = Paths.get(filePath.get()).getParent().toString();
      return new FileValue(parentPath);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while extracting parent directory: " + e.getMessage(), e);
    }
  }

}