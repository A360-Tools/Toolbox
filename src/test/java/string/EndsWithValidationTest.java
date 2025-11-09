package string;

import com.automationanywhere.botcommand.exception.BotCommandException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.conditionals.string.StringEndsWith;

public class EndsWithValidationTest {

  // Using renamed class StringEndsWith

  @BeforeMethod
  public void setUp() {
    // StringEndsWith methods will be called directly
  }

  @Test
  public void testEndsWithMatchCase() {
    // Test with matching case
    Assert.assertTrue(StringEndsWith.validate("Hello World", "World", true));
    Assert.assertFalse(StringEndsWith.validate("Hello World", "world", true));

    // Test with suffix equal to the string
    Assert.assertTrue(StringEndsWith.validate("Hello", "Hello", true));

    // Test with longer suffix
    Assert.assertFalse(StringEndsWith.validate("Hello", "Prefix Hello", true));
  }

  @Test
  public void testEndsWithIgnoreCase() {
    // Test with case-insensitive matching
    Assert.assertTrue(StringEndsWith.validate("Hello World", "world", false));
    Assert.assertTrue(StringEndsWith.validate("Hello World", "WORLD", false));
    Assert.assertTrue(StringEndsWith.validate("HELLO WORLD", "world", false));

    // Test with suffix equal to the string (different case)
    Assert.assertTrue(StringEndsWith.validate("Hello", "HELLO", false));

    // Test with longer suffix
    Assert.assertFalse(StringEndsWith.validate("Hello", "Prefix Hello", false));
  }

  @Test
  public void testEndsWithEdgeCases() {
    // Test with empty suffix
    Assert.assertTrue(StringEndsWith.validate("Hello World", "", true));
    Assert.assertTrue(StringEndsWith.validate("Hello World", "", false));

    // Test with empty source string
    Assert.assertTrue(StringEndsWith.validate("", "", true));
    Assert.assertFalse(StringEndsWith.validate("", "suffix", true));
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullSourceString() {
    StringEndsWith.validate(null, "suffix", true);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullSuffix() {
    StringEndsWith.validate("Hello World", null, true);
  }

  @Test
  public void testSpecialCases() {
    // Test with special characters
    Assert.assertTrue(StringEndsWith.validate("Hello World!", "World!", true));
    Assert.assertTrue(StringEndsWith.validate("Hello $%^&*", "$%^&*", true));

    // Test with Unicode characters
    Assert.assertTrue(StringEndsWith.validate("Hello 你好", "你好", true));
    Assert.assertTrue(StringEndsWith.validate("こんにちは World", "World", true));
  }

}