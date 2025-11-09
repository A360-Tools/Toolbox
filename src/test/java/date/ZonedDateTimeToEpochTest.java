package date;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.ConvertDateTimeToEpoch;

public class ZonedDateTimeToEpochTest {

  @Test
  public void testZonedDateTimeToEpoch() {
    // Test conversion at a specific time in UTC
    ZonedDateTime inputDate = ZonedDateTime.of(2024, 2, 24, 12, 0, 0, 0, ZoneId.of("UTC"));
    NumberValue result = ConvertDateTimeToEpoch.action(inputDate);
    Assert.assertEquals(result.get().longValue(), 1708776000000L,
        "The epoch timestamp should match the expected " +
            "value for 2024-02-24 12:00:00 UTC.");
  }

  @Test
  public void testZonedDateTimeToEpochWithDifferentTimeZone() {
    // Test conversion at the same instant in a different time zone
    ZonedDateTime inputDate = ZonedDateTime.of(2024, 2, 24, 7, 0, 0, 0,
        ZoneId.of("America/New_York"));
    NumberValue result = ConvertDateTimeToEpoch.action(inputDate);
    Assert.assertEquals(result.get().longValue(), 1708776000000L,
        "The epoch timestamp should match the expected" +
            " value for 2024-02-24 12:00:00 UTC (07:00:00 America/New_York).");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testZonedDateTimeToEpochWithNullInput() {
    // Test conversion with null input, expecting an exception
    ConvertDateTimeToEpoch.action(null);
  }

}
