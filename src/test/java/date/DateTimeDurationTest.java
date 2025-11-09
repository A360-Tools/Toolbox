package date;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZonedDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.CalculateDateTimeDuration;

/**
 * @author Sumit Kumar
 */
public class DateTimeDurationTest {

  @Test
  public void testDurationWithDaysHoursMinutesSeconds() {
    // Test with a duration that includes days, hours, minutes, and seconds
    ZonedDateTime startDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    ZonedDateTime endDate = ZonedDateTime.parse("2024-02-28T14:30:45+00:00");
    StringValue result = CalculateDateTimeDuration.action(startDate, endDate);
    Assert.assertEquals(result.get(), "4 days 4 hours 15 minutes 15 seconds");
  }

  @Test
  public void testDurationWithHoursMinutesSeconds() {
    // Test with a duration that includes only hours, minutes, and seconds
    ZonedDateTime startDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    ZonedDateTime endDate = ZonedDateTime.parse("2024-02-24T14:30:45+00:00");
    StringValue result = CalculateDateTimeDuration.action(startDate, endDate);
    Assert.assertEquals(result.get(), "4 hours 15 minutes 15 seconds");
  }

  @Test
  public void testDurationWithMinutesSeconds() {
    // Test with a duration that includes only minutes and seconds
    ZonedDateTime startDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    ZonedDateTime endDate = ZonedDateTime.parse("2024-02-24T10:30:45+00:00");
    StringValue result = CalculateDateTimeDuration.action(startDate, endDate);
    Assert.assertEquals(result.get(), "15 minutes 15 seconds");
  }

  @Test
  public void testDurationWithOnlySeconds() {
    // Test with a duration that includes only seconds
    ZonedDateTime startDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    ZonedDateTime endDate = ZonedDateTime.parse("2024-02-24T10:15:45+00:00");
    StringValue result = CalculateDateTimeDuration.action(startDate, endDate);
    Assert.assertEquals(result.get(), "15 seconds");
  }

  @Test
  public void testZeroDuration() {
    // Test with identical start and end dates
    ZonedDateTime startDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    ZonedDateTime endDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    StringValue result = CalculateDateTimeDuration.action(startDate, endDate);
    Assert.assertEquals(result.get(), "0 seconds");
  }

  @Test
  public void testNegativeDuration() {
    // Test with end date earlier than start date
    ZonedDateTime startDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    ZonedDateTime endDate = ZonedDateTime.parse("2024-02-23T10:15:30+00:00");
    StringValue result = CalculateDateTimeDuration.action(startDate, endDate);
    Assert.assertEquals(result.get(), "-1 day");
  }

  @Test
  public void testDifferentTimeZones() {
    // Test with dates in different time zones
    ZonedDateTime startDate = ZonedDateTime.parse("2024-02-24T10:15:30+00:00");
    ZonedDateTime endDate = ZonedDateTime.parse("2024-02-24T12:15:30+02:00");
    StringValue result = CalculateDateTimeDuration.action(startDate, endDate);
    Assert.assertEquals(result.get(), "0 seconds");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullStartDate() {
    // Test with null start date
    CalculateDateTimeDuration.action(null, ZonedDateTime.now());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullEndDate() {
    // Test with null end date
    CalculateDateTimeDuration.action(ZonedDateTime.now(), null);
  }

}