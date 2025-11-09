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
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Get Previous Day Of Week",
    description = "Gets previous date of particular day of week",
    icon = "Calendar.svg",
    name = "getPreviousDayOfWeek",
    group_label = "Date",
    node_label = "Get previous {{daysOfWeek}} of {{inputDate}} and assign to {{returnTo}}",
    return_label = "Assign previous day of week to",
    return_description = "Datetime of the previous day of week",
    return_required = true,
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.DATETIME)
public class GetPreviousDayOfWeek {

  @Execute
  public static DateTimeValue action(

      @Idx(index = "1", type = AttributeType.DATETIME)
      @Pkg(label = "Enter datetime value")
      @NotEmpty
      ZonedDateTime inputDate,

      @Idx(index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "MONDAY", value = "MONDAY")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "TUESDAY", value = "TUESDAY")),
          @Idx.Option(index = "2.3", pkg = @Pkg(label = "WEDNESDAY", value = "WEDNESDAY")),
          @Idx.Option(index = "2.4", pkg = @Pkg(label = "THURSDAY", value = "THURSDAY")),
          @Idx.Option(index = "2.5", pkg = @Pkg(label = "FRIDAY", value = "FRIDAY")),
          @Idx.Option(index = "2.6", pkg = @Pkg(label = "SATURDAY", value = "SATURDAY")),
          @Idx.Option(index = "2.7", pkg = @Pkg(label = "SUNDAY", value = "SUNDAY"))
      })
      @Pkg(label = "Select day", default_value = "MONDAY", default_value_type = DataType.STRING)
      @NotEmpty
      String daysOfWeek,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Include same day if it matches",
          description = "Controls whether to include the input date if it already matches the target day of week. " +
              "When false (default): Returns strictly the previous occurrence (e.g., Input is Monday, target is Monday → Returns previous Monday, 7 days earlier). " +
              "When true: Returns the same day if it matches (e.g., Input is Monday, target is Monday → Returns the same Monday).",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean includeInputIfMatch

  ) {
    try {
      includeInputIfMatch = Optional.ofNullable(includeInputIfMatch).orElse(Boolean.FALSE);
      DateTimeValue result = new DateTimeValue();
      DayOfWeek dayOfWeek = DayOfWeek.valueOf(daysOfWeek.toUpperCase());

      ZonedDateTime resultDT;
      if (includeInputIfMatch) {
        resultDT = inputDate.with(TemporalAdjusters.previousOrSame(dayOfWeek));
      } else {
        resultDT = inputDate.with(TemporalAdjusters.previous(dayOfWeek));
      }

      result.set(resultDT.truncatedTo(ChronoUnit.DAYS));
      return result;
    } catch (Exception e) {
      throw new BotCommandException(
          "Failed to calculate the previous day of week: " + e.getMessage(), e);
    }

  }

}