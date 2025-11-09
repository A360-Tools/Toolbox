package sumit.devtools.actions.file;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Decode Base64 to file",
    description = "Decodes a Base64 string to a file",
    icon = "File.svg",
    name = "decodeBase64ToFile",
    group_label = "File",
    node_label = "Decode {{base64String}} to file {{outputFilePath}}"
)
public class DecodeBase64ToFile {

  // Buffer size for efficient streaming (8KB chunks)
  private static final int BUFFER_SIZE = 8192;

  @Execute
  public static void action(
      @Idx(index = "1", type = AttributeType.TEXTAREA)
      @Pkg(label = "Base64 string", description = "The Base64 encoded string to decode")
      @NotEmpty
      String base64String,

      @Idx(index = "2", type = AttributeType.FILE)
      @Pkg(label = "Output file path", description = "Path where the decoded file will be saved")
      @NotEmpty
      @LocalFile
      String outputFilePath,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Overwrite if file exists",
          description = "Controls what happens if the output file already exists. " +
              "When false (default): Fails with an error if file exists. " +
              "When true: Overwrites the existing file.",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      Boolean overwriteIfExists
  ) {
    try {
      // Validate the output file path
      File outputFile = new File(outputFilePath);

      // Check if file exists and overwrite is not enabled
      if (outputFile.exists() && (overwriteIfExists == null || !overwriteIfExists)) {
        throw new BotCommandException(
            "Output file already exists and overwrite option is not enabled");
      }

      // Ensure parent directories exist
      Path parentDir = Paths.get(outputFilePath).getParent();
      if (parentDir != null) {
        Files.createDirectories(parentDir);
      }

      // Use streaming approach for memory efficiency
      try (InputStream base64InputStream =
          new ByteArrayInputStream(base64String.getBytes(StandardCharsets.UTF_8));
          OutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {

        // Create a Base64 decoding input stream
        InputStream decodingStream = Base64.getDecoder().wrap(base64InputStream);

        // Transfer data in chunks
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = decodingStream.read(buffer)) != -1) {
          fileOutputStream.write(buffer, 0, bytesRead);
        }

        // Flush to ensure all data is written
        fileOutputStream.flush();
      }

    } catch (IOException e) {
      throw new BotCommandException("Error writing to file: " + e.getMessage(), e);
    } catch (IllegalArgumentException e) {
      throw new BotCommandException("Invalid Base64 string: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new BotCommandException("Error decoding Base64 to file: " + e.getMessage(), e);
    }
  }

}