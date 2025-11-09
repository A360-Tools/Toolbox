package sumit.devtools.variables.file;

import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.VariableExecute;
import com.automationanywhere.commandsdk.model.DataType;
import java.io.File;
import org.apache.commons.lang3.SystemUtils;

@BotCommand(commandType = BotCommand.CommandType.Variable)
@CommandPkg(description =
    "Get user download directory path. Returns the standard path even if the directory doesn't " +
        "currently exist.",
    name = "UserDownloadDir", label = "User Download Directory",
    variable_return_type = DataType.FILE)
public class UserDownloadDirectory {

  @VariableExecute
  public static FileValue getUserDownloadDir() {
    try {
      String userHome = SystemUtils.USER_HOME;
      if (userHome == null || userHome.isEmpty()) {
        throw new BotCommandException(
            "Unable to determine user home directory (USER_HOME is not set).");
      }

      File downloadDir;
      if (SystemUtils.IS_OS_WINDOWS) {
        downloadDir = new File(userHome, "Downloads");
      } else if (SystemUtils.IS_OS_MAC) {
        downloadDir = new File(userHome, "Downloads");
      } else if (SystemUtils.IS_OS_LINUX) {
        String xdgDownloadsPath = System.getenv("XDG_DOWNLOAD_DIR");
        if (xdgDownloadsPath != null && !xdgDownloadsPath.isEmpty()) {
          downloadDir = new File(xdgDownloadsPath);
        } else {
          downloadDir = new File(userHome, "Downloads");
        }
      } else {
        downloadDir = new File(userHome, "Downloads");
      }

      return new FileValue(downloadDir.getAbsolutePath());

    } catch (Exception e) {
      throw new BotCommandException("Error retrieving user download directory: " + e.getMessage(),
          e);
    }
  }

}