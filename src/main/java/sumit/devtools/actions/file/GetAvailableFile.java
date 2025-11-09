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
 * Finds an available file path by appending an incrementing counter if the file already exists.
 */
@BotCommand
@CommandPkg(
    label = "Get Available File",
    description = "Finds an available file path by appending an incrementing counter if the file already exists",
    icon = "File.svg",
    name = "getAvailableFile",
    group_label = "File",
    node_label = "Get available file for {{filename}} in {{directoryPath}} and assign to {{returnTo}}",
    return_description = "Complete path to the available file that can be created",
    return_required = true,
    return_label = "Assign available file path to",
    return_type = DataType.FILE
)
public class GetAvailableFile {

  private static final String DEFAULT_SEPARATOR = "_";
  private static final int MAX_ATTEMPTS = 10000;

  @Execute
  public static FileValue action(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "Directory path", description = "Parent directory where the file will be created")
      @NotEmpty
      @FileFolder
      String directoryPath,

      @Idx(index = "2", type = AttributeType.TEXT)
      @Pkg(label = "Filename", description = "Desired filename (can include extension)")
      @NotEmpty
      String filename,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Increment before extension",
          description = "Controls where the counter is placed in the filename. " +
              "When false (default): Appends counter after the entire filename (e.g., 'file.txt_1', 'file.txt_2'). " +
              "When true: Inserts counter before the extension (e.g., 'file_1.txt', 'file_2.txt').",
          default_value = "true",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean incrementBeforeExtension
  ) {
    try {
      // Validate directory
      File directory = new File(directoryPath);
      if (!directory.exists()) {
        throw new BotCommandException("Directory does not exist: " + directoryPath);
      }
      if (!directory.isDirectory()) {
        throw new BotCommandException("Path is not a directory: " + directoryPath);
      }

      // Validate filename
      if (filename == null || filename.trim().isEmpty()) {
        throw new BotCommandException("Filename cannot be null or empty");
      }

      // Find available file path
      String availableFilename = findAvailableFilename(directory, filename.trim(), incrementBeforeExtension);

      // Construct full path
      Path fullPath = Paths.get(directoryPath, availableFilename);

      return new FileValue(fullPath.toString());

    } catch (BotCommandException e) {
      throw e;
    } catch (Exception e) {
      throw new BotCommandException("Error finding available file: " + e.getMessage(), e);
    }
  }

  /**
   * Finds an available filename by incrementing a counter if the file already exists.
   *
   * @param directory               The parent directory where the file will be created
   * @param filename                The desired filename
   * @param incrementBeforeExtension Whether to place counter before extension
   * @return An available filename
   */
  private static String findAvailableFilename(File directory, String filename, boolean incrementBeforeExtension) {
    // Check if original filename is available
    File testFile = new File(directory, filename);
    if (!testFile.exists()) {
      return filename;
    }

    // Parse filename into base and extension
    FilenameParts parts = parseFilename(filename);

    // Try incrementing until we find an available name
    for (int counter = 1; counter <= MAX_ATTEMPTS; counter++) {
      String newFilename;

      if (incrementBeforeExtension && !parts.extension.isEmpty()) {
        // Insert counter before extension: file_1.txt
        newFilename = parts.baseName + DEFAULT_SEPARATOR + counter + parts.extension;
      } else {
        // Append counter after entire filename: file.txt_1
        newFilename = filename + DEFAULT_SEPARATOR + counter;
      }

      testFile = new File(directory, newFilename);
      if (!testFile.exists()) {
        return newFilename;
      }
    }

    // If we exhausted all attempts, throw an exception
    throw new BotCommandException(
        "Could not find available filename after " + MAX_ATTEMPTS + " attempts for: " + filename
    );
  }

  /**
   * Parses a filename into base name and extension.
   *
   * @param filename The filename to parse
   * @return FilenameParts containing base name and extension (with dot)
   */
  private static FilenameParts parseFilename(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');

    // No extension or hidden file (starts with dot)
    if (lastDotIndex <= 0) {
      return new FilenameParts(filename, "");
    }

    // Filename ends with just a dot (e.g., "file.") - treat as no extension
    if (lastDotIndex == filename.length() - 1) {
      return new FilenameParts(filename, "");
    }

    String baseName = filename.substring(0, lastDotIndex);
    String extension = filename.substring(lastDotIndex); // Include the dot

    return new FilenameParts(baseName, extension);
  }

  /**
   * Simple class to hold filename parts.
   */
  private static class FilenameParts {
    final String baseName;
    final String extension;

    FilenameParts(String baseName, String extension) {
      this.baseName = baseName;
      this.extension = extension;
    }
  }
}
