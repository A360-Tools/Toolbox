package string;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.string.ReplaceTemplate;

public class HtmlTemplateReplacerTest {

  // Using renamed class ReplaceTemplate
  private Map<String, Value> dictionary;
  private String htmlTemplate;

  @BeforeMethod
  public void setUp() throws IOException {
    // ReplaceTemplate methods will be called directly
    dictionary = new HashMap<>();

    // Load the HTML template from a file
    // Alternatively, you could include the HTML as a string directly in the test
    htmlTemplate = new String(
        Files.readAllBytes(Paths.get("src/test/resources/email_template.html")));
  }

  @Test
  public void testHtmlTemplateReplacement() {
    // Setup - prepare dictionary with values to replace placeholders
    dictionary.put("{processName}", new StringValue("Invoice Processing Bot"));
    dictionary.put("{userName}", new StringValue("John Doe"));
    dictionary.put("{machineName}", new StringValue("DESKTOP-AB123"));
    dictionary.put("{startTime}", new StringValue("2023-05-10 14:30:45"));

    // Execute
    Value<String> result = ReplaceTemplate.action(htmlTemplate, dictionary);
    String processedTemplate = result.get();

    // Verify all placeholders are replaced
    Assert.assertFalse(processedTemplate.contains("{processName}"));
    Assert.assertFalse(processedTemplate.contains("{userName}"));
    Assert.assertFalse(processedTemplate.contains("{machineName}"));
    Assert.assertFalse(processedTemplate.contains("{startTime}"));

    // Verify specific replacements
    Assert.assertTrue(
        processedTemplate.contains("<title>Bot Started: Invoice Processing Bot</title>"));
    Assert.assertTrue(
        processedTemplate.contains("<div class=\"details-value\">Invoice Processing Bot</div>"));
    Assert.assertTrue(processedTemplate.contains("<div class=\"details-value\">John Doe</div>"));
    Assert.assertTrue(
        processedTemplate.contains("<div class=\"details-value\">DESKTOP-AB123</div>"));
    Assert.assertTrue(
        processedTemplate.contains("<div class=\"details-value\">2023-05-10 14:30:45</div>"));
  }

  @Test
  public void testHtmlTemplatePartialReplacement() {
    // Setup - only provide some of the required replacements
    dictionary.put("{processName}", new StringValue("Payroll Processing"));
    dictionary.put("{userName}", new StringValue("Jane Smith"));
    // Intentionally omit machineName and startTime

    // Execute
    Value<String> result = ReplaceTemplate.action(htmlTemplate, dictionary);
    String processedTemplate = result.get();

    // Verify provided placeholders are replaced
    Assert.assertFalse(processedTemplate.contains("{processName}"));
    Assert.assertFalse(processedTemplate.contains("{userName}"));

    // Verify omitted placeholders remain unchanged
    Assert.assertTrue(processedTemplate.contains("{machineName}"));
    Assert.assertTrue(processedTemplate.contains("{startTime}"));

    // Verify specific replacements
    Assert.assertTrue(processedTemplate.contains("<title>Bot Started: Payroll Processing</title>"));
    Assert.assertTrue(
        processedTemplate.contains("<div class=\"details-value\">Payroll Processing</div>"));
    Assert.assertTrue(processedTemplate.contains("<div class=\"details-value\">Jane Smith</div>"));
  }

  @Test
  public void testHtmlTemplateWithSpecialCharacters() {
    // Setup - include special characters in replacement values
    dictionary.put("{processName}", new StringValue("Data Extract & Transform"));
    dictionary.put("{userName}", new StringValue("O'Neill, James"));
    dictionary.put("{machineName}", new StringValue("SYSTEM-001<test>"));
    dictionary.put("{startTime}", new StringValue("2023-05-10 15:00 (UTC+2)"));

    // Execute
    Value<String> result = ReplaceTemplate.action(htmlTemplate, dictionary);
    String processedTemplate = result.get();

    // Verify all placeholders are replaced with special characters intact
    Assert.assertTrue(processedTemplate.contains("Data Extract & Transform"));
    Assert.assertTrue(processedTemplate.contains("O'Neill, James"));
    Assert.assertTrue(processedTemplate.contains("SYSTEM-001<test>"));
    Assert.assertTrue(processedTemplate.contains("2023-05-10 15:00 (UTC+2)"));
  }

  @Test
  public void testHtmlTemplateCaseSensitivity() {
    // Setup - test with case variations of the same placeholder
    dictionary.put("{processName}", new StringValue("Main Process"));
    dictionary.put("{PROCESSNAME}", new StringValue("UPPERCASE PROCESS"));
    dictionary.put("{ProcessName}", new StringValue("Proper Case Process"));

    // Add a modified version of the template with case variations
    String modifiedTemplate = htmlTemplate.replace("{processName}", "{PROCESSNAME}");
    modifiedTemplate = modifiedTemplate.replace("<div class=\"details-value\">{PROCESSNAME}</div>",
        "<div class=\"details-value\">{ProcessName}</div>");

    // Execute
    Value<String> result = ReplaceTemplate.action(modifiedTemplate, dictionary);
    String processedTemplate = result.get();

    // Verify case-sensitive replacements
    Assert.assertTrue(processedTemplate.contains("<title>Bot Started: UPPERCASE PROCESS</title>"));
    Assert.assertTrue(
        processedTemplate.contains("<div class=\"details-value\">Proper Case Process</div>"));
    Assert.assertFalse(processedTemplate.contains("Main Process"));
  }

  @Test
  public void testHtmlTemplateWithMultipleOccurrences() {
    // Setup - modify template to have multiple occurrences of the same placeholder
    String modifiedTemplate = htmlTemplate.replace("<h1>Bot Initiation Notification</h1>",
        "<h1>Bot Initiation: {processName}</h1>");

    dictionary.put("{processName}", new StringValue("Duplicate Test Process"));
    dictionary.put("{userName}", new StringValue("Test User"));
    dictionary.put("{machineName}", new StringValue("TEST-PC"));
    dictionary.put("{startTime}", new StringValue("2023-05-10 16:00:00"));

    // Execute
    Value<String> result = ReplaceTemplate.action(modifiedTemplate, dictionary);
    String processedTemplate = result.get();

    // Verify all occurrences are replaced
    Assert.assertTrue(
        processedTemplate.contains("<title>Bot Started: Duplicate Test Process</title>"));
    Assert.assertTrue(
        processedTemplate.contains("<h1>Bot Initiation: Duplicate Test Process</h1>"));
    Assert.assertTrue(
        processedTemplate.contains("<div class=\"details-value\">Duplicate Test Process</div>"));

    // Count occurrences to ensure all were replaced
    int occurrences = countOccurrences(processedTemplate, "Duplicate Test Process");
    Assert.assertEquals(occurrences, 3);
  }

  private int countOccurrences(String text, String substring) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(substring, index)) != -1) {
      count++;
      index += substring.length();
    }
    return count;
  }

}