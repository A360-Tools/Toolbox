package sumit.devtools.conditionals.file;

import static com.automationanywhere.commandsdk.model.AttributeType.FILE;
import static com.automationanywhere.commandsdk.model.AttributeType.NUMBER;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.commandsdk.model.DataType;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.ConditionTest;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.FileFolder;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sumit.devtools.utils.BotVerifiable;
import sumit.devtools.utils.CommandUtil;

/**
 * @author Sumit Kumar
 */
@BotCommand(commandType = BotCommand.CommandType.Condition)
@CommandPkg(
    description = "Checks if directory contains a file with specified extension",
    name = "hasFileWithExtension",
    label = "Directory: Has file with extension",
    icon = "File.svg",
    node_label = "If directory {{directoryPath}} {{conditionType}} file with extension {{extension}}"
)
public class HasFileWithExtension {

  protected static final Logger LOGGER = LogManager.getLogger(HasFileWithExtension.class);

  @ConditionTest
  public static Boolean test(
      @Idx(index = "1", type = FILE)
      @Pkg(label = "Directory path")
      @NotEmpty
      @FileFolder
      @LocalFile
      String directoryPath,

      @Idx(index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Has", value = "CONTAINS")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Does not have", value = "NOTCONTAINS"))
      })
      @Pkg(label = "Condition", default_value = "CONTAINS", default_value_type = STRING)
      @NotEmpty
      String conditionType,

      @Idx(index = "3", type = TEXT)
      @Pkg(label = "File extension",
          description = "File extension to search for (e.g., 'txt', 'pdf', 'xlsx'). Do not include the dot")
      @NotEmpty
      String extension,

      @Idx(index = "4", type = AttributeType.BOOLEAN)
      @Pkg(label = "Search recursively",
          description = "Controls whether to search in subdirectories. " +
              "When false (default): Searches only in the specified directory. " +
              "When true: Searches in the directory and all its subdirectories recursively.",
          default_value = "false",
          default_value_type = BOOLEAN)
      @NotEmpty
      Boolean searchRecursively,

      @Idx(index = "5", type = NUMBER)
      @Pkg(label = "How long you would like to wait for this condition to be true?(Seconds)",
          default_value = "0",
          default_value_type = DataType.NUMBER)
      @NotEmpty
      @GreaterThanEqualTo("0")
      Double waitTimeSeconds
  ) {
    try {
      File directory = new File(Paths.get(directoryPath).toString());
      if (!directory.exists()) {
        throw new BotCommandException("Directory does not exist: " + directoryPath);
      }
      if (!directory.isDirectory()) {
        throw new BotCommandException("Path is not a directory: " + directoryPath);
      }

      // Normalize extension (remove leading dot if present)
      String normalizedExtension = extension.startsWith(".") ? extension.substring(1) : extension;

      Map<String, Object> parameters = new HashMap<>();
      parameters.put("directoryPath", directoryPath);
      parameters.put("extension", normalizedExtension);
      parameters.put("searchRecursively", searchRecursively);
      parameters.put("conditionType", conditionType);

      BotVerifiable fileCondition = HasFileWithExtension::validate;

      try {
        return CommandUtil.waitForCondition(waitTimeSeconds.intValue(), parameters, fileCondition);
      } catch (InterruptedException interruptedException) {
        throw new BotCommandException("Interrupted exception: " + interruptedException.getMessage());
      }
    } catch (BotCommandException e) {
      throw e;
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking for file with extension. Error: " + e.getMessage(),
          e);
    }
  }

  private static boolean validate(Map<String, Object> parameters) {
    String directoryPath = (String) parameters.get("directoryPath");
    String extension = (String) parameters.get("extension");
    Boolean searchRecursively = (Boolean) parameters.get("searchRecursively");
    String conditionType = (String) parameters.get("conditionType");

    try {
      Path directory = Paths.get(directoryPath);
      boolean hasFile;

      if (searchRecursively) {
        // Search recursively in all subdirectories
        try (Stream<Path> paths = Files.walk(directory)) {
          hasFile = paths
              .filter(Files::isRegularFile)
              .anyMatch(path -> hasExtension(path, extension));
        }
      } else {
        // Search only in the specified directory (not subdirectories)
        try (Stream<Path> paths = Files.list(directory)) {
          hasFile = paths
              .filter(Files::isRegularFile)
              .anyMatch(path -> hasExtension(path, extension));
        }
      }

      // Return based on condition type
      if (conditionType.equalsIgnoreCase("CONTAINS")) {
        return hasFile;
      } else {
        return !hasFile;
      }
    } catch (Exception e) {
      LOGGER.error("Error checking directory for file with extension. Directory: {}, Extension: {}, Error: {}",
          directoryPath, extension, e.getMessage());
      return false;
    }
  }

  /**
   * Checks if a path has the specified extension using Apache Commons IO.
   *
   * @param path      The file path to check
   * @param extension The extension to match (without dot)
   * @return true if the file has the extension, false otherwise
   */
  private static boolean hasExtension(Path path, String extension) {
    String fileExtension = FilenameUtils.getExtension(path.toString());
    return fileExtension.equalsIgnoreCase(extension);
  }
}
