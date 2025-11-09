package list;

/**
 * @author Sumit Kumar
 */

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.list.SortList;

public class SortListTest {

  private List<Value> inputList;

  @BeforeMethod
  public void setUp() {
    inputList = Arrays.asList(
        new StringValue("Item3"),
        new StringValue("Item1"),
        new StringValue("Item2")
    );
  }

  @AfterMethod
  public void tearDown() {
    inputList = null;
  }

  @Test
  public void testSortListAscending() {
    // Sort the list in ascending order
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the list is sorted correctly
    List<String> sortedList = ((List<Value>) result.get()).stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.toList());
    Assert.assertEquals(sortedList, Arrays.asList("Item1", "Item2", "Item3"));
  }

  @Test
  public void testSortListDescending() {
    // Sort the list in descending order
    ListValue result = SortList.action(inputList, "DESCENDING", false);

    // Verify the list is sorted correctly
    List<String> sortedList = ((List<Value>) result.get()).stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.toList());
    Assert.assertEquals(sortedList, Arrays.asList("Item3", "Item2", "Item1"));
  }

  @Test
  public void testSortListWithDuplicates() {
    inputList = Arrays.asList(
        new StringValue("Item1"),
        new StringValue("Item2"),
        new StringValue("Item2"),
        new StringValue("Item3")
    );

    // Sort the list in ascending order
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the list is sorted correctly and maintains duplicates
    List<String> sortedList = ((List<Value>) result.get()).stream()
        .map(Value::get)
        .map(Object::toString)
        .collect(Collectors.toList());
    Assert.assertEquals(sortedList, Arrays.asList("Item1", "Item2", "Item2", "Item3"));
  }

  @Test
  public void testSortListSingleItem() {
    inputList = List.of(new StringValue("Item1"));

    // Sort the list with a single item in ascending order
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the list remains unchanged
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(result.get().get(0).toString(), "Item1");
  }

  @Test
  public void testSortListAscendingWithNumbers() {
    // Prepare the input list with NumberValue items
    inputList = Arrays.asList(
        new NumberValue(3),
        new NumberValue(1),
        new NumberValue(2)
    );

    // Sort the list in ascending order
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the list is sorted correctly
    List<Double> sortedList = ((List<Value>) result.get()).stream()
        .map(Value::get)
        .map(value -> Double.parseDouble(value.toString()))
        .collect(Collectors.toList());
    Assert.assertEquals(sortedList, Arrays.asList(1.0, 2.0, 3.0));
  }

  @Test
  public void testSortListDescendingWithNumbers() {
    // Prepare the input list with NumberValue items
    inputList = Arrays.asList(
        new NumberValue(1),
        new NumberValue(3),
        new NumberValue(2)
    );

    // Sort the list in descending order
    ListValue result = SortList.action(inputList, "DESCENDING", false);

    // Verify the list is sorted correctly
    List<Double> sortedList = ((List<Value>) result.get()).stream()
        .map(Value::get)
        .map(value -> Double.parseDouble(value.toString()))
        .collect(Collectors.toList());
    Assert.assertEquals(sortedList, Arrays.asList(3.0, 2.0, 1.0));
  }

  @Test
  public void testSortListWithDuplicateNumbers() {
    // Prepare the input list with duplicate NumberValue items
    inputList = Arrays.asList(
        new NumberValue(2),
        new NumberValue(1),
        new NumberValue(2),
        new NumberValue(3)
    );

    // Sort the list in ascending order
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the list is sorted correctly and maintains duplicates
    List<Double> sortedList = ((List<Value>) result.get()).stream()
        .map(Value::get)
        .map(value -> Double.parseDouble(value.toString()))
        .collect(Collectors.toList());
    Assert.assertEquals(sortedList, Arrays.asList(1.0, 2.0, 2.0, 3.0));
  }

  @Test
  public void testSortListSingleNumber() {
    // Prepare the input list with a single NumberValue item
    inputList = List.of(new NumberValue(1));

    // Sort the list with a single numeric value
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the list remains unchanged
    Assert.assertEquals(result.get().size(), 1);
    Assert.assertEquals(Double.parseDouble(result.get().get(0).toString()), 1.0);
  }

  @Test
  public void testSortListAscendingWithDateTimeValues() {
    // Prepare the input list with DateTimeValue items
    inputList = Arrays.asList(
        new DateTimeValue(ZonedDateTime.parse("2022-05-10T15:30:00Z")),
        new DateTimeValue(ZonedDateTime.parse("2022-05-08T10:00:00Z")),
        new DateTimeValue(ZonedDateTime.parse("2022-05-09T12:45:00Z"))
    );

    // Sort the list in ascending order
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Extract the ZonedDateTime values from the result for comparison
    List<ZonedDateTime> sortedZonedDateTimes = ((List<Value>) result.get()).stream()
        .map(value -> ((DateTimeValue) value).get())
        .collect(Collectors.toList());

    // Verify the list is sorted correctly by comparing ZonedDateTime values
    Assert.assertEquals(sortedZonedDateTimes.get(0), ZonedDateTime.parse("2022-05-08T10:00:00Z"));
    Assert.assertEquals(sortedZonedDateTimes.get(1), ZonedDateTime.parse("2022-05-09T12:45:00Z"));
    Assert.assertEquals(sortedZonedDateTimes.get(2), ZonedDateTime.parse("2022-05-10T15:30:00Z"));
  }

  @Test
  public void testSortListDescendingWithDateTimeValues() {
    // Prepare the input list with DateTimeValue items
    inputList = Arrays.asList(
        new DateTimeValue(
            ZonedDateTime.parse("2022-05-08T10:00:00Z", DateTimeFormatter.ISO_DATE_TIME)),
        new DateTimeValue(
            ZonedDateTime.parse("2022-05-10T15:30:00Z", DateTimeFormatter.ISO_DATE_TIME)),
        new DateTimeValue(
            ZonedDateTime.parse("2022-05-09T12:45:00Z", DateTimeFormatter.ISO_DATE_TIME))
    );

    // Sort the list in descending order
    ListValue result = SortList.action(inputList, "DESCENDING", false);

    // Verify the list is sorted correctly
    List<ZonedDateTime> sortedZonedDateTimes = ((List<Value>) result.get()).stream()
        .map(value -> ((DateTimeValue) value).get())
        .collect(Collectors.toList());
    Assert.assertEquals(sortedZonedDateTimes, Arrays.asList(
        ZonedDateTime.parse("2022-05-10T15:30:00Z"),
        ZonedDateTime.parse("2022-05-09T12:45:00Z"),
        ZonedDateTime.parse("2022-05-08T10:00:00Z")
    ));
  }

  @Test
  public void testSortListWithDuplicateDateTimeValues() {
    // Prepare the input list with duplicate DateTimeValue items
    inputList = Arrays.asList(
        new DateTimeValue(
            ZonedDateTime.parse("2022-05-09T12:45:00Z", DateTimeFormatter.ISO_DATE_TIME)),
        new DateTimeValue(
            ZonedDateTime.parse("2022-05-08T10:00:00Z", DateTimeFormatter.ISO_DATE_TIME)),
        new DateTimeValue(
            ZonedDateTime.parse("2022-05-09T12:45:00Z", DateTimeFormatter.ISO_DATE_TIME)),
        new DateTimeValue(
            ZonedDateTime.parse("2022-05-10T15:30:00Z", DateTimeFormatter.ISO_DATE_TIME))
    );

    // Sort the list in ascending order
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the list is sorted correctly and maintains duplicates
    List<ZonedDateTime> sortedZonedDateTimes = ((List<Value>) result.get()).stream()
        .map(value -> ((DateTimeValue) value).get())
        .collect(Collectors.toList());
    Assert.assertEquals(sortedZonedDateTimes, Arrays.asList(
        ZonedDateTime.parse("2022-05-08T10:00:00Z"),
        ZonedDateTime.parse("2022-05-09T12:45:00Z"),
        ZonedDateTime.parse("2022-05-09T12:45:00Z"),
        ZonedDateTime.parse("2022-05-10T15:30:00Z")
    ));
  }

  @Test
  public void testSortListSingleDateTimeValue() {
    // Prepare the input list with a single DateTimeValue item
    inputList = List.of(new DateTimeValue(ZonedDateTime.parse("2022-05-08T10:00:00Z",
        DateTimeFormatter.ISO_DATE_TIME)));

    // Sort the list with a single datetime value
    List<Value> result = SortList.action(inputList, "ASCENDING", false).get();

    // Verify the list remains unchanged
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(((DateTimeValue) result.get(0)).get(),
        ZonedDateTime.parse("2022-05-08T10:00:00Z"));
  }

  @Test
  public void testSortListEmpty() {
    inputList = List.of();

    // Sort an empty list
    ListValue result = SortList.action(inputList, "ASCENDING", false);

    // Verify the result is an empty list
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testSortListInvalidSortType() {
    // Attempt to sort the list with an invalid sort type
    SortList.action(inputList, "INVALID_TYPE", false);
  }

  @Test
  public void testCaseInsensitiveSortAscending() {
    // Test case-insensitive ascending sort (default)
    inputList = new ArrayList<>();
    inputList.add(new StringValue("apple"));
    inputList.add(new StringValue("Banana"));
    inputList.add(new StringValue("CHERRY"));
    inputList.add(new StringValue("date"));

    List<Value> result = SortList.action(inputList, "ASCENDING", false).get();

    // Should sort case-insensitively: apple, Banana, CHERRY, date
    Assert.assertEquals(result.size(), 4);
    Assert.assertEquals(result.get(0).get().toString(), "apple");
    Assert.assertEquals(result.get(1).get().toString(), "Banana");
    Assert.assertEquals(result.get(2).get().toString(), "CHERRY");
    Assert.assertEquals(result.get(3).get().toString(), "date");
  }

  @Test
  public void testCaseSensitiveSortAscending() {
    // Test case-sensitive ascending sort
    inputList = new ArrayList<>();
    inputList.add(new StringValue("apple"));
    inputList.add(new StringValue("Banana"));
    inputList.add(new StringValue("CHERRY"));
    inputList.add(new StringValue("date"));

    List<Value> result = SortList.action(inputList, "ASCENDING", true).get();

    // Should sort case-sensitively: uppercase before lowercase (CHERRY, Banana, apple, date)
    Assert.assertEquals(result.size(), 4);
    Assert.assertEquals(result.get(0).get().toString(), "Banana");
    Assert.assertEquals(result.get(1).get().toString(), "CHERRY");
    Assert.assertEquals(result.get(2).get().toString(), "apple");
    Assert.assertEquals(result.get(3).get().toString(), "date");
  }

}
