package file;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.file.GetFilesWithExtensions;

/**
 * Test class for the GetFilesWithExtensions action
 */
public class GetFilesWithExtensionsTest {

  private Path testDir;
  private Path subDir;

  @BeforeMethod
  public void setUp() throws IOException {
    // Create temporary test directory structure
    testDir = Files.createTempDirectory("file-tests");
    subDir = Files.createDirectory(testDir.resolve("subdir"));

    // Create test files in root directory
    Files.createFile(testDir.resolve("document1.txt"));
    Files.createFile(testDir.resolve("document2.txt"));
    Files.createFile(testDir.resolve("report.pdf"));
    Files.createFile(testDir.resolve("data.csv"));
    Files.createFile(testDir.resolve("image.png"));
    Files.createFile(testDir.resolve("noextension"));

    // Create files in subdirectory
    Files.createFile(subDir.resolve("nested.java"));
    Files.createFile(subDir.resolve("config.xml"));
    Files.createFile(subDir.resolve("notes.txt"));
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up test files and directories
    if (testDir != null && Files.exists(testDir)) {
      Files.walk(testDir)
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

  @Test
  public void testGetAllFiles_NonRecursive() {
    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "ALL",
        null
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertNotNull(result.get(), "Result list should not be null");
    Assert.assertEquals(result.get().size(), 6, "Should find 6 files in root directory (excluding subdirectory)");
  }

  @Test
  public void testGetAllFiles_Recursive() {
    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        true,
        "ALL",
        null
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertNotNull(result.get(), "Result list should not be null");
    Assert.assertEquals(result.get().size(), 9, "Should find 9 files total (6 in root + 3 in subdir)");
  }

  @Test
  public void testGetSpecificExtension_SingleExtension() {
    List<StringValue> extensions = new ArrayList<>();
    extensions.add(new StringValue("txt"));

    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "SPECIFIC",
        extensions
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertEquals(result.get().size(), 2, "Should find 2 .txt files in root directory");

    // Verify all files have .txt extension
    List<String> filePaths = result.get().stream()
        .map(v -> ((FileValue) v).get())
        .collect(Collectors.toList());

    for (String path : filePaths) {
      Assert.assertTrue(path.endsWith(".txt"), "File should have .txt extension: " + path);
    }
  }

  @Test
  public void testGetSpecificExtension_MultipleExtensions() {
    List<StringValue> extensions = new ArrayList<>();
    extensions.add(new StringValue("txt"));
    extensions.add(new StringValue("pdf"));
    extensions.add(new StringValue("csv"));

    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "SPECIFIC",
        extensions
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertEquals(result.get().size(), 4, "Should find 4 files (2 txt + 1 pdf + 1 csv)");
  }

  @Test
  public void testGetSpecificExtension_Recursive() {
    List<StringValue> extensions = new ArrayList<>();
    extensions.add(new StringValue("txt"));

    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        true,
        "SPECIFIC",
        extensions
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertEquals(result.get().size(), 3, "Should find 3 .txt files (2 in root + 1 in subdir)");
  }

  @Test
  public void testGetSpecificExtension_WithLeadingDot() {
    List<StringValue> extensions = new ArrayList<>();
    extensions.add(new StringValue(".pdf"));

    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "SPECIFIC",
        extensions
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertEquals(result.get().size(), 1, "Should find 1 .pdf file");
  }

  @Test
  public void testGetSpecificExtension_CaseInsensitive() {
    List<StringValue> extensions = new ArrayList<>();
    extensions.add(new StringValue("TXT"));
    extensions.add(new StringValue("PdF"));

    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "SPECIFIC",
        extensions
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertEquals(result.get().size(), 3, "Should find 3 files with case-insensitive matching");
  }

  @Test
  public void testGetSpecificExtension_NoMatches() {
    List<StringValue> extensions = new ArrayList<>();
    extensions.add(new StringValue("docx"));
    extensions.add(new StringValue("xlsx"));

    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "SPECIFIC",
        extensions
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertEquals(result.get().size(), 0, "Should find 0 files when no matches");
  }

  @Test
  public void testEmptyDirectory() throws IOException {
    // Create empty directory
    Path emptyDir = Files.createTempDirectory("empty-test");

    try {
      ListValue<FileValue> result = GetFilesWithExtensions.action(
          emptyDir.toString(),
          false,
          "ALL",
          null
      );

      Assert.assertNotNull(result, "Result should not be null");
      Assert.assertEquals(result.get().size(), 0, "Should find 0 files in empty directory");
    } finally {
      Files.deleteIfExists(emptyDir);
    }
  }

  @Test
  public void testFilePathsAreAbsolute() {
    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "ALL",
        null
    );

    // Verify all paths are absolute
    List<String> filePaths = result.get().stream()
        .map(v -> ((FileValue) v).get())
        .collect(Collectors.toList());

    for (String path : filePaths) {
      Assert.assertTrue(new File(path).isAbsolute(), "File path should be absolute: " + path);
    }
  }

  @Test
  public void testResultContainsFileValues() {
    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        false,
        "ALL",
        null
    );

    // Verify all items are FileValue instances
    for (Value value : result.get()) {
      Assert.assertTrue(value instanceof FileValue, "All items should be FileValue instances");
    }
  }

  @Test(expectedExceptions = com.automationanywhere.botcommand.exception.BotCommandException.class)
  public void testNonExistentDirectory() {
    GetFilesWithExtensions.action(
        testDir.toString() + File.separator + "nonexistent",
        false,
        "ALL",
        null
    );
  }

  @Test(expectedExceptions = com.automationanywhere.botcommand.exception.BotCommandException.class)
  public void testFilePathInsteadOfDirectory() {
    // Pass a file path instead of directory
    Path filePath = testDir.resolve("document1.txt");
    GetFilesWithExtensions.action(
        filePath.toString(),
        false,
        "ALL",
        null
    );
  }

  @Test
  public void testMixedExtensionsWithRecursive() {
    List<StringValue> extensions = new ArrayList<>();
    extensions.add(new StringValue("java"));
    extensions.add(new StringValue("xml"));

    ListValue<FileValue> result = GetFilesWithExtensions.action(
        testDir.toString(),
        true,
        "SPECIFIC",
        extensions
    );

    Assert.assertNotNull(result, "Result should not be null");
    Assert.assertEquals(result.get().size(), 2, "Should find 2 files (1 java + 1 xml in subdir)");

    // Verify correct files
    List<String> filePaths = result.get().stream()
        .map(v -> ((FileValue) v).get())
        .collect(Collectors.toList());

    boolean hasJava = filePaths.stream().anyMatch(path -> path.endsWith(".java"));
    boolean hasXml = filePaths.stream().anyMatch(path -> path.endsWith(".xml"));

    Assert.assertTrue(hasJava, "Should contain .java file");
    Assert.assertTrue(hasXml, "Should contain .xml file");
  }
}
