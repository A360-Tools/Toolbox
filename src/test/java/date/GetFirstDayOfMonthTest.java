package date;

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZonedDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.GetFirstDayOfMonth;

/**
 * @author Sumit Kumar
 */
public class GetFirstDayOfMonthTest {

  @Test
  public void testMiddleOfMonthDate() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-02-15T10:15:30+01:00[Europe/Paris]");
    DateTimeValue result = GetFirstDayOfMonth.action(inputDate);
    ZonedDateTime expected = ZonedDateTime.parse("2024-02-01T00:00:00+01:00[Europe/Paris]");
    Assert.assertEquals(result.get(), expected,
        "The first day of the month was not calculated correctly.");
  }

  @Test
  public void testFirstOfMonthDate() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-02-01T00:00:00+01:00[Europe/Paris]");
    DateTimeValue result = GetFirstDayOfMonth.action(inputDate);
    Assert.assertEquals(result.get(), inputDate,
        "The first day of the month should be the same as input date.");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullInput() {
    GetFirstDayOfMonth.action(null);
  }

}
