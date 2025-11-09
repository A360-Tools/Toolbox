package date;


import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZonedDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.GetLastDayOfMonth;

/**
 * @author Sumit Kumar
 */

public class GetLastDayOfMonthTest {

  @Test
  public void testRandomDayOfMonth() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-02-15T10:15:30+01:00[Europe/Paris]");
    DateTimeValue result = GetLastDayOfMonth.action(inputDate);
    ZonedDateTime expected = ZonedDateTime.parse(
        "2024-02-29T00:00:00+01:00[Europe/Paris]"); // Leap year
    Assert.assertEquals(result.get(), expected,
        "The last day of the month was not calculated correctly.");
  }

  @Test
  public void testAlreadyLastDayOfMonth() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-01-31T00:00:00+01:00[Europe/Paris]");
    DateTimeValue result = GetLastDayOfMonth.action(inputDate);
    Assert.assertEquals(result.get(), inputDate,
        "The last day of the month should be the same as input date.");
  }

  @Test
  public void testLastDayOfFebruaryNonLeapYear() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2023-02-15T10:15:30+01:00[Europe/Paris]");
    DateTimeValue result = GetLastDayOfMonth.action(inputDate);
    ZonedDateTime expected = ZonedDateTime.parse("2023-02-28T00:00:00+01:00[Europe/Paris]");
    Assert.assertEquals(result.get(), expected,
        "The last day of February in a non-leap year was not calculated " +
            "correctly.");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullInput() {
    GetLastDayOfMonth.action(null);
  }

}

