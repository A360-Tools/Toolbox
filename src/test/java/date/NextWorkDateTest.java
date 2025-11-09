package date;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.date.GetNextWorkDate;

public class NextWorkDateTest {

  // Helper method to create a DateTimeValue for a holiday
  private static DateTimeValue createHoliday(String date) {
    DateTimeValue holiday = new DateTimeValue();
    holiday.set(ZonedDateTime.parse(date + "T00:00:00Z"));
    return holiday;
  }

  @Test
  public void testNextWorkingDayFromFriday() {
    // Test the transition from Friday to the next Monday (no holidays)
    ZonedDateTime inputDate = ZonedDateTime.parse("2023-03-03T00:00:00Z"); // Friday
    DateTimeValue result = GetNextWorkDate.action(inputDate, new ArrayList<>());
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2023-03-06"); // Expect next Monday
  }

  @Test
  public void testNextWorkingDayFromSunday() {
    // Test the transition from Sunday to the next Monday (no holidays)
    ZonedDateTime inputDate = ZonedDateTime.parse("2023-03-05T00:00:00Z"); // Sunday
    DateTimeValue result = GetNextWorkDate.action(inputDate, new ArrayList<>());
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2023-03-06"); // Expect next Monday
  }

  @Test
  public void testNextWorkingDayBeforeHoliday() {
    // Test finding the next working day when the next day is a holiday
    ZonedDateTime inputDate = ZonedDateTime.parse(
        "2023-03-10T00:00:00Z"); // Friday before a Monday holiday
    List<DateTimeValue> holidays = List.of(createHoliday("2023-03-13")); // Holiday on Monday
    DateTimeValue result = GetNextWorkDate.action(inputDate, holidays);
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2023-03-14"); // Expect Tuesday after the holiday
  }

  @Test
  public void testNextWorkingDayAdjacentToWeekendAndHoliday() {
    // Test finding the next working day when the next days are weekend followed by a holiday
    ZonedDateTime inputDate = ZonedDateTime.parse("2023-12-29T00:00:00Z"); // Friday
    List<DateTimeValue> holidays = List.of(
        createHoliday("2024-01-01")); // Holiday on Tuesday after the weekend
    DateTimeValue result = GetNextWorkDate.action(inputDate, holidays);
    Assert.assertEquals(result.get().toString().substring(0, 10),
        "2024-01-02"); // Expect Wednesday after the
    // holiday
  }

}
