package sumit.devtools.actions.date;

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Get Previous Working Date",
    description = "Gets previous working date (Monday to Friday are considered working days)",
    icon = "Calendar.svg",
    name = "getPreviousWorkDate",
    group_label = "Date",
    node_label = "Get previous working date from {{inputDate}} and assign to {{returnTo}}",
    return_label = "Assign previous working date to",
    return_description = "Datetime of the previous working day",
    return_required = true,
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.DATETIME)
public class GetPreviousWorkDate {

  @Execute
  public static DateTimeValue action(
      @Idx(index = "1", type = AttributeType.DATETIME)
      @Pkg(label = "Enter datetime value")
      @NotEmpty
      ZonedDateTime inputDate,

      @Idx(index = "2", type = AttributeType.LIST)
      @Pkg(label = "List of holiday dates")
      @ListType(DataType.DATETIME)
      List<DateTimeValue> holidayDates
  ) {
    try {
      holidayDates = Optional.ofNullable(holidayDates).orElse(new ArrayList<>());

      DateTimeValue result = new DateTimeValue();
      ZonedDateTime resultDT = inputDate;

      DayOfWeek dayOfWeek;
      do {
        resultDT = resultDT.minusDays(1);
        dayOfWeek = resultDT.getDayOfWeek();
      } while (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || isHoliday(
          resultDT,
          holidayDates));

      result.set(resultDT.truncatedTo(ChronoUnit.DAYS));

      return result;
    } catch (Exception e) {
      throw new BotCommandException("Failed to calculate the previous work date: " + e.getMessage(),
          e);
    }
  }

  private static boolean isHoliday(ZonedDateTime date, List<DateTimeValue> holidayDates) {
    for (DateTimeValue holidayValue : holidayDates) {
      if (holidayValue.get() != null) {
        ZonedDateTime holidayDate = holidayValue.get();
        if (date.toLocalDate().equals(holidayDate.toLocalDate())) {
          return true;
        }
      }
    }
    return false;
  }

}
