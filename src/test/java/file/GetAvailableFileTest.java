package file;

import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.file.GetAvailableFile;

/**
 * Comprehensive test suite for GetAvailableFile action.
 *
 * @author Sumit Kumar
 */
public class GetAvailableFileTest {

  private Path tempDir;

  @BeforeMethod
  public void setUp() throws IOException {
    // Create temporary test directory
    tempDir = Files.createTempDirectory("file-tests");
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up test files and directories
    if (tempDir != null && Files.exists(tempDir)) {
      Files.walk(tempDir)
          .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
          .forEach(path -> {
            try {
              Files.delete(path);
            } catch (IOException e) {
              // Ignore cleanup errors
            }
          });
    }
  }

  // ==================== Basic Functionality Tests ====================

  @Test
  public void shouldReturnOriginalFilenameWhenFileDoesNotExist() throws IOException {
    String filename = "newfile.txt";

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("newfile.txt"),
        "Should return original filename when file doesn't exist");
  }

  @Test
  public void shouldReturnIncrementedFilenameWhenFileExists() throws IOException {
    String filename = "existing.txt";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("existing_1.txt"),
        "Should return incremented filename when file exists");
  }

  @Test
  public void shouldReturnSecondIncrementWhenFirstAlsoExists() throws IOException {
    String filename = "existing.txt";
    Files.createFile(tempDir.resolve(filename));
    Files.createFile(tempDir.resolve("existing_1.txt"));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("existing_2.txt"),
        "Should return second increment when first increment also exists");
  }

  @Test
  public void shouldHandleMultipleIncrements() throws IOException {
    String filename = "test.txt";
    Files.createFile(tempDir.resolve("test.txt"));
    Files.createFile(tempDir.resolve("test_1.txt"));
    Files.createFile(tempDir.resolve("test_2.txt"));
    Files.createFile(tempDir.resolve("test_3.txt"));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("test_4.txt"),
        "Should find next available increment");
  }

  // ==================== Increment Placement Tests ====================

  @Test
  public void shouldIncrementBeforeExtensionWhenTrue() throws IOException {
    String filename = "document.pdf";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true // Increment before extension
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("document_1.pdf"),
        "Should place counter before extension when incrementBeforeExtension is true");
  }

  @Test
  public void shouldIncrementAfterFilenameWhenFalse() throws IOException {
    String filename = "document.pdf";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        false // Increment after filename
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("document.pdf_1"),
        "Should place counter after entire filename when incrementBeforeExtension is false");
  }

  // ==================== File Extension Handling Tests ====================

  @Test
  public void shouldHandleFilesWithoutExtension() throws IOException {
    String filename = "README";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("README_1"),
        "Should append counter to files without extension");
  }

  @Test
  public void shouldHandleMultipleDotExtensions() throws IOException {
    String filename = "archive.tar.gz";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("archive.tar_1.gz"),
        "Should treat last extension for files with multiple dots");
  }

  @Test
  public void shouldHandleHiddenFiles() throws IOException {
    String filename = ".gitignore";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith(".gitignore_1"),
        "Should handle hidden files (starting with dot)");
  }

  @Test
  public void shouldHandleFilenameEndingWithDot() throws IOException {
    String filename = "file.";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("file._1"),
        "Should handle filename ending with dot");
  }

  // ==================== Path Construction Tests ====================

  @Test
  public void shouldReturnCompleteFilePath() throws IOException {
    String filename = "test.txt";

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    File resultFile = new File(resultPath);

    Assert.assertTrue(resultPath.contains(tempDir.toString()),
        "Should include directory path in result");
    Assert.assertTrue(resultPath.endsWith("test.txt"),
        "Should include filename in result");
    Assert.assertEquals(resultFile.getParentFile().getAbsolutePath(), tempDir.toAbsolutePath().toString(),
        "Parent directory should match input directory");
  }

  // ==================== Error Handling Tests ====================

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*does not exist.*")
  public void shouldThrowExceptionWhenDirectoryDoesNotExist() {
    GetAvailableFile.action(
        tempDir.resolve("nonexistent").toString(),
        "test.txt",
        true
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*not a directory.*")
  public void shouldThrowExceptionWhenPathIsNotDirectory() throws IOException {
    // Create a file instead of directory
    Path testFile = tempDir.resolve("notadirectory.txt");
    Files.createFile(testFile);

    GetAvailableFile.action(
        testFile.toString(),
        "test.txt",
        true
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenFilenameIsNull() {
    GetAvailableFile.action(
        tempDir.toString(),
        null,
        true
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenFilenameIsEmpty() {
    GetAvailableFile.action(
        tempDir.toString(),
        "",
        true
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenFilenameIsWhitespace() {
    GetAvailableFile.action(
        tempDir.toString(),
        "   ",
        true
    );
  }

  // ==================== Edge Cases Tests ====================

  @Test
  public void shouldHandleFilenameWithSpaces() throws IOException {
    String filename = "my document.txt";
    Files.createFile(tempDir.resolve(filename));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.contains("my document_1.txt"),
        "Should handle filenames with spaces");
  }

  @Test
  public void shouldHandleVeryLongFilename() throws IOException {
    String longBase = "a".repeat(100);
    String filename = longBase + ".txt";

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith(filename),
        "Should handle very long filenames");
  }

  @Test
  public void shouldHandleFilenameWithSpecialCharacters() throws IOException {
    // Using valid special characters for filenames
    String filename = "file-name_123.txt";

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith(filename),
        "Should handle filenames with special characters");
  }

  // ==================== Integration Tests ====================

  @Test
  public void shouldWorkWithRealWorldScenario() throws IOException {
    // Create several conflicting files
    String baseName = "report.xlsx";
    Files.createFile(tempDir.resolve("report.xlsx"));
    Files.createFile(tempDir.resolve("report_1.xlsx"));
    Files.createFile(tempDir.resolve("report_2.xlsx"));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        baseName,
        true
    );

    String resultPath = result.get();
    File resultFile = new File(resultPath);

    // Verify the result
    Assert.assertTrue(resultPath.endsWith("report_3.xlsx"),
        "Should find next available file");
    Assert.assertFalse(resultFile.exists(),
        "Result file should not exist yet");

    // Verify we can actually create the file
    boolean created = resultFile.createNewFile();
    Assert.assertTrue(created, "Should be able to create the suggested file");
  }

  @Test
  public void shouldHandleBothIncrementModes() throws IOException {
    String filename = "data.csv";
    Files.createFile(tempDir.resolve(filename));

    // Test with increment before extension (true)
    FileValue result1 = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );
    Assert.assertTrue(result1.get().endsWith("data_1.csv"),
        "Should increment before extension");

    // Test with increment after filename (false)
    FileValue result2 = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        false
    );
    Assert.assertTrue(result2.get().endsWith("data.csv_1"),
        "Should increment after filename");
  }

  @Test
  public void shouldFindGapInNumberSequence() throws IOException {
    // Create files with gaps in numbering
    String filename = "file.txt";
    Files.createFile(tempDir.resolve("file.txt"));
    Files.createFile(tempDir.resolve("file_1.txt"));
    // Note: file_2.txt is missing
    Files.createFile(tempDir.resolve("file_3.txt"));

    FileValue result = GetAvailableFile.action(
        tempDir.toString(),
        filename,
        true
    );

    String resultPath = result.get();
    // Should return file_2.txt (the first available)
    Assert.assertTrue(resultPath.endsWith("file_2.txt"),
        "Should find the first available number in sequence");
  }
}
