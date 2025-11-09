package string;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.string.ReplaceTemplate;

public class TemplateReplacerTest {

  // Using renamed class ReplaceTemplate
  private Map<String, Value> dictionary;

  @BeforeMethod
  public void setUp() {
    // ReplaceTemplate methods will be called directly
    dictionary = new HashMap<>();
  }

  @Test
  public void testBasicReplacementWithBraceFormat() {
    // Setup
    dictionary.put("{{name}}", new StringValue("John"));
    dictionary.put("{{age}}", new StringValue("30"));
    String template = "My name is {{name}} and I am {{age}} years old.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify
    Assert.assertEquals(result.get(), "My name is John and I am 30 years old.");
  }

  @Test
  public void testBasicReplacementWithAngleBracketFormat() {
    // Setup
    dictionary.put("<<name>>", new StringValue("John"));
    dictionary.put("<<age>>", new StringValue("30"));
    String template = "My name is <<name>> and I am <<age>> years old.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify
    Assert.assertEquals(result.get(), "My name is John and I am 30 years old.");
  }

  @Test
  public void testNoMatchingKeys() {
    // Setup
    dictionary.put("{{name}}", new StringValue("John"));
    dictionary.put("{{age}}", new StringValue("30"));
    String template = "This template has {{unknown}} placeholder.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - unmatched placeholder remains unchanged
    Assert.assertEquals(result.get(), "This template has {{unknown}} placeholder.");
  }

  @Test
  public void testMultipleOccurrencesOfSameKey() {
    // Setup
    dictionary.put("{{name}}", new StringValue("John"));
    String template = "{{name}} is my {{name}}. Hello {{name}}!";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - all occurrences should be replaced
    Assert.assertEquals(result.get(), "John is my John. Hello John!");
  }

  @Test
  public void testEmptyValue() {
    // Setup
    dictionary.put("{{name}}", new StringValue(""));
    String template = "My name is {{name}}.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - key should be replaced with empty string
    Assert.assertEquals(result.get(), "My name is .");
  }

  @Test
  public void testNullValue() {
    // Setup
    dictionary.put("{{name}}", null);
    String template = "My name is {{name}}.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - null should be handled and replaced with empty string
    Assert.assertEquals(result.get(), "My name is .");
  }

  @Test
  public void testMixedFormatPlaceholders() {
    // Setup - different placeholder formats in same template
    dictionary.put("{{name}}", new StringValue("John"));
    dictionary.put("<<age>>", new StringValue("30"));
    dictionary.put("$city$", new StringValue("New York"));
    String template = "{{name}} is <<age>> years old and lives in $city$.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - all formats should be replaced correctly
    Assert.assertEquals(result.get(), "John is 30 years old and lives in New York.");
  }

  @Test
  public void testCaseSensitivity() {
    // Setup
    dictionary.put("{{Name}}", new StringValue("John"));
    dictionary.put("{{NAME}}", new StringValue("DOE"));
    String template = "My name is {{Name}} and my surname is {{NAME}}.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - case sensitivity is maintained
    Assert.assertEquals(result.get(), "My name is John and my surname is DOE.");
  }

  @Test
  public void testSpecialCharactersInValue() {
    // Setup
    dictionary.put("{{special}}", new StringValue("$100 & 50€ (25%)"));
    String template = "The cost is {{special}}";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - special characters should be handled correctly
    Assert.assertEquals(result.get(), "The cost is $100 & 50€ (25%)");
  }

  @Test
  public void testPartialKeyMatch() {
    // Setup
    dictionary.put("{{name}}", new StringValue("John"));
    dictionary.put("{{fullname}}", new StringValue("John Doe"));
    String template = "My {{name}} and {{fullname}} with plain name text.";

    // Execute
    Value<String> result = ReplaceTemplate.action(template, dictionary);

    // Verify - only exact keys should be replaced
    Assert.assertEquals(result.get(), "My John and John Doe with plain name text.");
  }

}