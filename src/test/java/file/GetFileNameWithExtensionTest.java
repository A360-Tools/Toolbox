package file;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.FileValue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.properties.file.GetFileNameWithExtension;

/**
 * Test class for the GetFileNameWithExtension action
 */
public class GetFileNameWithExtensionTest {

  private Path tempDir;
  private File testFile;
  private File testFileInSubDir;
  private File testFileWithMultipleDots;

  @BeforeMethod
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("file-tests");

    // Create test files
    testFile = Files.createFile(tempDir.resolve("test.txt")).toFile();

    // Create a subdirectory and file in it
    Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
    testFileInSubDir = Files.createFile(subDir.resolve("file_in_subdir.pdf")).toFile();

    // File with multiple dots
    testFileWithMultipleDots = Files.createFile(tempDir.resolve("file.name.with.dots.txt"))
        .toFile();
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up temporary files
    Files.deleteIfExists(testFile.toPath());
    Files.deleteIfExists(testFileInSubDir.toPath());
    Files.deleteIfExists(testFileWithMultipleDots.toPath());
    Files.deleteIfExists(tempDir.resolve("subdir"));
    Files.deleteIfExists(tempDir);
  }

  @Test
  public void testGetFileNameWithExtension() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFile.getAbsolutePath());

    // Execute
    Value<String> result = GetFileNameWithExtension.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), "test.txt");
  }

  @Test
  public void testGetFileNameWithExtensionFromSubDir() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileInSubDir.getAbsolutePath());

    // Execute
    Value<String> result = GetFileNameWithExtension.convert(fileValue);

    // Verify - should return only the filename, not the path
    Assert.assertEquals(result.get(), "file_in_subdir.pdf");
  }

  @Test
  public void testGetFileNameWithExtensionForFileWithMultipleDots() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileWithMultipleDots.getAbsolutePath());

    // Execute
    Value<String> result = GetFileNameWithExtension.convert(fileValue);

    // Verify - should return the full filename with extension
    Assert.assertEquals(result.get(), "file.name.with.dots.txt");
  }

  @Test
  public void testGetFileNameWithExtensionForNonExistentFile() {
    // Create FileValue input with non-existent file
    FileValue fileValue = new FileValue(tempDir.resolve("nonexistent.file").toString());

    Value<String> result = GetFileNameWithExtension.convert(fileValue);

    // Verify - should return the full filename with extension
    Assert.assertEquals(result.get(), "nonexistent.file");
  }

}