package sumit.devtools.actions.security;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.CredentialAllowPassword;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.core.security.SecureString;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Generate TOTP",
    description = "Generates a Time-based One-Time Password (TOTP) based on RFC 6238",
    icon = "Totp.svg",
    name = "generateTOTP",
    group_label = "Security",
    node_label = "Generate TOTP with secret {{secret}} and assign to {{returnTo}}",
    return_description = "Time-based One-Time Password",
    return_required = true,
    multiple_returns = {
        @CommandPkg.Returns(return_label = "Current TOTP",
            return_name = "CurrentTOTP",
            return_description = "Currently valid TOTP code",
            return_type = DataType.STRING),
        @CommandPkg.Returns(return_label = "Next TOTP",
            return_name = "NextTOTP",
            return_description = "TOTP code for the next time period",
            return_type = DataType.STRING),
        @CommandPkg.Returns(return_label = "Seconds Remaining",
            return_name = "SecondsRemaining",
            return_description = "Seconds remaining until the current TOTP expires",
            return_type = DataType.NUMBER)
    }
)
public class GenerateTOTP {

  @Execute
  public static DictionaryValue action(
      @Idx(index = "1", type = AttributeType.CREDENTIAL)
      @Pkg(label = "Secret key", description = "Base32 encoded secret key")
      @NotEmpty
      @CredentialAllowPassword
      SecureString secret,

      @Idx(index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "SHA1", value = "SHA1")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "SHA256", value = "SHA256")),
          @Idx.Option(index = "2.3", pkg = @Pkg(label = "SHA512", value = "SHA512"))
      })
      @Pkg(label = "Algorithm", default_value = "SHA1", default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String algorithm,

      @Idx(index = "3", type = AttributeType.NUMBER)
      @Pkg(label = "Code digits", description = "Number of digits in the generated code (6-8)", default_value =
          "6", default_value_type = DataType.NUMBER)
      @NotEmpty
      Double codeDigits,

      @Idx(index = "4", type = AttributeType.NUMBER)
      @Pkg(label = "Time step", description = "Time step in seconds (usually 30)", default_value = "30",
          default_value_type = DataType.NUMBER)
      @NotEmpty
      Double timeStep
  ) {
    try {
      String secretString = secret.getInsecureString();
      if (secretString == null || secretString.trim().isEmpty()) {
        throw new BotCommandException("Secret key cannot be empty");
      }

      int digits = codeDigits.intValue();
      if (digits < 6 || digits > 8) {
        throw new BotCommandException("Code digits must be between 6 and 8");
      }

      int timeStepSeconds = timeStep.intValue();
      if (timeStepSeconds <= 0) {
        throw new BotCommandException("Time step must be positive");
      }

      // Convert the secret from Base32 to bytes
      byte[] secretBytes = base32Decode(secretString);

      // Get the current timestamp and calculate the counter value
      long currentTimestamp = Instant.now().getEpochSecond();
      long counter = currentTimestamp / timeStepSeconds;

      // Calculate seconds remaining in this time step
      long secondsRemaining = timeStepSeconds - (currentTimestamp % timeStepSeconds);

      // Generate the current TOTP
      String currentCode = generateTOTP(secretBytes, counter, digits, algorithm);

      // Generate the next TOTP (for the next time step)
      String nextCode = generateTOTP(secretBytes, counter + 1, digits, algorithm);

      // Prepare return values
      Map<String, Value> returnValue = new HashMap<>();
      returnValue.put("CurrentTOTP", new StringValue(currentCode));
      returnValue.put("NextTOTP", new StringValue(nextCode));
      returnValue.put("SecondsRemaining", new NumberValue(secondsRemaining));

      return new DictionaryValue(returnValue);
    } catch (Exception e) {
      throw new BotCommandException("Error generating TOTP: " + e.getMessage(), e);
    }
  }

  /**
   * Decode a Base32 string to a byte array Implementation based on RFC 4648
   */
  private static byte[] base32Decode(String base32) {
    // Strip spaces and convert to uppercase
    String cleaned = base32.toUpperCase().trim();

    // Before removing anything, check if the string contains invalid characters
    // Note: '=' is allowed as padding per RFC 4648
    for (char c : cleaned.toCharArray()) {
      if (!((c >= 'A' && c <= 'Z') || (c >= '2' && c <= '7') || c == ' ' || c == '=')) {
        throw new IllegalArgumentException("Invalid Base32 format: contains illegal characters");
      }
    }

    // Now remove all whitespace
    cleaned = cleaned.replaceAll("\\s", "");

    if (cleaned.isEmpty()) {
      throw new IllegalArgumentException("Empty Base32 string");
    }

    // Handle padding - find the first padding character
    int padIndex = cleaned.indexOf('=');
    if (padIndex >= 0) {  // Changed from padIndex > 0 to catch ANY padding, including at the start
      // IMPORTANT: Check for padding at the start FIRST
      if (padIndex == 0) {
        throw new IllegalArgumentException("Invalid Base32 format: padding cannot be at the start");
      }

      // Then check if all characters from padIndex onwards are padding
      String paddingPart = cleaned.substring(padIndex);
      if (!paddingPart.matches("=*")) {
        throw new IllegalArgumentException("Invalid Base32 format: data after padding character");
      }

      // Remove padding for processing
      cleaned = cleaned.substring(0, padIndex);
    }

    // Base32 character set (RFC 4648)
    final String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    // Calculate output length - each 8 chars of base32 gives 5 bytes
    int numBytes = (cleaned.length() * 5) / 8;
    byte[] result = new byte[numBytes];

    int buffer = 0;
    int bitsLeft = 0;
    int resultIndex = 0;

    for (char c : cleaned.toCharArray()) {
      int value = base32Chars.indexOf(c);
      if (value < 0) {
        throw new IllegalArgumentException("Invalid Base32 character: " + c);
      }

      // Add 5 bits to the buffer
      buffer = (buffer << 5) | value;
      bitsLeft += 5;

      // If we have at least 8 bits, write a byte
      if (bitsLeft >= 8) {
        bitsLeft -= 8;
        result[resultIndex++] = (byte) ((buffer >> bitsLeft) & 0xFF);
      }
    }

    return result;
  }

  /**
   * Generate a TOTP code
   */
  private static String generateTOTP(byte[] secret, long counter, int digits, String algorithm)
      throws NoSuchAlgorithmException, InvalidKeyException {

    // Convert counter to byte array
    byte[] counterBytes = new byte[8];
    for (int i = 7; i >= 0; i--) {
      counterBytes[i] = (byte) (counter & 0xff);
      counter >>= 8;
    }

    // Select the crypto algorithm
    String cryptoAlgorithm;
    switch (algorithm) {
      case "SHA1":
        cryptoAlgorithm = "HmacSHA1";
        break;
      case "SHA256":
        cryptoAlgorithm = "HmacSHA256";
        break;
      case "SHA512":
        cryptoAlgorithm = "HmacSHA512";
        break;
      default:
        throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
    }

    // Create the HMAC
    Mac mac = Mac.getInstance(cryptoAlgorithm);
    SecretKeySpec keySpec = new SecretKeySpec(secret, cryptoAlgorithm);
    mac.init(keySpec);
    byte[] hash = mac.doFinal(counterBytes);

    // Get the offset
    int offset = hash[hash.length - 1] & 0x0f;

    // Calculate binary code (RFC 4226)
    int binary = ((hash[offset] & 0x7f) << 24) |
        ((hash[offset + 1] & 0xff) << 16) |
        ((hash[offset + 2] & 0xff) << 8) |
        (hash[offset + 3] & 0xff);

    // Calculate the final code
    int code = binary % (int) Math.pow(10, digits);

    // Format the code to have correct number of digits (with leading zeros if needed)
    return String.format("%0" + digits + "d", code);
  }

}