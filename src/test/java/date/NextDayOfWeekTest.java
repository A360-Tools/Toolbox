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
import sumit.devtools.actions.date.GetNextDayOfWeek;

public class NextDayOfWeekTest {

  private ZonedDateTime testDate;

  @BeforeMethod
  public void setUp() {
    // Initialize test environment
    //Saturday
    testDate = ZonedDateTime.parse("2024-02-24T00:00:00Z", DateTimeFormatter.ISO_DATE_TIME);
  }

  @Test
  public void testActionNextMonday() {
    // Test for next Monday from a given date
    DateTimeValue result = GetNextDayOfWeek.action(testDate, "MONDAY", false);
    assertEquals(result.get().getDayOfWeek().toString(), "MONDAY",
        "The day of the week should be MONDAY.");
  }

  @Test
  public void testActionWithMatchSameDayTrue() {
    // Test when matchSameDay is true and inputDate is the desired day
    DateTimeValue result = GetNextDayOfWeek.action(testDate, "SATURDAY", true);
    assertEquals(result.get(), testDate.truncatedTo(ChronoUnit.DAYS),
        "The date should match the input date.");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testActionWithInvalidDayOfWeek() {
    // Test with an invalid day of the week
    GetNextDayOfWeek.action(testDate, "INVALID_DAY", false);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testActionWithNullInputDate() {
    // Test with null input date
    GetNextDayOfWeek.action(null, "MONDAY", false);
  }

}
