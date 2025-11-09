package sumit.devtools.actions.file;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Sumit Kumar
 * Sanitizes filenames by replacing invalid characters and handling platform-specific restrictions.
 */
@BotCommand
@CommandPkg(
    label = "Get sanitized filename",
    description =
        "Sanitizes a filename by replacing OS-specific invalid characters with a replacement character,"
            +
            " normalizing consecutive replacements, and removing leading/trailing replacement characters.",
    icon = "File.svg",
    name = "getSanitizedFileName",
    group_label = "File",
    node_label = "Sanitize {{fileName}} with {{replacementCharInput}} and assign to {{returnTo}}",
    return_description = "Sanitized filename",
    return_required = true,
    return_label = "Assign sanitized filename to",
    return_type = DataType.STRING
)
public class GetSanitizedFileName {

  // Invalid characters in filenames across most operating systems
  // Windows is most restrictive, so we use its invalid characters
  private static final String WINDOWS_INVALID_FILENAME_CHARS = "<>:\"/\\|?*\0\t\r\n";

  // Default replacement character
  private static final char DEFAULT_REPLACEMENT_CHAR = '_';

  // Windows reserved filenames (case-insensitive)
  private static final Set<String> WINDOWS_RESERVED_NAMES = Set.of(
      "CON", "PRN", "AUX", "NUL",
      "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
      "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
  );

  // Pre-compiled patterns for performance (compiled once, reused many times)
  private static final Pattern CONSECUTIVE_PATTERN_CACHE = Pattern.compile("(.)(\\1+)");
  private static final Pattern LEADING_TRAILING_WHITESPACE = Pattern.compile("^\\s+|\\s+$");

  @Execute
  public static StringValue action(
      @Idx(index = "1", type = AttributeType.TEXT)
      @Pkg(label = "Filename", description = "The filename to sanitize")
      @NotEmpty
      String fileName,

      @Idx(index = "2", type = AttributeType.TEXT)
      @Pkg(label = "Replacement character", description =
          "Character to replace invalid characters with. " +
              "Defaults to '_'. Only the first character is used. " +
              "If the specified character is invalid for filenames, '_' will be used instead.",
          default_value = "_", default_value_type = DataType.STRING)
      String replacementCharInput
  ) {
    try {
      // Validate input
      if (fileName == null || fileName.isEmpty()) {
        throw new BotCommandException("Filename cannot be null or empty");
      }

      // Determine replacement character
      char replacementChar = getValidReplacementChar(replacementCharInput);

      // Sanitize the filename
      String sanitized = sanitizeFilename(fileName, replacementChar);

      return new StringValue(sanitized);

    } catch (BotCommandException e) {
      throw e;
    } catch (Exception e) {
      throw new BotCommandException("Error sanitizing filename: " + e.getMessage(), e);
    }
  }

  /**
   * Gets a valid replacement character from the input string.
   * If the input is null, empty, or contains an invalid character, returns the default.
   *
   * @param replacementCharInput The replacement character input string
   * @return A valid replacement character
   */
  private static char getValidReplacementChar(String replacementCharInput) {
    if (replacementCharInput == null || replacementCharInput.isEmpty()) {
      return DEFAULT_REPLACEMENT_CHAR;
    }

    char proposedChar = replacementCharInput.charAt(0);

    // Check if the proposed character is invalid for filenames
    if (WINDOWS_INVALID_FILENAME_CHARS.indexOf(proposedChar) >= 0) {
      return DEFAULT_REPLACEMENT_CHAR;
    }

    return proposedChar;
  }

  /**
   * Main sanitization logic using a pipeline approach.
   *
   * @param filename        The filename to sanitize
   * @param replacementChar The replacement character to use
   * @return Sanitized filename
   */
  private static String sanitizeFilename(String filename, char replacementChar) {
    // Step 1: Trim leading/trailing whitespace
    String trimmed = LEADING_TRAILING_WHITESPACE.matcher(filename).replaceAll("");

    // Step 2: Handle edge cases
    if (trimmed.isEmpty() || isAllInvalidChars(trimmed)) {
      return String.valueOf(replacementChar);
    }

    if (isAllSameChar(trimmed, replacementChar)) {
      return String.valueOf(replacementChar);
    }

    // Step 3: Split into base name and extension
    FilenameParts parts = parseFilename(trimmed);

    // Step 4: Handle Windows reserved names
    if (isWindowsReservedName(parts.baseName) && parts.extension.isEmpty()) {
      return String.valueOf(replacementChar);
    }

    // Step 5: Sanitize each part
    String sanitizedBase = sanitizePart(parts.baseName, replacementChar);
    String sanitizedExt = parts.extension.isEmpty() ? "" :
        sanitizePart(parts.extension.substring(1), replacementChar); // Remove leading dot

    // Step 6: Handle empty results
    if (sanitizedBase.isEmpty()) {
      sanitizedBase = String.valueOf(replacementChar);
    }

    // Step 7: Reconstruct filename
    if (sanitizedExt.isEmpty() || parts.extension.isEmpty()) {
      return sanitizedBase;
    }

    return sanitizedBase + "." + sanitizedExt;
  }

  /**
   * Parses a filename into base name and extension.
   *
   * @param filename The filename to parse
   * @return FilenameParts containing base name and extension (with dot)
   */
  private static FilenameParts parseFilename(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');

    if (lastDotIndex <= 0) {
      // No extension or dot at the beginning (hidden file)
      return new FilenameParts(filename, "");
    }

    String baseName = filename.substring(0, lastDotIndex);
    String extension = filename.substring(lastDotIndex); // Include the dot

    return new FilenameParts(baseName, extension);
  }

  /**
   * Sanitizes a part of the filename (base name or extension).
   *
   * @param part            The part to sanitize
   * @param replacementChar The replacement character
   * @return Sanitized part
   */
  private static String sanitizePart(String part, char replacementChar) {
    // Step 1: Replace invalid characters
    StringBuilder sanitized = new StringBuilder(part.length());
    for (char c : part.toCharArray()) {
      if (WINDOWS_INVALID_FILENAME_CHARS.indexOf(c) >= 0) {
        sanitized.append(replacementChar);
      } else {
        sanitized.append(c);
      }
    }

    String result = sanitized.toString();

    // Step 2: Normalize consecutive replacement characters to a single one
    String quotedReplacement = Pattern.quote(String.valueOf(replacementChar));
    Pattern consecutivePattern = Pattern.compile(quotedReplacement + "{2,}");
    result = consecutivePattern.matcher(result).replaceAll(String.valueOf(replacementChar));

    // Step 3: Remove leading/trailing replacement characters
    Pattern leadingTrailingPattern = Pattern.compile(
        "^" + quotedReplacement + "+|" + quotedReplacement + "+$"
    );
    result = leadingTrailingPattern.matcher(result).replaceAll("");

    // Step 4: Trim whitespace
    result = LEADING_TRAILING_WHITESPACE.matcher(result).replaceAll("");

    return result;
  }

  /**
   * Checks if the filename is a Windows reserved name (case-insensitive).
   *
   * @param filename The filename to check
   * @return True if it's a Windows reserved name
   */
  private static boolean isWindowsReservedName(String filename) {
    return WINDOWS_RESERVED_NAMES.contains(filename.toUpperCase());
  }

  /**
   * Checks if all characters in the string are invalid for filenames.
   *
   * @param str The string to check
   * @return True if all characters are invalid
   */
  private static boolean isAllInvalidChars(String str) {
    for (char c : str.toCharArray()) {
      if (WINDOWS_INVALID_FILENAME_CHARS.indexOf(c) < 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if all characters in the string are the same specified character.
   *
   * @param str The string to check
   * @param ch  The character to compare against
   * @return True if all characters match
   */
  private static boolean isAllSameChar(String str, char ch) {
    for (char c : str.toCharArray()) {
      if (c != ch) {
        return false;
      }
    }
    return true;
  }

  /**
   * Simple record to hold filename parts.
   */
  private static class FilenameParts {
    final String baseName;
    final String extension;

    FilenameParts(String baseName, String extension) {
      this.baseName = baseName;
      this.extension = extension;
    }
  }
}
