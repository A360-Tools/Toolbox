package regex;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.regex.MatchRegex;

/**
 * Test class for the updated RegexMatch action. Covers scenarios for full matches, numbered groups,
 * named groups, regex flags, and other options.
 */
public class RegexMatchTest {

  // Using renamed class MatchRegex

  @BeforeMethod
  public void setUp() {
    // MatchRegex methods will be called directly
  }

  @Test
  public void testSimpleMatch_FindAll_NoGroups() {
    String input = "cat dog cat";
    String pattern = "cat";
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, null);

    List<String> fullMatches = getFullMatches(result);
    Assert.assertEquals(fullMatches, Arrays.asList("cat", "cat"));

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 2);
    Assert.assertTrue(numberedGroups.get(0).isEmpty());
    Assert.assertTrue(numberedGroups.get(1).isEmpty());

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 2);
    Assert.assertTrue(namedGroups.get(0).isEmpty());
    Assert.assertTrue(namedGroups.get(1).isEmpty());
  }

  // Helper method to extract FullMatches list from the result DictionaryValue
  @SuppressWarnings("unchecked")
  private List<String> getFullMatches(DictionaryValue result) {
    ListValue<StringValue> fullMatchesListValue = (ListValue<StringValue>) result.get()
        .get("FullMatches");
    Assert.assertNotNull(fullMatchesListValue, "FullMatches list should not be null.");
    List<String> fullMatches = new ArrayList<>();
    if (fullMatchesListValue.get() != null) {
      for (Value v : fullMatchesListValue.get()) {
        fullMatches.add(v != null && v.get() != null ? v.get().toString() : "");
      }
    }
    return fullMatches;
  }

  // Helper method to extract NumberedGroupsByMatch list from the result DictionaryValue
  @SuppressWarnings("unchecked")
  private List<List<String>> getNumberedGroupsByMatch(DictionaryValue result) {
    ListValue<ListValue<StringValue>> numberedGroupsByMatchListValue =
        (ListValue<ListValue<StringValue>>) result.get().get("NumberedGroupsByMatch");
    Assert.assertNotNull(numberedGroupsByMatchListValue,
        "NumberedGroupsByMatch list should not be null.");
    List<List<String>> allNumberedGroups = new ArrayList<>();
    if (numberedGroupsByMatchListValue.get() != null) {
      for (Value vOuter : numberedGroupsByMatchListValue.get()) {
        ListValue<StringValue> innerListValue = (ListValue<StringValue>) vOuter;
        List<String> currentMatchNumberedGroups = new ArrayList<>();
        if (innerListValue != null && innerListValue.get() != null) {
          for (Value vInner : innerListValue.get()) {
            currentMatchNumberedGroups.add(vInner != null && vInner.get() != null ?
                vInner.get().toString() : "");
          }
        }
        allNumberedGroups.add(currentMatchNumberedGroups);
      }
    }
    return allNumberedGroups;
  }

  // --- Test Cases ---

  // Helper method to extract NamedGroupsByMatch list from the result DictionaryValue
  @SuppressWarnings("unchecked")
  private List<Map<String, String>> getNamedGroupsByMatch(DictionaryValue result) {
    ListValue<DictionaryValue> namedGroupsByMatchListValue = (ListValue<DictionaryValue>) result.get()
        .get(
            "NamedGroupsByMatch");
    Assert.assertNotNull(namedGroupsByMatchListValue,
        "NamedGroupsByMatch list should not be null.");
    List<Map<String, String>> allNamedGroups = new ArrayList<>();
    if (namedGroupsByMatchListValue.get() != null) {
      for (Value vOuter : namedGroupsByMatchListValue.get()) {
        DictionaryValue innerDictValue = (DictionaryValue) vOuter;
        Map<String, String> currentMatchNamedGroups = new LinkedHashMap<>();
        if (innerDictValue != null && innerDictValue.get() != null) {
          for (Map.Entry<String, Value> entry : innerDictValue.get().entrySet()) {
            currentMatchNamedGroups.put(entry.getKey(),
                entry.getValue() != null && entry.getValue().get() != null ?
                    entry.getValue().get().toString() : "");
          }
        }
        allNamedGroups.add(currentMatchNamedGroups);
      }
    }
    return allNamedGroups;
  }

  @Test
  public void testSimpleMatch_FindFirst_NoGroups() {
    String input = "cat dog cat";
    String pattern = "cat";
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_FIRST", null, false,
        false, false,
        false, false, false, false, null);

    List<String> fullMatches = getFullMatches(result);
    Assert.assertEquals(fullMatches, Collections.singletonList("cat"));
  }

  @Test
  public void testNoMatch() {
    String input = "cat dog bird";
    String pattern = "fish";
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, null);

    Assert.assertTrue(getFullMatches(result).isEmpty());
    Assert.assertTrue(getNumberedGroupsByMatch(result).isEmpty());
    Assert.assertTrue(getNamedGroupsByMatch(result).isEmpty());
  }

  @Test
  public void testEmptyInputString() {
    String input = "";
    String pattern = "cat";
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, null);
    Assert.assertTrue(getFullMatches(result).isEmpty());
  }

  @Test(expectedExceptions = BotCommandException.class, expectedExceptionsMessageRegExp =
      "Error during regex match" +
          " and group extraction: Invalid regular expression pattern:.*")
  public void testInvalidRegexPattern() {
    MatchRegex.action("abc", "[", "FIND_ALL", null, false, false, false, false, false, false,
        false, null);
  }

  @Test
  public void testMatchWithNumberedGroups_FindAll() {
    String input = "name:John age:30, name:Jane age:25";
    String pattern = "name:(\\w+) age:(\\d+)";
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, null);

    List<String> fullMatches = getFullMatches(result);
    Assert.assertEquals(fullMatches, Arrays.asList("name:John age:30", "name:Jane age:25"));

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 2);
    Assert.assertEquals(numberedGroups.get(0), Arrays.asList("John", "30"));
    Assert.assertEquals(numberedGroups.get(1), Arrays.asList("Jane", "25"));

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 2);
    Assert.assertTrue(namedGroups.get(0).isEmpty());
    Assert.assertTrue(namedGroups.get(1).isEmpty());
  }

  @Test
  public void testMatchWithNamedGroups_FindAll_ExtractSpecific() {
    String input = "user:john pass:123; user:jane pass:456";
    String pattern = "user:(?<username>\\w+) pass:(?<password>\\d+)";
    List<Value> namedToExtract = Arrays.asList(new StringValue("username"),
        new StringValue("password"));
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, namedToExtract);

    List<String> fullMatches = getFullMatches(result);
    Assert.assertEquals(fullMatches, Arrays.asList("user:john pass:123", "user:jane pass:456"));

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 2);
    Assert.assertEquals(numberedGroups.get(0), Arrays.asList("john", "123"));
    Assert.assertEquals(numberedGroups.get(1), Arrays.asList("jane", "456"));

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 2);
    Assert.assertEquals(namedGroups.get(0).get("username"), "john");
    Assert.assertEquals(namedGroups.get(0).get("password"), "123");
    Assert.assertEquals(namedGroups.get(1).get("username"), "jane");
    Assert.assertEquals(namedGroups.get(1).get("password"), "456");
  }

  @Test
  public void testMatchWithNamedGroups_ExtractOneSpecific() {
    String input = "name=Alice&age=30";
    String pattern = "name=(?<nameVal>[A-Za-z]+)&age=(?<ageVal>\\d+)";
    List<Value> namedToExtract = Collections.singletonList(new StringValue("nameVal"));
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, namedToExtract);

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 1);
    Assert.assertEquals(namedGroups.get(0).size(), 1);
    Assert.assertEquals(namedGroups.get(0).get("nameVal"), "Alice");
    Assert.assertFalse(namedGroups.get(0).containsKey("ageVal"));
  }


  @Test
  public void testMatchWithNamedGroups_ExtractNonExistent() {
    String input = "key:value";
    String pattern = "key:(?<actualName>\\w+)";
    List<Value> namedToExtract = Arrays.asList(new StringValue("actualName"),
        new StringValue("nonExistentName"));
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, namedToExtract);

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 1);
    Assert.assertEquals(namedGroups.get(0).get("actualName"), "value");
    Assert.assertEquals(namedGroups.get(0).get("nonExistentName"), "");
  }

  @Test
  public void testMatchWithNamedGroups_NoNamedGroupsToExtractSpecified() {
    String input = "user:john pass:123";
    String pattern = "user:(?<username>\\w+) pass:(?<password>\\d+)";
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, null);

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 1);
    Assert.assertTrue(namedGroups.get(0).isEmpty(),
        "Named groups map should be empty if no names are specified " +
            "for extraction.");
  }


  @Test
  public void testCaseInsensitiveFlag() {
    String input = "Apple APPLE apple";
    String pattern = "apple";
    DictionaryValue resultSensitive = MatchRegex.action(input, pattern, "FIND_ALL", null,
        false, false,
        false, false, false, false, false, null);
    Assert.assertEquals(getFullMatches(resultSensitive), Collections.singletonList("apple"));

    DictionaryValue resultInsensitive = MatchRegex.action(input, pattern, "FIND_ALL", null,
        true, false,
        false, false, false, false, false, null);
    Assert.assertEquals(getFullMatches(resultInsensitive),
        Arrays.asList("Apple", "APPLE", "apple"));
  }

  @Test
  public void testMultilineFlag() {
    String input = "first line\nsecond line\nthird line";
    String pattern = "^second line$";
    DictionaryValue resultNoMulti = MatchRegex.action(input, pattern, "FIND_ALL", null, false,
        false, false
        , false, false, false, false, null);
    Assert.assertTrue(getFullMatches(resultNoMulti).isEmpty());

    DictionaryValue resultMulti = MatchRegex.action(input, pattern, "FIND_ALL", null, false,
        true, false,
        false, false, false, false, null);
    Assert.assertEquals(getFullMatches(resultMulti), Collections.singletonList("second line"));
  }

  @Test
  public void testDotAllFlag() {
    String input = "one\ntwo";
    String patternDefault = "one.two";
    String patternDotAll = "one.two";

    DictionaryValue resultNoDotAll = MatchRegex.action(input, patternDefault, "FIND_ALL",
        null, false,
        false, false, false, false, false, false, null);
    Assert.assertTrue(getFullMatches(resultNoDotAll).isEmpty());

    DictionaryValue resultDotAll = MatchRegex.action(input, patternDotAll, "FIND_ALL", null,
        false, false,
        true, false, false, false, false, null);
    Assert.assertEquals(getFullMatches(resultDotAll), Collections.singletonList("one\ntwo"));
  }

  @Test
  public void testUnicodeCaseFlag() {
    String input = "straße Strasse";
    String pattern = "strasse";

    DictionaryValue resultCi = MatchRegex.action(input, pattern, "FIND_ALL", null,
        true, false, false, false, false, false, false, null);
    Assert.assertTrue(getFullMatches(resultCi).contains("Strasse"),
        "CASE_INSENSITIVE should find 'Strasse'");

    DictionaryValue resultUnicodeCi = MatchRegex.action(input, pattern, "FIND_ALL", null,
        true, false, false, true, false, false, false, null);
    Assert.assertTrue(getFullMatches(resultUnicodeCi).contains("Strasse"),
        "UNICODE_CASE should find 'Strasse'");
  }

  @Test
  public void testCommentsFlag() {
    String input = "item123";
    String patternWithComments = "item \\d+ # Match item followed by digits";

    DictionaryValue resultNoComments = MatchRegex.action(input, patternWithComments,
        "FIND_ALL", null,
        false, false, false, false, false, false, false, null);
    Assert.assertTrue(getFullMatches(resultNoComments).isEmpty());

    DictionaryValue resultComments = MatchRegex.action(input, patternWithComments, "FIND_ALL",
        null,
        false, false, false, false, true, false, false, null);
    Assert.assertEquals(getFullMatches(resultComments), Collections.singletonList("item123"));
  }


  @Test
  public void testTrimValuesOption() {
    String input = "  val1  end"; // Simplified input
    String pattern = "(\\s*val1\\s*)"; // Pattern captures spaces around val1

    // trimValues = false
    DictionaryValue resultNoTrim = MatchRegex.action(input, pattern, "FIND_ALL", null, false,
        false, false,
        false, false, false, false, null);
    List<String> fullMatchesNoTrim = getFullMatches(resultNoTrim);
    Assert.assertEquals(fullMatchesNoTrim, Collections.singletonList("  val1  "),
        "Full match (no trim)");
    List<List<String>> numberedGroupsNoTrim = getNumberedGroupsByMatch(resultNoTrim);
    Assert.assertEquals(numberedGroupsNoTrim.size(), 1, "Numbered groups count (no trim)");
    Assert.assertEquals(numberedGroupsNoTrim.get(0), Collections.singletonList("  val1  "),
        "Numbered group 1 (no" +
            " trim)");

    // trimValues = true
    DictionaryValue resultTrim = MatchRegex.action(input, pattern, "FIND_ALL", null, false,
        false, false,
        false, false, true, false, null);
    List<String> fullMatchesTrim = getFullMatches(resultTrim);
    Assert.assertEquals(fullMatchesTrim, Collections.singletonList("val1"), "Full match (trimmed)");
    List<List<String>> numberedGroupsTrim = getNumberedGroupsByMatch(resultTrim);
    Assert.assertEquals(numberedGroupsTrim.size(), 1, "Numbered groups count (trimmed)");
    Assert.assertEquals(numberedGroupsTrim.get(0), Collections.singletonList("val1"),
        "Numbered group 1 (trimmed)");
  }

  @Test
  public void testTrimValuesWithNamedGroups() {
    String input = "name:  John Doe  ; age:  30  ";
    String pattern = "name:\\s*(?<name>[^;]+?)\\s*;\\s*age:\\s*(?<age>\\d+)\\s*";
    List<Value> namedToExtract = Arrays.asList(new StringValue("name"), new StringValue("age"));

    DictionaryValue resultTrim = MatchRegex.action(input, pattern, "FIND_ALL", null,
        false, false, false, false, false, true, false, namedToExtract);

    List<String> fullMatchesTrim = getFullMatches(resultTrim);
    // The full match itself is also subject to trimming if the `trimValues` flag is true,
    // and the pattern `name:\s*(?<name>[^;]+?)\s*;\s*age:\s*(?<age>\d+)\s*` captures the surrounding spaces
    // as part of the full match.
    Assert.assertEquals(fullMatchesTrim, Collections.singletonList("name:  John Doe  ; age:  30"));

    List<Map<String, String>> namedGroupsTrim = getNamedGroupsByMatch(resultTrim);
    Assert.assertEquals(namedGroupsTrim.size(), 1);
    Assert.assertEquals(namedGroupsTrim.get(0).get("name"), "John Doe");
    Assert.assertEquals(namedGroupsTrim.get(0).get("age"), "30");
  }


  @Test
  public void testIncludeEmptyMatches_True() {
    String input = "a,,b,   ,c";
    String pattern = "([^,]*)(?:,|$)";

    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null,
        false, false, false, false, false, false, true, null);

    List<String> fullMatches = getFullMatches(result);
    // Expected: "a,", ",", "b,", "   ,", "c", "" (empty match at the end)
    Assert.assertEquals(fullMatches, Arrays.asList("a,", ",", "b,", "   ,", "c", ""),
        "Full matches with " +
            "includeEmpty=true, trim=false");

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 6);
    Assert.assertEquals(numberedGroups.get(0), Collections.singletonList("a"));
    Assert.assertEquals(numberedGroups.get(1), Collections.singletonList(""));
    Assert.assertEquals(numberedGroups.get(2), Collections.singletonList("b"));
    Assert.assertEquals(numberedGroups.get(3), Collections.singletonList("   "));
    Assert.assertEquals(numberedGroups.get(4), Collections.singletonList("c"));
    Assert.assertEquals(numberedGroups.get(5),
        Collections.singletonList("")); // For the last empty full match
  }

  @Test
  public void testIncludeEmptyMatches_False() {
    String input = "a,,b,   ,c";
    String pattern = "([^,]*)(?:,|$)";

    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null,
        false, false, false, false, false, false, false, null);

    List<String> fullMatches = getFullMatches(result);
    // Expected: "a,", ",", "b,", "   ,", "c" (empty full match at the end is skipped)
    // The full matches ",", "   ," are NOT blank themselves.
    Assert.assertEquals(fullMatches, Arrays.asList("a,", ",", "b,", "   ,", "c"),
        "Full matches with " +
            "includeEmpty=false, trim=false");

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 5);
    Assert.assertEquals(numberedGroups.get(0), Collections.singletonList("a"));
    Assert.assertEquals(numberedGroups.get(1), Collections.singletonList(""));
    Assert.assertEquals(numberedGroups.get(2), Collections.singletonList("b"));
    Assert.assertEquals(numberedGroups.get(3), Collections.singletonList("   "));
    Assert.assertEquals(numberedGroups.get(4), Collections.singletonList("c"));
  }

  @Test
  public void testIncludeEmptyMatches_True_WithTrim_True() {
    String input = "a,,b,   ,c";
    String pattern = "([^,]*)(?:,|$)";

    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null,
        false, false, false, false, false, true, true, null);

    List<String> fullMatches = getFullMatches(result);
    // After trimming, "   ," becomes ",". Empty full matches are still included.
    Assert.assertEquals(fullMatches, Arrays.asList("a,", ",", "b,", ",", "c", ""),
        "Full matches with " +
            "includeEmpty=true, trim=true");

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 6);
    Assert.assertEquals(numberedGroups.get(0), Collections.singletonList("a"));
    Assert.assertEquals(numberedGroups.get(1), Collections.singletonList(""));
    Assert.assertEquals(numberedGroups.get(2), Collections.singletonList("b"));
    Assert.assertEquals(numberedGroups.get(3),
        Collections.singletonList(""));  // "   " (group) becomes "" after
    // trim
    Assert.assertEquals(numberedGroups.get(4), Collections.singletonList("c"));
    Assert.assertEquals(numberedGroups.get(5),
        Collections.singletonList("")); // for the last empty full match
  }


  @Test
  public void testOptionalGroupNotMatched() {
    String input = "item:value1 item: value2";
    String pattern = "item:(?<value>\\w+)?";
    List<Value> namedToExtract = Collections.singletonList(new StringValue("value"));
    DictionaryValue result = MatchRegex.action(input, pattern, "FIND_ALL", null, false, false,
        false, false
        , false, false, false, namedToExtract);

    List<String> fullMatches = getFullMatches(result);
    Assert.assertEquals(fullMatches, Arrays.asList("item:value1", "item:"));

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 2);
    Assert.assertEquals(numberedGroups.get(0), Collections.singletonList("value1"));
    Assert.assertEquals(numberedGroups.get(1), Collections.singletonList(""));

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 2);
    Assert.assertEquals(namedGroups.get(0).get("value"), "value1");
    Assert.assertEquals(namedGroups.get(1).get("value"), "");
  }

  @Test
  public void testComplexPatternWithMultipleFlagsAndGroups() {
    String input = "Event: MEETING Date: 2023-10-26\nEvent: webinar Date: 2023-11-15";
    String pattern = "^event:\\s*(?<type>\\w+)\\s*Date:\\s*(?<date>\\d{4}-\\d{2}-\\d{2})$";
    List<Value> namedToExtract = Arrays.asList(new StringValue("type"), new StringValue("date"));

    DictionaryValue result = MatchRegex.action(input, pattern,
        "FIND_ALL", null,
        true,  // caseInsensitive
        true,  // multiline
        false, // dotAll
        true,  // unicodeCase
        false, // comments
        true,  // trimValues
        false, // includeEmptyMatches
        namedToExtract);

    List<String> fullMatches = getFullMatches(result);
    Assert.assertEquals(fullMatches,
        Arrays.asList("Event: MEETING Date: 2023-10-26", "Event: webinar Date: " +
            "2023-11-15"));

    List<List<String>> numberedGroups = getNumberedGroupsByMatch(result);
    Assert.assertEquals(numberedGroups.size(), 2);
    Assert.assertEquals(numberedGroups.get(0), Arrays.asList("MEETING", "2023-10-26"));
    Assert.assertEquals(numberedGroups.get(1), Arrays.asList("webinar", "2023-11-15"));

    List<Map<String, String>> namedGroups = getNamedGroupsByMatch(result);
    Assert.assertEquals(namedGroups.size(), 2);
    Assert.assertEquals(namedGroups.get(0).get("type"), "MEETING");
    Assert.assertEquals(namedGroups.get(0).get("date"), "2023-10-26");
    Assert.assertEquals(namedGroups.get(1).get("type"), "webinar");
    Assert.assertEquals(namedGroups.get(1).get("date"), "2023-11-15");
  }

}
