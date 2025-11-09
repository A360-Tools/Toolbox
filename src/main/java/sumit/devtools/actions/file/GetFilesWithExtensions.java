package sumit.devtools.actions.file;

import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.FileFolder;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Get files with extensions",
    description = "Gets list of file paths matching specified extensions from a directory",
    icon = "File.svg",
    name = "getFilesWithExtensions",
    group_label = "File",
    node_label = "Get files from {{directoryPath}} and assign to {{returnTo}}",
    return_description = "List of file paths matching the criteria",
    return_required = true,
    return_label = "Assign file paths to",
    return_type = DataType.LIST,
    return_sub_type = DataType.FILE
)
public class GetFilesWithExtensions {

  @Execute
  public static ListValue<FileValue> action(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "Directory path")
      @NotEmpty
      @FileFolder
      @LocalFile
      String directoryPath,

      @Idx(index = "2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Search recursively",
          description = "Controls whether to search in subdirectories. " +
              "When false (default): Searches only in the specified directory. " +
              "When true: Searches in the directory and all its subdirectories recursively.",
          default_value = "false",
          default_value_type = BOOLEAN)
      @NotEmpty
      Boolean searchRecursively,

      @Idx(index = "3", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "3.1", pkg = @Pkg(label = "All files", value = "ALL")),
          @Idx.Option(index = "3.2", pkg = @Pkg(label = "Specific extensions", value = "SPECIFIC"))
      })
      @Pkg(label = "Files to include",
          default_value = "ALL",
          default_value_type = STRING)
      @NotEmpty
      @SelectModes
      String filterType,

      @Idx(index = "3.2.1", type = AttributeType.LIST)
      @Pkg(label = "File extensions",
          description = "Extensions to match (e.g., txt, pdf, xlsx). Do not include dots")
      @NotEmpty
      @ListType(STRING)
      List<StringValue> extensions
  ) {
    try {
      // Validate directory
      File directory = new File(Paths.get(directoryPath).toString());
      if (!directory.exists()) {
        throw new BotCommandException("Directory does not exist: " + directoryPath);
      }
      if (!directory.isDirectory()) {
        throw new BotCommandException("Path is not a directory: " + directoryPath);
      }

      // Normalize extensions (remove leading dots if present)
      final List<String> normalizedExtensions;
      if ("SPECIFIC".equalsIgnoreCase(filterType) && extensions != null && !extensions.isEmpty()) {
        normalizedExtensions = extensions.stream()
            .map(StringValue::get)
            .map(ext -> ext.startsWith(".") ? ext.substring(1) : ext)
            .collect(Collectors.toList());
      } else {
        normalizedExtensions = new ArrayList<>();
      }

      // Get files
      Path dirPath = Paths.get(directoryPath);
      List<Path> filePaths;

      if (searchRecursively) {
        // Search recursively in all subdirectories
        try (Stream<Path> paths = Files.walk(dirPath)) {
          filePaths = paths
              .filter(Files::isRegularFile)
              .collect(Collectors.toList());
        }
      } else {
        // Search only in the specified directory (not subdirectories)
        try (Stream<Path> paths = Files.list(dirPath)) {
          filePaths = paths
              .filter(Files::isRegularFile)
              .collect(Collectors.toList());
        }
      }

      // Filter by extensions if "SPECIFIC" is selected
      if ("SPECIFIC".equalsIgnoreCase(filterType) && !normalizedExtensions.isEmpty()) {
        filePaths = filePaths.stream()
            .filter(path -> matchesAnyExtension(path, normalizedExtensions))
            .collect(Collectors.toList());
      }

      // Convert to FileValue list
      List<Value> fileValueList = filePaths.stream()
          .map(path -> new FileValue(path.toAbsolutePath().toString()))
          .collect(Collectors.toList());

      ListValue<FileValue> resultList = new ListValue<>();
      resultList.set(fileValueList);
      return resultList;

    } catch (BotCommandException e) {
      throw e;
    } catch (IOException e) {
      throw new BotCommandException(
          "Error accessing directory or reading files: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while getting files with extensions: " + e.getMessage(), e);
    }
  }

  /**
   * Checks if a path matches any of the specified extensions (case-insensitive).
   *
   * @param path       The file path to check
   * @param extensions List of extensions to match (without dots)
   * @return true if the file matches any extension, false otherwise
   */
  private static boolean matchesAnyExtension(Path path, List<String> extensions) {
    String fileExtension = FilenameUtils.getExtension(path.toString());
    return extensions.stream()
        .anyMatch(ext -> ext.equalsIgnoreCase(fileExtension));
  }
}
