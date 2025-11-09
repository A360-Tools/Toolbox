package date;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.GetPreviousWorkDate;

public class PreviousWorkDateTest {

  // Helper method to create a DateTimeValue for a holiday
  private static DateTimeValue createHoliday(String date) {
    DateTimeValue holiday = new DateTimeValue();
    holiday.set(ZonedDateTime.parse(date + "T00:00:00Z"));
    return holiday;
  }

  @Test
  public void testPreviousWorkingDayFromMonday() {
    // Test the transition from Monday to the previous Friday (no holidays)
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-01-08T00:00:00Z"); // Monday
    List<DateTimeValue> holidays = Collections.emptyList(); // No holidays
    DateTimeValue result = GetPreviousWorkDate.action(inputDate, holidays);
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2024-01-05"); // Expect previous Friday
  }

  @Test
  public void testPreviousWorkingDayFromMondayThroughHoliday() {
    // Test the transition from Monday through a holiday to the previous Friday
    ZonedDateTime inputDate = ZonedDateTime.parse(
        "2024-01-02T00:00:00Z"); // Tuesday after New Year's Day
    List<DateTimeValue> holidays = Collections.singletonList(
        createHoliday("2024-01-01")); // New Year's Day holiday
    DateTimeValue result = GetPreviousWorkDate.action(inputDate, holidays);
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2023-12-29"); // Expect the Friday before New
    // Year's
  }

  @Test
  public void testPreviousWorkingDayFromTuesday() {
    // Test the transition from Tuesday to Monday (no holidays)
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-01-09T00:00:00Z"); // Tuesday
    List<DateTimeValue> holidays = Collections.emptyList(); // No holidays
    DateTimeValue result = GetPreviousWorkDate.action(inputDate, holidays);
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2024-01-08"); // Expect previous Monday
  }

  @Test
  public void testPreviousWorkingDayThroughWeekend() {
    // Test finding the previous working day through the weekend
    ZonedDateTime inputDate = ZonedDateTime.parse("2024-01-08T00:00:00Z"); // Monday
    List<DateTimeValue> holidays = Collections.emptyList(); // No holidays
    DateTimeValue result = GetPreviousWorkDate.action(inputDate, holidays);
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2024-01-05"); // Expect the previous Friday
  }

}

