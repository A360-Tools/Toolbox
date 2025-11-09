package string;

import com.automationanywhere.botcommand.exception.BotCommandException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.conditionals.string.StringBeginsWith;

public class BeginsWithValidationTest {

  // Using renamed class StringBeginsWith

  @BeforeMethod
  public void setUp() {
    // StringBeginsWith methods will be called directly
  }

  @Test
  public void testBeginsWithMatchCase() {
    // Test with matching case
    Assert.assertTrue(StringBeginsWith.validate("Hello World", "Hello", true));
    Assert.assertFalse(StringBeginsWith.validate("Hello World", "hello", true));

    // Test with prefix equal to the string
    Assert.assertTrue(StringBeginsWith.validate("Hello", "Hello", true));

    // Test with longer prefix
    Assert.assertFalse(StringBeginsWith.validate("Hello", "Hello World", true));
  }

  @Test
  public void testBeginsWithIgnoreCase() {
    // Test with case-insensitive matching
    Assert.assertTrue(StringBeginsWith.validate("Hello World", "hello", false));
    Assert.assertTrue(StringBeginsWith.validate("Hello World", "HELLO", false));
    Assert.assertTrue(StringBeginsWith.validate("HELLO WORLD", "hello", false));

    // Test with prefix equal to the string (different case)
    Assert.assertTrue(StringBeginsWith.validate("Hello", "HELLO", false));

    // Test with longer prefix
    Assert.assertFalse(StringBeginsWith.validate("Hello", "Hello World", false));
  }

  @Test
  public void testBeginsWithEdgeCases() {
    // Test with empty prefix
    Assert.assertTrue(StringBeginsWith.validate("Hello World", "", true));
    Assert.assertTrue(StringBeginsWith.validate("Hello World", "", false));

    // Test with empty source string
    Assert.assertTrue(StringBeginsWith.validate("", "", true));
    Assert.assertFalse(StringBeginsWith.validate("", "prefix", true));
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullSourceString() {
    StringBeginsWith.validate(null, "prefix", true);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullPrefix() {
    StringBeginsWith.validate("Hello World", null, true);
  }

}