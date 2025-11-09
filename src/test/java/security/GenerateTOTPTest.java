package security;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.core.security.SecureString;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.security.GenerateTOTP;

/**
 * @author Sumit Kumar
 */
public class GenerateTOTPTest {

  // Test secret in Base32 format
  private static final String TEST_SECRET = "JBSWY3DPEHPK3PXP";

  @Test
  public void testBasicTOTPGeneration() {
    // Test basic TOTP generation with default parameters
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    DictionaryValue result = GenerateTOTP.action(secureSecret, "SHA1", 6.0, 30.0);
    Map<String, Value> resultMap = result.get();

    // Get the current TOTP
    StringValue currentTOTP = (StringValue) resultMap.get("CurrentTOTP");

    // Since TOTP is time-based, we can't predict the exact value in the test
    // Instead, verify the properties of the generated code
    Assert.assertNotNull(currentTOTP.get());
    Assert.assertEquals(currentTOTP.get().length(), 6);
    Assert.assertTrue(isNumeric(currentTOTP.get()), "TOTP should contain only numeric digits");

    // Get and verify the next TOTP
    StringValue nextTOTP = (StringValue) resultMap.get("NextTOTP");
    Assert.assertNotNull(nextTOTP.get());
    Assert.assertEquals(nextTOTP.get().length(), 6);
    Assert.assertTrue(isNumeric(nextTOTP.get()), "Next TOTP should contain only numeric digits");

    // Get and verify seconds remaining
    NumberValue secondsRemaining = (NumberValue) resultMap.get("SecondsRemaining");
    Assert.assertNotNull(secondsRemaining.get());
    Double seconds = secondsRemaining.get();
    Assert.assertTrue(seconds > 0 && seconds <= 30, "Seconds remaining should be between 1 and 30");
  }

  /**
   * Helper method to check if a string contains only numeric digits
   */
  private boolean isNumeric(String str) {
    return str.matches("\\d+");
  }

  @Test
  public void testDifferentAlgorithms() {
    // Test TOTP generation with different hash algorithms
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    DictionaryValue resultSHA1 = GenerateTOTP.action(secureSecret, "SHA1", 6.0, 30.0);
    DictionaryValue resultSHA256 = GenerateTOTP.action(secureSecret, "SHA256", 6.0, 30.0);
    DictionaryValue resultSHA512 = GenerateTOTP.action(secureSecret, "SHA512", 6.0, 30.0);

    // Verify all results have valid formats
    StringValue currentSHA1 = (StringValue) resultSHA1.get().get("CurrentTOTP");
    StringValue currentSHA256 = (StringValue) resultSHA256.get().get("CurrentTOTP");
    StringValue currentSHA512 = (StringValue) resultSHA512.get().get("CurrentTOTP");

    Assert.assertEquals(currentSHA1.get().length(), 6);
    Assert.assertEquals(currentSHA256.get().length(), 6);
    Assert.assertEquals(currentSHA512.get().length(), 6);

    Assert.assertTrue(isNumeric(currentSHA1.get()));
    Assert.assertTrue(isNumeric(currentSHA256.get()));
    Assert.assertTrue(isNumeric(currentSHA512.get()));

    // Codes generated with different algorithms should typically be different
    // But this isn't guaranteed for every time interval, so we don't assert this
  }

  @Test
  public void testDifferentCodeLengths() {
    // Test TOTP generation with different code lengths
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    DictionaryValue result6Digits = GenerateTOTP.action(secureSecret, "SHA1", 6.0, 30.0);
    DictionaryValue result7Digits = GenerateTOTP.action(secureSecret, "SHA1", 7.0, 30.0);
    DictionaryValue result8Digits = GenerateTOTP.action(secureSecret, "SHA1", 8.0, 30.0);

    // Verify code lengths match the requested lengths
    StringValue current6Digits = (StringValue) result6Digits.get().get("CurrentTOTP");
    StringValue current7Digits = (StringValue) result7Digits.get().get("CurrentTOTP");
    StringValue current8Digits = (StringValue) result8Digits.get().get("CurrentTOTP");

    Assert.assertEquals(current6Digits.get().length(), 6);
    Assert.assertEquals(current7Digits.get().length(), 7);
    Assert.assertEquals(current8Digits.get().length(), 8);

    Assert.assertTrue(isNumeric(current6Digits.get()));
    Assert.assertTrue(isNumeric(current7Digits.get()));
    Assert.assertTrue(isNumeric(current8Digits.get()));
  }

  @Test
  public void testDifferentTimeSteps() {
    // Test TOTP generation with different time steps
    // Note: These should generally produce different codes since they use different time intervals
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    DictionaryValue result30Seconds = GenerateTOTP.action(secureSecret, "SHA1", 6.0, 30.0);
    DictionaryValue result60Seconds = GenerateTOTP.action(secureSecret, "SHA1", 6.0, 60.0);

    // Both should be valid 6-digit codes
    StringValue current30Seconds = (StringValue) result30Seconds.get().get("CurrentTOTP");
    StringValue current60Seconds = (StringValue) result60Seconds.get().get("CurrentTOTP");

    Assert.assertEquals(current30Seconds.get().length(), 6);
    Assert.assertEquals(current60Seconds.get().length(), 6);

    Assert.assertTrue(isNumeric(current30Seconds.get()));
    Assert.assertTrue(isNumeric(current60Seconds.get()));
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testInvalidSecretFormat() {
    // Test with an invalid Base32 secret
    SecureString invalidSecret = new SecureString(
        "NOT!A!VALID!BASE32!SECRET".getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(invalidSecret, "SHA1", 6.0, 30.0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testInvalidAlgorithm() {
    // Test with an invalid hash algorithm (should be handled by SelectModes annotation,
    // but we test the code's error handling as well)
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(secureSecret, "INVALID_ALGORITHM", 6.0, 30.0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testInvalidCodeLength() {
    // Test with code length outside the valid range (6-8)
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(secureSecret, "SHA1", 9.0, 30.0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNegativeTimeStep() {
    // Test with a negative time step
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(secureSecret, "SHA1", 6.0, -30.0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testZeroTimeStep() {
    // Test with a zero time step
    SecureString secureSecret = new SecureString(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(secureSecret, "SHA1", 6.0, 0.0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullSecret() {
    // Test with null secret
    GenerateTOTP.action(null, "SHA1", 6.0, 30.0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testEmptySecret() {
    // Test with empty secret
    SecureString emptySecret = new SecureString("".getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(emptySecret, "SHA1", 6.0, 30.0);
  }

  @Test
  public void testShortSecret() {
    // Test with very short valid Base32 secret
    SecureString shortSecret = new SecureString("AAAA".getBytes(StandardCharsets.UTF_8));
    DictionaryValue result = GenerateTOTP.action(shortSecret, "SHA1", 6.0, 30.0);
    StringValue currentTOTP = (StringValue) result.get().get("CurrentTOTP");

    Assert.assertNotNull(currentTOTP.get());
    Assert.assertEquals(currentTOTP.get().length(), 6);
    Assert.assertTrue(isNumeric(currentTOTP.get()));
  }

  @Test
  public void testBase32WithPadding() {
    // Test with valid Base32 secret that includes padding characters
    // This is the same as TEST_SECRET but with padding characters
    String paddedSecret = "JBSWY3DPEHPK3PXP===="; // Added padding
    SecureString paddedSecureSecret = new SecureString(
        paddedSecret.getBytes(StandardCharsets.UTF_8));
    DictionaryValue result = GenerateTOTP.action(paddedSecureSecret, "SHA1", 6.0, 30.0);
    StringValue currentTOTP = (StringValue) result.get().get("CurrentTOTP");

    // The result should be the same as if we used the non-padded secret
    SecureString regularSecureSecret = new SecureString(
        TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    DictionaryValue regularResult = GenerateTOTP.action(regularSecureSecret, "SHA1", 6.0, 30.0);
    StringValue regularCurrentTOTP = (StringValue) regularResult.get().get("CurrentTOTP");

    Assert.assertEquals(currentTOTP.get(), regularCurrentTOTP.get(),
        "TOTP with padded secret should match TOTP with unpadded secret");
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*Invalid Base32 format: padding cannot be at the start.*")
  public void testPaddingAtStart() {
    // Test with padding characters at the start (invalid)
    String paddingAtStartSecret = "==JBSWY3DPEHPK3PXP";
    SecureString invalidSecret = new SecureString(
        paddingAtStartSecret.getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(invalidSecret, "SHA1", 6.0, 30.0);
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*Invalid Base32 format: data after padding character.*")
  public void testDataAfterPadding() {
    // Test with data after padding (invalid)
    String dataAfterPaddingSecret = "JBSWY===DPEHPK3PXP";
    SecureString invalidSecret = new SecureString(
        dataAfterPaddingSecret.getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(invalidSecret, "SHA1", 6.0, 30.0);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testOnlyPadding() {
    // Test with only padding characters
    String onlyPaddingSecret = "========";
    SecureString invalidSecret = new SecureString(
        onlyPaddingSecret.getBytes(StandardCharsets.UTF_8));
    GenerateTOTP.action(invalidSecret, "SHA1", 6.0, 30.0);
  }

}