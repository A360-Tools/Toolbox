package sumit.devtools.actions.date;

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Last day of year",
    description = "Finds the last day of the year for a given date",
    icon = "Calendar.svg",
    name = "lastDayOfYear",
    group_label = "Date",
    node_label = "Find last day of year for {{inputDate}} and assign to {{returnTo}}",
    return_label = "Assign last day of year to",
    return_description = "Datetime of the last day of the year",
    return_required = true,
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.DATETIME)
public class GetLastDayOfYear {

  @Execute
  public static DateTimeValue action(
      @Idx(index = "1", type = AttributeType.DATETIME)
      @Pkg(label = "Input datetime")
      @NotEmpty
      ZonedDateTime inputDate
  ) {
    try {
      // Adjust the input date to the last day of the year and remove the time part
      ZonedDateTime lastDayOfYear =
          inputDate.with(TemporalAdjusters.lastDayOfYear()).truncatedTo(ChronoUnit.DAYS);
      return new DateTimeValue(lastDayOfYear);
    } catch (Exception e) {
      throw new BotCommandException(
          "Failed to calculate the last day of the year: " + e.getMessage(), e);
    }
  }

}
