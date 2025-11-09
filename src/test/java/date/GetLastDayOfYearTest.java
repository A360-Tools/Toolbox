package date;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZonedDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.GetLastDayOfYear;

public class GetLastDayOfYearTest {

  @Test
  public void testRandomDayOfYear() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-07-12T10:15:30+01:00[Europe/Paris]");
    DateTimeValue result = GetLastDayOfYear.action(inputDate);
    ZonedDateTime expected = ZonedDateTime.parse("2024-12-31T00:00:00+01:00[Europe/Paris]");
    Assert.assertEquals(result.get(), expected,
        "The last day of the year was not calculated correctly.");
  }

  @Test
  public void testAlreadyLastDayOfYear() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2023-12-31T00:00:00+01:00[Europe/Paris]");
    DateTimeValue result = GetLastDayOfYear.action(inputDate);
    Assert.assertEquals(result.get(), inputDate,
        "The last day of the year should be the same as input date.");
  }

  @Test
  public void testDifferentYear() {
    ZonedDateTime inputDate = ZonedDateTime.parse("2022-03-15T10:15:30+01:00[Europe/Paris]");
    DateTimeValue result = GetLastDayOfYear.action(inputDate);
    ZonedDateTime expected = ZonedDateTime.parse("2022-12-31T00:00:00+01:00[Europe/Paris]");
    Assert.assertEquals(result.get(), expected,
        "The last day of a different year was not calculated correctly.");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullInput() {
    GetLastDayOfYear.action(null);
  }

}
