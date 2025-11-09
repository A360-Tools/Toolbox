/**
 * @author Sumit Kumar
 */

package sumit.devtools.actions.date;

import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.time.ZonedDateTime;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Convert Datetime To Epoch",
    description = "Converts ZonedDateTime to epoch timestamp",
    icon = "Calendar.svg",
    name = "convertDateTimeToEpoch",
    group_label = "Date",
    node_label = "Convert {{inputDate}} to epoch timestamp and assign to {{returnTo}}",
    return_label = "Assign epoch timestamp to",
    return_description = "Epoch timestamp",
    return_required = true,
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.NUMBER)
public class ConvertDateTimeToEpoch {

  @Execute
  public static NumberValue action(
      @Idx(index = "1", type = AttributeType.DATETIME)
      @Pkg(label = "Enter datetime value")
      @NotEmpty
      ZonedDateTime inputDate
  ) {
    try {
      long epochTimestamp = inputDate.toInstant().toEpochMilli();
      return new NumberValue(epochTimestamp);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while converting ZonedDateTime to epoch timestamp: " + e.getMessage(), e);
    }
  }

}