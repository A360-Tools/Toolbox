package file;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.file.EncodeFileToBase64;

/**
 * Test class for the EncodeFileToBase64 action
 */
public class EncodeFileToBase64Test {

  private Path tempDir;
  private File testFile;
  private String testContent;

  @BeforeMethod
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("file-tests");

    // Create a test file with known content
    testFile = tempDir.resolve("test-file.txt").toFile();
    testContent = "This is a test file content for base64 encoding test.";

    try (FileOutputStream fos = new FileOutputStream(testFile)) {
      fos.write(testContent.getBytes(StandardCharsets.UTF_8));
    }
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up temporary files
    Files.deleteIfExists(testFile.toPath());
    Files.deleteIfExists(tempDir);
  }

  @Test
  public void testEncodeTextFile() {
    // Execute the action
    StringValue result = EncodeFileToBase64.action(testFile.getAbsolutePath());

    // Verify the result
    String expectedBase64 = Base64.getEncoder()
        .encodeToString(testContent.getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals(result.get(), expectedBase64,
        "The encoded Base64 string does not match the expected " +
            "value");
  }

  @Test
  public void testEncodeBinaryFile() throws IOException {
    // Create a binary file (with random bytes)
    File binaryFile = tempDir.resolve("binary-file.bin").toFile();
    byte[] binaryContent = new byte[100];
    for (int i = 0; i < binaryContent.length; i++) {
      binaryContent[i] = (byte) i;
    }

    try (FileOutputStream fos = new FileOutputStream(binaryFile)) {
      fos.write(binaryContent);
    }

    // Execute the action
    StringValue result = EncodeFileToBase64.action(binaryFile.getAbsolutePath());

    // Verify the result
    String expectedBase64 = Base64.getEncoder().encodeToString(binaryContent);
    Assert.assertEquals(result.get(), expectedBase64,
        "The encoded Base64 string does not match the expected " +
            "value for binary file");

    // Clean up
    Files.deleteIfExists(binaryFile.toPath());
  }

  @Test
  public void testEncodeEmptyFile() throws IOException {
    // Create an empty file
    File emptyFile = tempDir.resolve("empty-file.txt").toFile();
    emptyFile.createNewFile();

    // Execute the action
    StringValue result = EncodeFileToBase64.action(emptyFile.getAbsolutePath());

    // Verify the result - empty file should result in empty base64 string
    Assert.assertEquals(result.get(), "",
        "The encoded Base64 string for an empty file should be empty");

    // Clean up
    Files.deleteIfExists(emptyFile.toPath());
  }

  @Test
  public void testEncodeLargeFile() throws IOException {
    // Create a large file (1MB)
    File largeFile = tempDir.resolve("large-file.dat").toFile();
    byte[] largeContent = new byte[1024 * 1024]; // 1MB
    for (int i = 0; i < largeContent.length; i++) {
      largeContent[i] = (byte) (i % 256);
    }

    try (FileOutputStream fos = new FileOutputStream(largeFile)) {
      fos.write(largeContent);
    }

    // Execute the action
    StringValue result = EncodeFileToBase64.action(largeFile.getAbsolutePath());

    // Verify the result length (since checking the entire content would be unwieldy)
    String expectedBase64 = Base64.getEncoder().encodeToString(largeContent);
    Assert.assertEquals(result.get().length(), expectedBase64.length(),
        "The encoded Base64 string length does not match for large file");

    // Clean up
    Files.deleteIfExists(largeFile.toPath());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNonExistentFile() {
    // Attempt to encode a non-existent file
    EncodeFileToBase64.action(tempDir.resolve("non-existent-file.txt").toString());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testDirectory() {
    // Attempt to encode a directory
    EncodeFileToBase64.action(tempDir.toString());
  }

}