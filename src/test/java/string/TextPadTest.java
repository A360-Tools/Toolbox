package string;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.string.PadText;

/**
 * @author Claude
 */
public class TextPadTest {

  // Using renamed class PadText

  @BeforeMethod
  public void setUp() {
    // PadText methods will be called directly
  }

  @Test
  public void testLeftPadWithSingleCharacter() {
    // Test left padding with a single character
    String padDirection = "LEFT";
    String sourceText = "123";
    String padString = "0";
    Number finalLength = 5;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    Assert.assertEquals(result.get(), "00123");
  }

  @Test
  public void testRightPadWithSingleCharacter() {
    // Test right padding with a single character
    String padDirection = "RIGHT";
    String sourceText = "123";
    String padString = "0";
    Number finalLength = 5;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    Assert.assertEquals(result.get(), "12300");
  }

  @Test
  public void testLeftPadWithMultipleCharacters() {
    // Test left padding with a multi-character string
    String padDirection = "LEFT";
    String sourceText = "123";
    String padString = "ab";
    Number finalLength = 8;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    Assert.assertEquals(result.get(), "ababa123");
  }

  @Test
  public void testRightPadWithMultipleCharacters() {
    // Test right padding with a multi-character string
    String padDirection = "RIGHT";
    String sourceText = "123";
    String padString = "ab";
    Number finalLength = 8;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    Assert.assertEquals(result.get(), "123ababa");
  }

  @Test
  public void testPadToSmallerLength() {
    // Test when final length is smaller than source text length (should return original)
    String padDirection = "LEFT";
    String sourceText = "12345";
    String padString = "0";
    Number finalLength = 3;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    // StringUtils.leftPad doesn't truncate, it returns the original
    Assert.assertEquals(result.get(), "12345");
  }

  @Test
  public void testPadToEqualLength() {
    // Test when final length equals source text length
    String padDirection = "LEFT";
    String sourceText = "12345";
    String padString = "0";
    Number finalLength = 5;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    Assert.assertEquals(result.get(), "12345");
  }

  @Test
  public void testLeftPadEmptyString() {
    // Test left padding an empty string
    String padDirection = "LEFT";
    String sourceText = "";
    String padString = "x";
    Number finalLength = 5;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    Assert.assertEquals(result.get(), "xxxxx");
  }

  @Test
  public void testRightPadEmptyString() {
    // Test right padding an empty string
    String padDirection = "RIGHT";
    String sourceText = "";
    String padString = "x";
    Number finalLength = 5;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    Assert.assertEquals(result.get(), "xxxxx");
  }

  @Test
  public void testPadWithEmptyPadString() {
    // Test padding with an empty pad string
    String padDirection = "LEFT";
    String sourceText = "123";
    String padString = "";
    Number finalLength = 10;

    Value<String> result = PadText.padText(padDirection, sourceText, padString, finalLength);

    // StringUtils.leftPad with empty pad string uses space
    Assert.assertEquals(result.get(), "       123");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testExceptionHandling() {
    // Simulate an exception scenario
    PadText.padText(null, "123", "0", 5);
  }

}