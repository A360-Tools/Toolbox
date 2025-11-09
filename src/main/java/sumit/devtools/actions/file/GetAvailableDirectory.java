package sumit.devtools.actions.file;

import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.FileFolder;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Sumit Kumar
 * Finds an available directory path by appending an incrementing counter if the directory already exists.
 */
@BotCommand
@CommandPkg(
    label = "Get Available Directory",
    description = "Finds an available directory path by appending an incrementing counter if the directory already exists",
    icon = "File.svg",
    name = "getAvailableDirectory",
    group_label = "File",
    node_label = "Get available directory for {{directoryName}} in {{parentPath}} and assign to {{returnTo}}",
    return_description = "Complete path to the available directory that can be created",
    return_required = true,
    return_label = "Assign available directory path to",
    return_type = DataType.FILE
)
public class GetAvailableDirectory {

  private static final String DEFAULT_SEPARATOR = "_";
  private static final int MAX_ATTEMPTS = 10000;

  @Execute
  public static FileValue action(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "Parent directory path", description = "Parent directory where the new directory will be created")
      @NotEmpty
      @FileFolder
      String parentPath,

      @Idx(index = "2", type = AttributeType.TEXT)
      @Pkg(label = "Directory name", description = "Desired directory name")
      @NotEmpty
      String directoryName
  ) {
    try {
      // Validate parent directory
      File parentDirectory = new File(parentPath);
      if (!parentDirectory.exists()) {
        throw new BotCommandException("Parent directory does not exist: " + parentPath);
      }
      if (!parentDirectory.isDirectory()) {
        throw new BotCommandException("Path is not a directory: " + parentPath);
      }

      // Validate directory name
      if (directoryName == null || directoryName.trim().isEmpty()) {
        throw new BotCommandException("Directory name cannot be null or empty");
      }

      // Find available directory name
      String availableDirectoryName = findAvailableDirectoryName(parentDirectory, directoryName.trim());

      // Construct full path
      Path fullPath = Paths.get(parentPath, availableDirectoryName);

      return new FileValue(fullPath.toString());

    } catch (BotCommandException e) {
      throw e;
    } catch (Exception e) {
      throw new BotCommandException("Error finding available directory: " + e.getMessage(), e);
    }
  }

  /**
   * Finds an available directory name by incrementing a counter if the directory already exists.
   *
   * @param parentDirectory The parent directory where the new directory will be created
   * @param directoryName   The desired directory name
   * @return An available directory name
   */
  private static String findAvailableDirectoryName(File parentDirectory, String directoryName) {
    // Check if original directory name is available
    File testDirectory = new File(parentDirectory, directoryName);
    if (!testDirectory.exists()) {
      return directoryName;
    }

    // Try incrementing until we find an available name
    for (int counter = 1; counter <= MAX_ATTEMPTS; counter++) {
      String newDirectoryName = directoryName + DEFAULT_SEPARATOR + counter;
      testDirectory = new File(parentDirectory, newDirectoryName);
      if (!testDirectory.exists()) {
        return newDirectoryName;
      }
    }

    // If we exhausted all attempts, throw an exception
    throw new BotCommandException(
        "Could not find available directory name after " + MAX_ATTEMPTS + " attempts for: " + directoryName
    );
  }
}
