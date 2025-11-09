package sumit.devtools.conditionals.file;

import static com.automationanywhere.commandsdk.model.AttributeType.FILE;
import static com.automationanywhere.commandsdk.model.AttributeType.NUMBER;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.ConditionTest;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.DataType;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sumit.devtools.utils.BotVerifiable;
import sumit.devtools.utils.CommandUtil;


@BotCommand(commandType = BotCommand.CommandType.Condition)
@CommandPkg(description = "Checks if file is available for write operation",
    name = "FileWritableValidation",
    label = "File available for write",
    node_label = "File {{sourceFilePath}} available for write till {{waitTimeSeconds}} seconds")
public class IsFileWritable {

  protected static final Logger LOGGER = LogManager.getLogger(IsFileWritable.class);

  @ConditionTest
  public static Boolean test(
      @Idx(index = "1", type = FILE) @Pkg(label = "File path")
      @NotEmpty
      @LocalFile
      String sourceFilePath,

      @Idx(index = "2", type = NUMBER)
      @Pkg(label = "How long you would like to wait for this condition to be true?(Seconds)",
          default_value = "0",
          default_value_type = DataType.NUMBER)
      @NotEmpty
      @GreaterThanEqualTo("0")
      Double waitTimeSeconds

  ) {
    try {
      File inputFile = new File(Paths.get(sourceFilePath).toString());
      if (!inputFile.isFile()) {
        throw new BotCommandException("Not a valid file. Please check path");
      }

      Map<String, Object> parameters = new HashMap<>();
      parameters.put("filePath", sourceFilePath);
      BotVerifiable fileCondition = IsFileWritable::validate;

      try {
        return CommandUtil.waitForCondition(waitTimeSeconds.intValue(), parameters, fileCondition);
      } catch (InterruptedException interruptedException) {
        throw new BotCommandException("Interrupted exception" + interruptedException.getMessage());
      }
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred during file writable validation. Error: " + e.getMessage(),
          e);
    }

  }

  private static boolean validate(Map<String, Object> parameters) {
    String filePath = (String) parameters.get("filePath");
    RandomAccessFile stream = null;
    try {
      File file = new File(Paths.get(filePath).toString());
      stream = new RandomAccessFile(file, "rw");
      return true;
    } catch (Exception e) {
      LOGGER.info("Error locking the file {}Exception {}", filePath, e);
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          LOGGER.error("Exception during closing file {}Exception {}", filePath, e);
        }
      }
    }
    return false;
  }

}