package date;

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZonedDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.GetFirstDayOfYear;

/**
 * @author Sumit Kumar
 */
public class GetFirstDayOfYearTest {

  @Test
  public void testMiddleOfYearDate() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-06-15T10:15:30+01:00[Europe/Paris]");
    DateTimeValue result = GetFirstDayOfYear.action(inputDate);
    ZonedDateTime expected = ZonedDateTime.parse("2024-01-01T00:00:00+01:00[Europe/Paris]");
    Assert.assertEquals(result.get(), expected,
        "The first day of the year was not calculated correctly.");
  }

  @Test
  public void testFirstOfYearDate() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-01-01T00:00:00+01:00[Europe/Paris]");
    DateTimeValue result = GetFirstDayOfYear.action(inputDate);
    Assert.assertEquals(result.get(), inputDate,
        "The first day of the year should be the same as input date.");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullInput() {
    GetFirstDayOfYear.action(null);
  }

}