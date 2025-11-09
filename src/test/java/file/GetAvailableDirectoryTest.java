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
import sumit.devtools.actions.file.GetAvailableDirectory;

/**
 * Comprehensive test suite for GetAvailableDirectory action.
 *
 * @author Sumit Kumar
 */
public class GetAvailableDirectoryTest {

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
  public void shouldReturnOriginalDirectoryNameWhenNotExists() throws IOException {
    String directoryName = "newdir";

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("newdir"),
        "Should return original directory name when directory doesn't exist");
  }

  @Test
  public void shouldReturnIncrementedDirectoryNameWhenExists() throws IOException {
    String directoryName = "existing";
    Files.createDirectory(tempDir.resolve(directoryName));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("existing_1"),
        "Should return incremented directory name when directory exists");
  }

  @Test
  public void shouldReturnSecondIncrementWhenFirstAlsoExists() throws IOException {
    String directoryName = "existing";
    Files.createDirectory(tempDir.resolve(directoryName));
    Files.createDirectory(tempDir.resolve("existing_1"));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("existing_2"),
        "Should return second increment when first increment also exists");
  }

  @Test
  public void shouldHandleMultipleIncrements() throws IOException {
    String directoryName = "data";
    Files.createDirectory(tempDir.resolve("data"));
    Files.createDirectory(tempDir.resolve("data_1"));
    Files.createDirectory(tempDir.resolve("data_2"));
    Files.createDirectory(tempDir.resolve("data_3"));
    Files.createDirectory(tempDir.resolve("data_4"));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("data_5"),
        "Should find next available increment");
  }

  // ==================== Naming Tests ====================

  @Test
  public void shouldAlwaysAppendCounterToDirectoryName() throws IOException {
    String directoryName = "folder";
    Files.createDirectory(tempDir.resolve(directoryName));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("folder_1"),
        "Should append counter to directory name");
    Assert.assertFalse(resultPath.contains("folder1."),
        "Should not treat anything as extension");
  }

  @Test
  public void shouldHandleDirectoryNamesWithDots() throws IOException {
    // Directories can have dots in their names (like ".config" or "v1.0")
    String directoryName = "version.1.0";
    Files.createDirectory(tempDir.resolve(directoryName));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("version.1.0_1"),
        "Should append counter after entire directory name, ignoring dots");
  }

  @Test
  public void shouldHandleHiddenDirectories() throws IOException {
    String directoryName = ".hidden";
    Files.createDirectory(tempDir.resolve(directoryName));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith(".hidden_1"),
        "Should handle hidden directories (starting with dot)");
  }

  // ==================== Path Construction Tests ====================

  @Test
  public void shouldReturnCompleteDirectoryPath() throws IOException {
    String directoryName = "testdir";

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    File resultDir = new File(resultPath);

    Assert.assertTrue(resultPath.contains(tempDir.toString()),
        "Should include parent directory path in result");
    Assert.assertTrue(resultPath.endsWith("testdir"),
        "Should include directory name in result");
    Assert.assertEquals(resultDir.getParentFile().getAbsolutePath(), tempDir.toAbsolutePath().toString(),
        "Parent directory should match input directory");
  }

  // ==================== Error Handling Tests ====================

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*does not exist.*")
  public void shouldThrowExceptionWhenParentDirectoryDoesNotExist() {
    GetAvailableDirectory.action(
        tempDir.resolve("nonexistent").toString(),
        "testdir"
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*not a directory.*")
  public void shouldThrowExceptionWhenParentPathIsNotDirectory() throws IOException {
    // Create a file instead of directory
    Path testFile = tempDir.resolve("notadirectory.txt");
    Files.createFile(testFile);

    GetAvailableDirectory.action(
        testFile.toString(),
        "testdir"
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenDirectoryNameIsNull() {
    GetAvailableDirectory.action(
        tempDir.toString(),
        null
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenDirectoryNameIsEmpty() {
    GetAvailableDirectory.action(
        tempDir.toString(),
        ""
    );
  }

  @Test(expectedExceptions = BotCommandException.class,
      expectedExceptionsMessageRegExp = ".*cannot be null or empty.*")
  public void shouldThrowExceptionWhenDirectoryNameIsWhitespace() {
    GetAvailableDirectory.action(
        tempDir.toString(),
        "   "
    );
  }

  // ==================== Edge Cases Tests ====================

  @Test
  public void shouldHandleDirectoryNamesWithSpaces() throws IOException {
    String directoryName = "my folder";
    Files.createDirectory(tempDir.resolve(directoryName));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.contains("my folder_1"),
        "Should handle directory names with spaces");
  }

  @Test
  public void shouldHandleVeryLongDirectoryNames() throws IOException {
    String longName = "a".repeat(100);

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        longName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith(longName),
        "Should handle very long directory names");
  }

  @Test
  public void shouldHandleDirectoryNamesWithSpecialCharacters() throws IOException {
    // Using valid special characters for directory names
    String directoryName = "folder-name_123";

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith(directoryName),
        "Should handle directory names with special characters");
  }

  @Test
  public void shouldHandleDirectoryNamesWithUnderscores() throws IOException {
    // Directory name already has underscores
    String directoryName = "my_folder";
    Files.createDirectory(tempDir.resolve(directoryName));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("my_folder_1"),
        "Should correctly append counter even when directory name has underscores");
  }

  @Test
  public void shouldHandleDirectoryNamesEndingWithNumbers() throws IOException {
    String directoryName = "backup2024";
    Files.createDirectory(tempDir.resolve(directoryName));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.endsWith("backup2024_1"),
        "Should correctly append counter to directory names ending with numbers");
  }

  // ==================== Integration Tests ====================

  @Test
  public void shouldWorkWithRealWorldScenario() throws IOException {
    // Create several conflicting directories
    String baseName = "project";
    Files.createDirectory(tempDir.resolve("project"));
    Files.createDirectory(tempDir.resolve("project_1"));
    Files.createDirectory(tempDir.resolve("project_2"));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        baseName
    );

    String resultPath = result.get();
    File resultDir = new File(resultPath);

    // Verify the result
    Assert.assertTrue(resultPath.endsWith("project_3"),
        "Should find next available directory");
    Assert.assertFalse(resultDir.exists(),
        "Result directory should not exist yet");

    // Verify we can actually create the directory
    boolean created = resultDir.mkdir();
    Assert.assertTrue(created, "Should be able to create the suggested directory");
  }

  @Test
  public void shouldFindGapInNumberSequence() throws IOException {
    // Create directories with gaps in numbering
    String directoryName = "dir";
    Files.createDirectory(tempDir.resolve("dir"));
    Files.createDirectory(tempDir.resolve("dir_1"));
    // Note: dir_2 is missing
    Files.createDirectory(tempDir.resolve("dir_3"));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        directoryName
    );

    String resultPath = result.get();
    // Should return dir_2 (the first available)
    Assert.assertTrue(resultPath.endsWith("dir_2"),
        "Should find the first available number in sequence");
  }

  @Test
  public void shouldIgnoreFiles() throws IOException {
    // Create a FILE with the same name (not a directory)
    String name = "item";
    Files.createFile(tempDir.resolve(name));

    FileValue result = GetAvailableDirectory.action(
        tempDir.toString(),
        name
    );

    String resultPath = result.get();
    // Even though a FILE named "item" exists, the directory check should see it as existing
    // and return "item_1"
    Assert.assertTrue(resultPath.endsWith("item_1"),
        "Should increment when a file with the same name exists");
  }

  @Test
  public void shouldHandleNestedDirectoryCreation() throws IOException {
    // Create parent structure
    Path level1 = tempDir.resolve("level1");
    Files.createDirectory(level1);

    FileValue result = GetAvailableDirectory.action(
        level1.toString(),
        "sublevel"
    );

    String resultPath = result.get();
    Assert.assertTrue(resultPath.contains("level1"),
        "Should support nested directory paths");
    Assert.assertTrue(resultPath.endsWith("sublevel"),
        "Should return correct directory name");
  }
}
