package date;

/**
 * @author Sumit Kumar
 */

import static org.testng.Assert.assertEquals;

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.GetPreviousDayOfWeek;

public class PreviousDayOfWeekTest {

  private ZonedDateTime testDate;

  @BeforeMethod
  public void setUp() {
    // Initialize test environment
    // Example date is a Sunday
    testDate = ZonedDateTime.parse("2024-02-24T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME);
  }

  @Test
  public void testActionPreviousMonday() {
    // Test for previous Monday from a given Sunday
    DateTimeValue result = GetPreviousDayOfWeek.action(testDate, "MONDAY", false);
    assertEquals(result.get().getDayOfWeek().toString(), "MONDAY",
        "The day of the week should be MONDAY.");
  }

  @Test
  public void testActionWithMatchSameDayTrue() {
    // Test when matchSameDay is true and the previous day is the same as inputDate
    DateTimeValue result = GetPreviousDayOfWeek.action(testDate.minusDays(6), "SUNDAY",
        true); // Subtract 6 days to
    // be on a Monday
    assertEquals(result.get().truncatedTo(ChronoUnit.DAYS),
        testDate.minusDays(6).truncatedTo(ChronoUnit.DAYS),
        "The date should match the same day of the previous week.");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testActionWithInvalidDayOfWeek() {
    // Test with an invalid day of the week
    GetPreviousDayOfWeek.action(testDate, "INVALID_DAY", false);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testActionWithNullInputDate() {
    // Test with null input date
    GetPreviousDayOfWeek.action(null, "MONDAY", false);
  }

}
