package url;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.url.BuildURL;

/**
 * @author Sumit Kumar
 */
public class BuildURLTest {

    private Map<String, Value> queryParams;

  @BeforeMethod
  public void setUp() {
    queryParams = new HashMap<>();
  }

  @Test
  public void testBuildURLWithBasicParameters() {
    // Setup basic parameters
    queryParams.put("param1", new StringValue("value1"));
    queryParams.put("param2", new StringValue("value2"));

    // Execute
    StringValue result = BuildURL.action("https://example.com", queryParams, false);

    // Verify
    Assert.assertEquals(result.get(), "https://example.com?param1=value1&param2=value2");
  }

  @Test
  public void testBuildURLWithEncodedParameters() {
    // Setup parameters with special characters
    queryParams.put("query", new StringValue("hello world"));
    queryParams.put("special", new StringValue("a=b&c"));

    // Execute with encoding enabled
    StringValue result = BuildURL.action("https://example.com/search", queryParams, true);

    // Get the result URL
    String resultUrl = result.get();
    System.out.println("Generated URL: " + resultUrl);

    // With URLEncoder.encode(), spaces should be encoded as '+' and special chars as percent-encoded
    Assert.assertTrue(resultUrl.contains("query=hello+world"),
        "URL should contain 'query=hello+world'");
    Assert.assertTrue(resultUrl.contains("special=a%3Db%26c"),
        "URL should contain 'special=a%3Db%26c'");
  }

  @Test
  public void testBuildURLWithExistingParameters() {
    // Setup additional parameters
    queryParams.put("param2", new StringValue("value2"));
    queryParams.put("param3", new StringValue("value3"));

    // Execute with a base URL that already has parameters
    StringValue result = BuildURL.action("https://example.com?param1=value1", queryParams, false);

    // Verify
    Assert.assertTrue(result.get().startsWith("https://example.com?param1=value1&"));
    Assert.assertTrue(result.get().contains("param2=value2"));
    Assert.assertTrue(result.get().contains("param3=value3"));
  }

  @Test
  public void testBuildURLWithNoParameters() {
    // Execute with empty parameters map
    StringValue result = BuildURL.action("https://example.com", new HashMap<>(), false);

    // Verify
    Assert.assertEquals(result.get(), "https://example.com");
  }

  @Test
  public void testBuildURLWithNullParameters() {
    // Execute with null parameters map
    StringValue result = BuildURL.action("https://example.com", null, false);

    // Verify
    Assert.assertEquals(result.get(), "https://example.com");
  }

  @Test
  public void testBuildURLWithComplexBaseURL() {
    // Setup parameters
    queryParams.put("param", new StringValue("value"));

    // Execute with a complex base URL including path and fragment
    StringValue result = BuildURL.action("https://example.com/path/to/resource#section",
        queryParams, false);

    // Verify
    Assert.assertEquals(result.get(), "https://example.com/path/to/resource?param=value#section");
  }

  @Test
  public void testBuildURLWithEmptyStringValues() {
    // Setup parameters with empty values
    queryParams.put("empty", new StringValue(""));
    queryParams.put("normal", new StringValue("value"));

    // Execute
    StringValue result = BuildURL.action("https://example.com", queryParams, false);

    // Verify
    Assert.assertTrue(result.get().contains("empty="));
    Assert.assertTrue(result.get().contains("normal=value"));
  }

  @Test
  public void testBuildURLWithNullValues() {
    // Setup parameters with null values
    queryParams.put("nullValue", null);
    queryParams.put("normal", new StringValue("value"));

    // Execute
    StringValue result = BuildURL.action("https://example.com", queryParams, false);

    // Verify - null values should be treated as empty strings
    Assert.assertTrue(result.get().contains("nullValue="));
    Assert.assertTrue(result.get().contains("normal=value"));
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testBuildURLWithInvalidBaseURL() {
    // Setup
    queryParams.put("param", new StringValue("value"));

    // Execute with invalid URL - should throw exception
    BuildURL.action("invalid\\url", queryParams, false);
  }

}