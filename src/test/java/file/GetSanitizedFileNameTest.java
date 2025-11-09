package file;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sumit.devtools.actions.file.GetSanitizedFileName;

/**
 * Comprehensive test suite for GetSanitizedFileName action.
 * Uses parameterized tests and clear organization for better maintainability.
 *
 * @author Sumit Kumar
 */
public class GetSanitizedFileNameTest {

  // ==================== Test Data Providers ====================

  @DataProvider(name = "invalidCharacterScenarios")
  public Object[][] invalidCharacterScenarios() {
    return new Object[][]{
        // {input, replacementChar, expectedOutput, description}
        {"file<with>test.txt", "_", "file_with_test.txt", "Angle brackets"},
        {"file:name?.txt", "_", "file_name.txt", "Colon and question mark"},
        {"file|test*.txt", "_", "file_test.txt", "Pipe and asterisk"},
        {"file/test\\name.txt", "_", "file_test_name.txt", "Path separators"},
        {"file\"name.txt", "_", "file_name.txt", "Quote character"},
        {"file\0test\t.txt", "_", "file_test.txt", "Control characters"},
        {"file<>:\"/?*.txt", "_", "file.txt", "Multiple invalid chars"},
    };
  }

  @DataProvider(name = "consecutiveCharNormalization")
  public Object[][] consecutiveCharNormalization() {
    return new Object[][]{
        {"file___name.txt", "_", "file_name.txt"},
        {"file<<<test>>>.txt", "_", "file_test.txt"},
        {"file????name.txt", "-", "file-name.txt"},
    };
  }

  @DataProvider(name = "reservedNamesScenarios")
  public Object[][] reservedNamesScenarios() {
    return new Object[][]{
        // Reserved names without extension should return replacement char
        {"CON", "_", "_"},
        {"PRN", "_", "_"},
        {"AUX", "_", "_"},
        {"NUL", "_", "_"},
        {"COM1", "_", "_"},
        {"LPT1", "_", "_"},
        // Mixed case should also be detected
        {"Con", "_", "_"},
        {"prN", "_", "_"},
        {"AuX", "_", "_"},
        // Reserved names with extensions should preserve the name
        {"CON.txt", "_", "CON.txt"},
        {"PRN.doc", "_", "PRN.doc"},
        {"AUX.pdf", "_", "AUX.pdf"},
    };
  }

  @DataProvider(name = "replacementCharVariations")
  public Object[][] replacementCharVariations() {
    return new Object[][]{
        {"file<test>.txt", "+", "file+test.txt"},
        {"file<test>.txt", "-", "file-test.txt"},
        {"file<test>.txt", ".", "file.test.txt"},
        {"file<test>.txt", "XYZ", "fileXtest.txt"}, // Only first char used
    };
  }

  @DataProvider(name = "edgeCases")
  public Object[][] edgeCases() {
    return new Object[][]{
        // {input, replacementChar, expectedOutput, description}
        {"_____", "_", "_", "All underscores"},
        {"<<<???***", "_", "_", "All invalid chars"},
        {"valid-filename_123.txt", "_", "valid-filename_123.txt", "Already valid"},
        {"  leadingAndTrailingSpaces  .txt", "_", "leadingAndTrailingSpaces.txt",
            "Leading/trailing spaces"},
        {"file with spaces.txt", "_", "file with spaces.txt", "Internal spaces preserved"},
    };
  }

  @DataProvider(name = "unicodeScenarios")
  public Object[][] unicodeScenarios() {
    return new Object[][]{
        {"Unicode测试文件名.txt", "_", "Unicode测试文件名.txt"},
        {"こんにちは.txt", "_", "こんにちは.txt"},
        {"Файл.txt", "_", "Файл.txt"},
    };
  }

  @DataProvider(name = "extensionHandling")
  public Object[][] extensionHandling() {
    return new Object[][]{
        {".hiddenfile", "_", ".hiddenfile"},
        {"file.tar.gz", "_", "file.tar.gz"},
        {"file.", "_", "file"},
        {"file.txt.bak", "_", "file.txt.bak"},
    };
  }

  // ==================== Invalid Character Replacement Tests ====================

  @Test(dataProvider = "invalidCharacterScenarios")
  public void shouldReplaceInvalidCharactersCorrectly(
      String input, String replacement, String expected, String description) {
    StringValue result = GetSanitizedFileName.action(input, replacement);
    Assert.assertEquals(result.get(), expected,
        "Failed for scenario: " + description);
  }

  @Test(dataProvider = "consecutiveCharNormalization")
  public void shouldNormalizeConsecutiveReplacementChars(
      String input, String replacement, String expected) {
    StringValue result = GetSanitizedFileName.action(input, replacement);
    Assert.assertEquals(result.get(), expected);
  }

  @Test
  public void shouldRemoveLeadingTrailingReplacementChars() {
    StringValue result = GetSanitizedFileName.action("__filename_with_border__.txt", "_");
    String sanitized = result.get();

    Assert.assertFalse(sanitized.startsWith("_"),
        "Should not start with replacement char");
    Assert.assertTrue(sanitized.startsWith("filename"),
        "Should start with 'filename'");
    Assert.assertTrue(sanitized.endsWith(".txt"),
        "Should end with '.txt'");
  }

  // ==================== Windows Reserved Names Tests ====================

  @Test(dataProvider = "reservedNamesScenarios")
  public void shouldHandleWindowsReservedNamesCorrectly(
      String input, String replacement, String expected) {
    StringValue result = GetSanitizedFileName.action(input, replacement);
    Assert.assertEquals(result.get(), expected,
        String.format("Reserved name '%s' not handled correctly", input));
  }

  // ==================== Replacement Character Tests ====================

  @Test(dataProvider = "replacementCharVariations")
  public void shouldUseCustomReplacementCharacter(
      String input, String replacement, String expected) {
    StringValue result = GetSanitizedFileName.action(input, replacement);
    Assert.assertEquals(result.get(), expected);
  }

  @Test
  public void shouldFallbackToDefaultWhenReplacementIsInvalid() {
    // Using '/' as replacement (invalid) should fall back to '_'
    StringValue result = GetSanitizedFileName.action("file<test>.txt", "/");
    Assert.assertTrue(result.get().contains("_"),
        "Should use default underscore when replacement char is invalid");
    Assert.assertFalse(result.get().contains("/"),
        "Should not contain the invalid replacement char");
  }

  @Test
  public void shouldUseDefaultWhenReplacementIsNull() {
    StringValue result = GetSanitizedFileName.action("file<test>.txt", null);
    Assert.assertTrue(result.get().contains("_"),
        "Should use default underscore when replacement is null");
  }

  @Test
  public void shouldUseDefaultWhenReplacementIsEmpty() {
    StringValue result = GetSanitizedFileName.action("file<test>.txt", "");
    Assert.assertTrue(result.get().contains("_"),
        "Should use default underscore when replacement is empty");
  }

  // ==================== Edge Cases Tests ====================

  @Test(dataProvider = "edgeCases")
  public void shouldHandleEdgeCasesCorrectly(
      String input, String replacement, String expected, String description) {
    StringValue result = GetSanitizedFileName.action(input, replacement);
    Assert.assertEquals(result.get(), expected,
        "Failed for edge case: " + description);
  }

  @Test
  public void shouldHandleVeryLongFilenames() {
    // Test with 1000 character filename
    String longName = "abcdefghij".repeat(100);
    StringValue result = GetSanitizedFileName.action(longName, "_");

    Assert.assertEquals(result.get().length(), longName.length(),
        "Length should be preserved for valid long filenames");
  }

  // ==================== Unicode Support Tests ====================

  @Test(dataProvider = "unicodeScenarios")
  public void shouldPreserveValidUnicodeCharacters(
      String input, String replacement, String expected) {
    StringValue result = GetSanitizedFileName.action(input, replacement);
    Assert.assertEquals(result.get(), expected,
        "Unicode characters should be preserved");
  }

  // ==================== Extension Handling Tests ====================

  @Test(dataProvider = "extensionHandling")
  public void shouldHandleFileExtensionsCorrectly(
      String input, String replacement, String expected) {
    StringValue result = GetSanitizedFileName.action(input, replacement);
    Assert.assertEquals(result.get(), expected);
  }

  // ==================== Error Handling Tests ====================

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenFilenameIsNull() {
    GetSanitizedFileName.action(null, "_");
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenFilenameIsEmpty() {
    GetSanitizedFileName.action("", "_");
  }

  // ==================== Special Character Tests ====================

  @Test
  public void shouldHandleRegexSpecialCharsAsReplacement() {
    String[][] testCases = {
        {"file<test>.txt", ".", "file.test.txt"},
        {"file<test>.txt", "+", "file+test.txt"},
        {"file<test>.txt", "*", "file_test.txt"}, // Should fall back to _
        {"file<test>.txt", "^", "file^test.txt"},
        {"file<test>.txt", "$", "file$test.txt"},
    };

    for (String[] testCase : testCases) {
      String input = testCase[0];
      String replacement = testCase[1];
      String expected = testCase[2];

      StringValue result = GetSanitizedFileName.action(input, replacement);
      Assert.assertEquals(result.get(), expected,
          String.format("Failed for replacement char '%s'", replacement));
    }
  }

  @Test
  public void shouldPreserveInternalSpaces() {
    StringValue result = GetSanitizedFileName.action("file with spaces.txt", "_");
    Assert.assertEquals(result.get(), "file with spaces.txt",
        "Internal spaces should be preserved");
  }

  @Test
  public void shouldTrimLeadingAndTrailingWhitespace() {
    StringValue result = GetSanitizedFileName.action("  file.txt  ", "_");
    Assert.assertEquals(result.get(), "file.txt",
        "Leading and trailing whitespace should be trimmed");
  }

  // ==================== Comprehensive Integration Tests ====================

  @Test
  public void shouldHandleComplexRealWorldScenario() {
    // Simulates a complex real-world filename with multiple issues
    String complexFilename = "  My<Document>:Version2.0|Final?.docx  ";
    StringValue result = GetSanitizedFileName.action(complexFilename, "_");

    String sanitized = result.get();

    // Verify all transformations
    Assert.assertFalse(sanitized.contains("<"), "Should not contain '<'");
    Assert.assertFalse(sanitized.contains(">"), "Should not contain '>'");
    Assert.assertFalse(sanitized.contains(":"), "Should not contain ':'");
    Assert.assertFalse(sanitized.contains("|"), "Should not contain '|'");
    Assert.assertFalse(sanitized.contains("?"), "Should not contain '?'");
    Assert.assertFalse(sanitized.contains("__"), "Should not have consecutive underscores");
    Assert.assertFalse(sanitized.startsWith(" "), "Should not start with space");
    Assert.assertFalse(sanitized.endsWith(" "), "Should not end with space");
    Assert.assertTrue(sanitized.endsWith(".docx"), "Should preserve extension");
    Assert.assertTrue(sanitized.contains("My"), "Should preserve valid chars");
  }

  @Test
  public void shouldMaintainFileIntegrityForValidNames() {
    String[] validFilenames = {
        "document.pdf",
        "My_File-2024.txt",
        "data-analysis_v1.0.xlsx",
        "report.final.docx",
    };

    for (String filename : validFilenames) {
      StringValue result = GetSanitizedFileName.action(filename, "_");
      Assert.assertEquals(result.get(), filename,
          String.format("Valid filename '%s' should remain unchanged", filename));
    }
  }
}
