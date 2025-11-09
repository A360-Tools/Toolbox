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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Encode file to Base64",
    description = "Encodes a file to a Base64 string",
    icon = "File.svg",
    name = "encodeFileToBase64",
    group_label = "File",
    node_label = "Encode {{filePath}} to Base64 and assign to {{returnTo}}",
    return_description = "Base64 encoded string representation of the file",
    return_required = true,
    return_label = "Assign encoded file to",
    return_type = DataType.STRING
)
public class EncodeFileToBase64 {

  // Buffer size for efficient streaming (8KB chunks)
  private static final int BUFFER_SIZE = 8192;

  @Execute
  public static StringValue action(
      @Idx(index = "1", type = AttributeType.FILE)
      @Pkg(label = "Select file to encode", description = "Path to the file that will be encoded")
      @NotEmpty
      String filePath
  ) {
    try {
      // Validate file path
      File file = new File(filePath);
      if (!file.exists() || !file.isFile()) {
        throw new BotCommandException("File not found at specified path: " + filePath);
      }

      // Use streaming approach with Base64 OutputStream for memory efficiency
      try (InputStream fileInputStream = new FileInputStream(file);
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
              (int) (file.length() * 1.4))) { //
        // Base64 encoding increases size by approximately 33%

        // Create a Base64 encoding output stream
        OutputStream base64OutputStream = Base64.getEncoder().wrap(outputStream);

        // Transfer data in chunks
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
          base64OutputStream.write(buffer, 0, bytesRead);
        }

        // Flush and close the Base64 stream to ensure all data is written
        base64OutputStream.flush();
        base64OutputStream.close();

        // Convert output stream to string
        return new StringValue(outputStream.toString(StandardCharsets.UTF_8));
      }

    } catch (IOException e) {
      throw new BotCommandException("Error reading file: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new BotCommandException("Error encoding file to Base64: " + e.getMessage(), e);
    }
  }

}