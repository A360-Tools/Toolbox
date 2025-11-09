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
import sumit.devtools.properties.file.GetFileExtension;

/**
 * Test class for the GetFileExtension action
 */
public class GetFileExtensionTest {

    private Path tempDir;
  private File testTextFile;
  private File testPdfFile;
  private File testFileWithoutExtension;
  private File testFileWithMultipleDots;

  @BeforeMethod
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("file-tests");

    // Create test files with different extensions
    testTextFile = Files.createFile(tempDir.resolve("test.txt")).toFile();
    testPdfFile = Files.createFile(tempDir.resolve("document.pdf")).toFile();
    testFileWithoutExtension = Files.createFile(tempDir.resolve("noExtension")).toFile();
    testFileWithMultipleDots = Files.createFile(tempDir.resolve("file.name.with.dots.txt"))
        .toFile();
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up temporary files
    Files.deleteIfExists(testTextFile.toPath());
    Files.deleteIfExists(testPdfFile.toPath());
    Files.deleteIfExists(testFileWithoutExtension.toPath());
    Files.deleteIfExists(testFileWithMultipleDots.toPath());
    Files.deleteIfExists(tempDir);
  }

  @Test
  public void testGetExtensionForTextFile() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testTextFile.getAbsolutePath());

    // Execute
    Value<String> result = GetFileExtension.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), "txt");
  }

  @Test
  public void testGetExtensionForPdfFile() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testPdfFile.getAbsolutePath());

    // Execute
    Value<String> result = GetFileExtension.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), "pdf");
  }

  @Test
  public void testGetExtensionForFileWithoutExtension() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileWithoutExtension.getAbsolutePath());

    // Execute
    Value<String> result = GetFileExtension.convert(fileValue);

    // Verify - should return empty string
    Assert.assertEquals(result.get(), "");
  }

  @Test
  public void testGetExtensionForFileWithMultipleDots() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileWithMultipleDots.getAbsolutePath());

    // Execute
    Value<String> result = GetFileExtension.convert(fileValue);

    // Verify - should return the last extension
    Assert.assertEquals(result.get(), "txt");
  }

  @Test
  public void testGetExtensionForNonExistentFile() {
    // Create FileValue input with non-existent file
    FileValue fileValue = new FileValue(tempDir.resolve("nonexistent.file").toString());

    // Execute - should throw exception
    Value<String> result = GetFileExtension.convert(fileValue);
    Assert.assertEquals(result.get(), "file");
  }

}