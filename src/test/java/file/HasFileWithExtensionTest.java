package file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.conditionals.file.HasFileWithExtension;

/**
 * Test class for the HasFileWithExtension conditional
 */
public class HasFileWithExtensionTest {

  private Path testDir;
  private Path subDir;

  @BeforeMethod
  public void setUp() throws IOException {
    // Create temporary test directory structure
    testDir = Files.createTempDirectory("file-tests");
    subDir = Files.createDirectory(testDir.resolve("subdir"));

    // Create test files
    Files.createFile(testDir.resolve("test.txt"));
    Files.createFile(testDir.resolve("document.pdf"));
    Files.createFile(testDir.resolve("data.csv"));
    Files.createFile(testDir.resolve("noextension"));

    // Create files in subdirectory
    Files.createFile(subDir.resolve("nested.java"));
    Files.createFile(subDir.resolve("config.xml"));
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
  public void testHasFileWithExtension_TxtExists() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "txt",
        false,
        0.0
    );
    Assert.assertTrue(result, "Should find .txt file in directory");
  }

  @Test
  public void testHasFileWithExtension_PdfExists() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "pdf",
        false,
        0.0
    );
    Assert.assertTrue(result, "Should find .pdf file in directory");
  }

  @Test
  public void testHasFileWithExtension_DoesNotExist() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "docx",
        false,
        0.0
    );
    Assert.assertFalse(result, "Should not find .docx file in directory");
  }

  @Test
  public void testDoesNotHaveFileWithExtension_NonExistent() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "NOTCONTAINS",
        "docx",
        false,
        0.0
    );
    Assert.assertTrue(result, "Should confirm .docx file does not exist");
  }

  @Test
  public void testDoesNotHaveFileWithExtension_Exists() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "NOTCONTAINS",
        "txt",
        false,
        0.0
    );
    Assert.assertFalse(result, "Should confirm .txt file exists (does not satisfy 'does not have')");
  }

  @Test
  public void testNonRecursive_DoesNotFindInSubdirectory() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "java",
        false,
        0.0
    );
    Assert.assertFalse(result, "Should not find .java file in subdirectory when not searching recursively");
  }

  @Test
  public void testRecursive_FindsInSubdirectory() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "java",
        true,
        0.0
    );
    Assert.assertTrue(result, "Should find .java file in subdirectory when searching recursively");
  }

  @Test
  public void testRecursive_FindsXmlInSubdirectory() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "xml",
        true,
        0.0
    );
    Assert.assertTrue(result, "Should find .xml file in subdirectory when searching recursively");
  }

  @Test
  public void testExtensionWithLeadingDot() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        ".txt",
        false,
        0.0
    );
    Assert.assertTrue(result, "Should handle extension with leading dot");
  }

  @Test
  public void testCaseInsensitiveExtension() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "TXT",
        false,
        0.0
    );
    Assert.assertTrue(result, "Should find file with case-insensitive extension match");
  }

  @Test
  public void testCaseInsensitiveExtension_Pdf() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "PDF",
        false,
        0.0
    );
    Assert.assertTrue(result, "Should find .pdf file with uppercase extension search");
  }

  @Test
  public void testWaitTimeout_ImmediateSuccess() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "csv",
        false,
        1.0
    );
    Assert.assertTrue(result, "Should find .csv file immediately even with timeout");
  }

  @Test
  public void testWaitTimeout_ReturnsFalseNotException() {
    // Test that timeout returns false instead of throwing exception
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "nonexistent",
        false,
        1.0  // Wait 1 second for file that doesn't exist
    );
    Assert.assertFalse(result, "Should return false after timeout, not throw exception");
  }

  @Test
  public void testMultipleFilesWithSameExtension() {
    Boolean result = HasFileWithExtension.test(
        testDir.toString(),
        "CONTAINS",
        "txt",
        false,
        0.0
    );
    Assert.assertTrue(result, "Should find at least one .txt file");
  }

  @Test(expectedExceptions = com.automationanywhere.botcommand.exception.BotCommandException.class)
  public void testNonExistentDirectory() {
    HasFileWithExtension.test(
        testDir.toString() + File.separator + "nonexistent",
        "CONTAINS",
        "txt",
        false,
        0.0
    );
  }

  @Test(expectedExceptions = com.automationanywhere.botcommand.exception.BotCommandException.class)
  public void testFilePathInsteadOfDirectory() throws IOException {
    // Pass a file path instead of directory
    Path filePath = testDir.resolve("test.txt");
    HasFileWithExtension.test(
        filePath.toString(),
        "CONTAINS",
        "txt",
        false,
        0.0
    );
  }
}
