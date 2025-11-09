package string;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.string.ChangeCase;

/**
 * @author Claude
 */
public class ChangeCaseTest {

  @Test
  public void testUppercase() {
    // Test uppercase transformation
    String inputText = "Hello World";
    String caseOption = "UPPERCASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "HELLO WORLD");
  }

  @Test
  public void testLowercase() {
    // Test lowercase transformation
    String inputText = "Hello World";
    String caseOption = "LOWERCASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "hello world");
  }

  @Test
  public void testTitleCase() {
    // Test title case transformation
    String inputText = "hello world example";
    String caseOption = "TITLECASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "Hello World Example");
  }

  @Test
  public void testSentenceCase() {
    // Test sentence case transformation
    String inputText = "hello world. another sentence. third one.";
    String caseOption = "SENTENCECASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "Hello world. Another sentence. Third one.");
  }

  @Test
  public void testUppercaseWithMixedCase() {
    // Test uppercase transformation with mixed case input
    String inputText = "HelLo WoRlD";
    String caseOption = "UPPERCASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "HELLO WORLD");
  }

  @Test
  public void testLowercaseWithMixedCase() {
    // Test lowercase transformation with mixed case input
    String inputText = "HelLo WoRlD";
    String caseOption = "LOWERCASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "hello world");
  }

  @Test
  public void testTitleCaseWithMixedCase() {
    // Test title case transformation with mixed case input
    String inputText = "hElLo wOrLd";
    String caseOption = "TITLECASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "Hello World");
  }

  @Test
  public void testSentenceCaseWithMultipleSentences() {
    // Test sentence case with multiple sentences
    String inputText = "first sentence. SECOND SENTENCE! third sentence?";
    String caseOption = "SENTENCECASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "First sentence. Second sentence! Third sentence?");
  }

  @Test
  public void testEmptyString() {
    // Test with empty string
    String inputText = "";
    String caseOption = "UPPERCASE";

    Value<String> result = ChangeCase.changeCase(inputText, caseOption);

    Assert.assertEquals(result.get(), "");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testInvalidCaseOption() {
    // Test with invalid case option
    String inputText = "Hello World";
    String caseOption = "INVALID_CASE";

    ChangeCase.changeCase(inputText, caseOption);
  }

}