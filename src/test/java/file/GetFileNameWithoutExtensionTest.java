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
import sumit.devtools.properties.file.GetFileNameWithoutExtension;

/**
 * Test class for the GetFileNameWithoutExtension action
 */
public class GetFileNameWithoutExtensionTest {

  private Path tempDir;
  private File testFile;
  private File testFileInSubDir;
  private File testFileWithoutExtension;
  private File testFileWithMultipleDots;

  @BeforeMethod
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("file-tests");

    // Create test files
    testFile = Files.createFile(tempDir.resolve("test.txt")).toFile();

    // Create a subdirectory and file in it
    Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
    testFileInSubDir = Files.createFile(subDir.resolve("file_in_subdir.pdf")).toFile();

    // File without extension
    testFileWithoutExtension = Files.createFile(tempDir.resolve("noExtension")).toFile();

    // File with multiple dots
    testFileWithMultipleDots = Files.createFile(tempDir.resolve("file.name.with.dots.txt"))
        .toFile();
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up temporary files
    Files.deleteIfExists(testFile.toPath());
    Files.deleteIfExists(testFileInSubDir.toPath());
    Files.deleteIfExists(testFileWithoutExtension.toPath());
    Files.deleteIfExists(testFileWithMultipleDots.toPath());
    Files.deleteIfExists(tempDir.resolve("subdir"));
    Files.deleteIfExists(tempDir);
  }

  @Test
  public void testGetFileNameWithoutExtension() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFile.getAbsolutePath());

    // Execute
    Value<String> result = GetFileNameWithoutExtension.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), "test");
  }

  @Test
  public void testGetFileNameWithoutExtensionFromSubDir() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileInSubDir.getAbsolutePath());

    // Execute
    Value<String> result = GetFileNameWithoutExtension.convert(fileValue);

    // Verify - should return only the filename without extension
    Assert.assertEquals(result.get(), "file_in_subdir");
  }

  @Test
  public void testGetFileNameWithoutExtensionForFileWithoutExtension() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileWithoutExtension.getAbsolutePath());

    // Execute
    Value<String> result = GetFileNameWithoutExtension.convert(fileValue);

    // Verify - should return the filename
    Assert.assertEquals(result.get(), "noExtension");
  }

  @Test
  public void testGetFileNameWithoutExtensionForFileWithMultipleDots() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileWithMultipleDots.getAbsolutePath());

    // Execute
    Value<String> result = GetFileNameWithoutExtension.convert(fileValue);

    // Verify - should return the filename without the last extension
    Assert.assertEquals(result.get(), "file.name.with.dots");
  }

  @Test
  public void testGetFileNameWithoutExtensionForNonExistentFile() {
    // Create FileValue input with non-existent file
    FileValue fileValue = new FileValue(tempDir.resolve("nonexistent.file").toString());

    Value<String> result = GetFileNameWithoutExtension.convert(fileValue);
    Assert.assertEquals(result.get(), "nonexistent");
  }

}