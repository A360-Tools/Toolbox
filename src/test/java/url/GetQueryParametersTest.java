package url;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.url.GetQueryParameters;

/**
 * @author Sumit Kumar
 */
public class GetQueryParametersTest {

  @Test
  public void testExtractBasicQueryParameters() {
    // Execute
    DictionaryValue result = GetQueryParameters.action(
        "https://example.com/search?param1=value1&param2=value2", false, false);

    // Verify
    Map<String, Value> params = result.get();
    Assert.assertEquals(params.size(), 2);
    Assert.assertEquals(params.get("param1").toString(), "value1");
    Assert.assertEquals(params.get("param2").toString(), "value2");
  }

  @Test
  public void testExtractAndDecodeParameters() {
    // Execute with decoding enabled
    DictionaryValue result = GetQueryParameters.action(
        "https://example.com/search?query=hello+world&special=a%3Db%26c", false, true);

    // Verify
    Map<String, Value> params = result.get();
    Assert.assertEquals(params.size(), 2);
    Assert.assertEquals(params.get("query").toString(), "hello world");
    Assert.assertEquals(params.get("special").toString(), "a=b&c");
  }

  @Test
  public void testExtractParametersWithoutDecoding() {
    // Execute with decoding disabled
    DictionaryValue result = GetQueryParameters.action(
        "https://example.com/search?query=hello+world&special=a%3Db%26c", false, false);

    // Verify
    Map<String, Value> params = result.get();
    Assert.assertEquals(params.get("query").toString(), "hello+world");
    Assert.assertEquals(params.get("special").toString(), "a%3Db%26c");
  }

  @Test
  public void testURLWithNoQueryParameters() {
    // Execute
    DictionaryValue result = GetQueryParameters.action("https://example.com/path", false, false);

    // Verify
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testURLWithEmptyQueryString() {
    // Execute
    DictionaryValue result = GetQueryParameters.action("https://example.com?", false, false);

    // Verify
    Assert.assertTrue(result.get().isEmpty());
  }

  @Test
  public void testURLWithParametersWithoutValues() {
    // Execute
    DictionaryValue result = GetQueryParameters.action(
        "https://example.com?param1=&param2", false, false);

    // Verify
    Map<String, Value> params = result.get();
    Assert.assertEquals(params.size(), 2);
    Assert.assertEquals(params.get("param1").toString(), "");
    Assert.assertEquals(params.get("param2").toString(), "");
  }

  @Test
  public void testURLWithComplexParameters() {
    // Execute
    DictionaryValue result = GetQueryParameters.action(
        "https://example.com/path?id=123&filter[name]=test&sort=created:desc", false, false);

    // Verify
    Map<String, Value> params = result.get();
    Assert.assertEquals(params.size(), 3);
    Assert.assertEquals(params.get("id").toString(), "123");
    Assert.assertEquals(params.get("filter[name]").toString(), "test");
    Assert.assertEquals(params.get("sort").toString(), "created:desc");
  }

  @Test
  public void testURLWithRepeatedParameters() {
    // Execute - last value for a parameter should be used
    DictionaryValue result = GetQueryParameters.action(
        "https://example.com?param=value1&param=value2", false, false);

    // Verify
    Map<String, Value> params = result.get();
    Assert.assertEquals(params.size(), 1);
    Assert.assertEquals(params.get("param").toString(), "value2");
  }

  @Test
  public void testURLWithParametersAndFragment() {
    // Execute
    DictionaryValue result = GetQueryParameters.action(
        "https://example.com?param=value#section", false, false);

    // Verify - fragment should be ignored
    Map<String, Value> params = result.get();
    Assert.assertEquals(params.size(), 1);
    Assert.assertEquals(params.get("param").toString(), "value");
  }

}