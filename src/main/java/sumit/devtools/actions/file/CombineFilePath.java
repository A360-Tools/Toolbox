package sumit.devtools.actions.file;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.FileFolder;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Combine to file path",
    description = "Combines a base path with path segments to create a file path variable.",
    icon = "File.svg",
    name = "combineToFilePath",
    group_label = "File",
    node_label = "Combine {{basePath}} with {{pathSegments}} and assign to {{returnTo}}",
    return_description = "Combined file path variable.",
    return_required = true,
    return_label = "Assign combined path to",
    return_type = DataType.FILE
)
public class CombineFilePath {

  @Execute
  public static FileValue action(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "Base path", description = "Base directory path.")
      @NotEmpty
      @FileFolder
      String basePath,

      @Idx(index = "2", type = AttributeType.LIST)
      @Pkg(label = "Path segments", description = "List of path segments to combine with base path.")
      @NotEmpty
      @ListType(DataType.STRING)
      List<Value> pathSegments
  ) {
    if (basePath == null || basePath.trim().isEmpty()) {
      throw new BotCommandException("Base path cannot be null or empty.");
    }
    if (pathSegments == null || pathSegments.isEmpty()) {
      throw new BotCommandException("Path segments list cannot be null or empty.");
    }

    try {
      // Start with the base path. This can throw InvalidPathException if basePath is malformed.
      Path resultPath = Paths.get(basePath);

      for (Value segmentValue : pathSegments) {
        if (segmentValue == null || segmentValue.get() == null) {
          // Skip null Value objects.
          continue;
        }
        String segment = segmentValue.get().toString();

        // Skip segments that are effectively empty.
        if (segment.trim().isEmpty()) {
          continue;
        }

        resultPath = resultPath.resolve(segment);
      }

      // Return the string representation of the combined path without explicit normalization.
      return new FileValue(resultPath.toString());

    } catch (InvalidPathException ipe) {
      // This exception is thrown if the path string cannot be converted to a Path
      // because it contains invalid characters or sequences for the file system.
      throw new BotCommandException(
          "The resulting path is invalid. Base path or a segment might contain illegal characters. "
              +
              "Offending input: '" + ipe.getInput() + "'. Reason: " + ipe.getReason(),
          ipe);
    } catch (Exception e) {
      // Catch-all for other unexpected issues
      throw new BotCommandException("Error combining file path: " + e.getMessage(), e);
    }
  }

}
