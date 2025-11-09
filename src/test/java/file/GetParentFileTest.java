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
import sumit.devtools.properties.file.GetParentFile;

/**
 * Test class for the GetParentFile action
 */
public class GetParentFileTest {

  private Path tempDir;
  private Path subDir;
  private Path nestedDir;
  private File testFile;
  private File testFileInSubDir;
  private File testFileInNestedDir;

  @BeforeMethod
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("file-tests");

    // Create test files and directories
    testFile = Files.createFile(tempDir.resolve("test.txt")).toFile();

    // Create subdirectory and file in it
    subDir = Files.createDirectory(tempDir.resolve("subdir"));
    testFileInSubDir = Files.createFile(subDir.resolve("file_in_subdir.txt")).toFile();

    // Create nested directory and file in it
    nestedDir = Files.createDirectory(subDir.resolve("nesteddir"));
    testFileInNestedDir = Files.createFile(nestedDir.resolve("file_in_nested.txt")).toFile();
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up temporary files
    Files.deleteIfExists(testFile.toPath());
    Files.deleteIfExists(testFileInSubDir.toPath());
    Files.deleteIfExists(testFileInNestedDir.toPath());
    Files.deleteIfExists(nestedDir);
    Files.deleteIfExists(subDir);
    Files.deleteIfExists(tempDir);
  }

  @Test
  public void testGetParentOfFileInRootDir() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFile.getAbsolutePath());

    // Execute
    Value<String> result = GetParentFile.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), tempDir.toString());
  }

  @Test
  public void testGetParentOfFileInSubDir() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileInSubDir.getAbsolutePath());

    // Execute
    Value<String> result = GetParentFile.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), subDir.toString());
  }

  @Test
  public void testGetParentOfFileInNestedDir() {
    // Create FileValue input
    FileValue fileValue = new FileValue(testFileInNestedDir.getAbsolutePath());

    // Execute
    Value<String> result = GetParentFile.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), nestedDir.toString());
  }

  @Test
  public void testGetParentOfDirectory() {
    // Create FileValue input for a directory
    FileValue fileValue = new FileValue(subDir.toString());

    // Execute
    Value<String> result = GetParentFile.convert(fileValue);

    // Verify
    Assert.assertEquals(result.get(), tempDir.toString());
  }

  @Test
  public void testGetParentOfNonExistentFile() {
    // Create FileValue input with non-existent file
    FileValue fileValue = new FileValue(tempDir.resolve("nonexistent.file").toString());
    Value<String> result = GetParentFile.convert(fileValue);
    Assert.assertEquals(result.get(), tempDir.toString());
  }

}