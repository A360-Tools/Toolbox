package sumit.devtools.actions.date;

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
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Calculate Datetime Duration",
    description = "Calculates duration between two datetime values",
    icon = "Calendar.svg",
    name = "calculateDateTimeDuration",
    group_label = "Date",
    node_label = "Calculate duration between {{startDate}} and {{endDate}} and assign to {{returnTo}}",
    return_label = "Assign duration to",
    return_description = "Formatted duration between two dates",
    return_required = true,
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.STRING)
public class CalculateDateTimeDuration {

  @Execute
  public static StringValue action(
      @Idx(index = "1", type = AttributeType.DATETIME)
      @Pkg(label = "Start datetime")
      @NotEmpty
      ZonedDateTime startDate,

      @Idx(index = "2", type = AttributeType.DATETIME)
      @Pkg(label = "End datetime")
      @NotEmpty
      ZonedDateTime endDate
  ) {
    try {
      // Calculate the duration between the two dates
      Duration duration = Duration.between(startDate, endDate);

      // Format the duration as days, hours, minutes, and seconds
      long totalSeconds = Math.abs(duration.getSeconds());
      long days = totalSeconds / (24 * 3600);
      long remainingSeconds = totalSeconds % (24 * 3600);
      long hours = remainingSeconds / 3600;
      remainingSeconds = remainingSeconds % 3600;
      long minutes = remainingSeconds / 60;
      long seconds = remainingSeconds % 60;

      // Build the formatted string - only show non-zero components
      StringBuilder formattedDuration = new StringBuilder();
      boolean hasContent = false;

      if (days > 0) {
        if (hasContent) formattedDuration.append(" ");
        formattedDuration.append(days).append(" day").append(days != 1 ? "s" : "");
        hasContent = true;
      }

      if (hours > 0) {
        if (hasContent) formattedDuration.append(" ");
        formattedDuration.append(hours).append(" hour").append(hours != 1 ? "s" : "");
        hasContent = true;
      }

      if (minutes > 0) {
        if (hasContent) formattedDuration.append(" ");
        formattedDuration.append(minutes).append(" minute").append(minutes != 1 ? "s" : "");
        hasContent = true;
      }

      if (seconds > 0) {
        if (hasContent) formattedDuration.append(" ");
        formattedDuration.append(seconds).append(" second").append(seconds != 1 ? "s" : "");
        hasContent = true;
      }

      // If all components are zero, show "0 seconds"
      if (!hasContent) {
        formattedDuration.append("0 seconds");
      }

      // If the original duration was negative, add a negative sign
      if (duration.isNegative()) {
        formattedDuration.insert(0, "-");
      }

      return new StringValue(formattedDuration.toString());
    } catch (Exception e) {
      throw new BotCommandException("Failed to calculate duration between dates: " + e.getMessage(),
          e);
    }
  }

}