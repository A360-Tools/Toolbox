package file;

import com.automationanywhere.botcommand.exception.BotCommandException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.file.DecodeBase64ToFile;

/**
 * Test class for the DecodeBase64ToFile action
 */
public class DecodeBase64ToFileTest {

  private Path tempDir;
  private String testContent;
  private String base64Content;

  @BeforeMethod
  public void setUp() throws IOException {
    tempDir = Files.createTempDirectory("file-tests");

    // Create test content and its Base64 encoded version
    testContent = "This is a test file content for base64 decoding test.";
    base64Content = Base64.getEncoder()
        .encodeToString(testContent.getBytes(StandardCharsets.UTF_8));
  }

  @AfterMethod
  public void tearDown() throws IOException {
    // Clean up temporary directory
    Files.walk(tempDir)
        .sorted((p1, p2) -> -p1.compareTo(p2))
        .forEach(p -> {
          try {
            Files.deleteIfExists(p);
          } catch (IOException e) {
            // Ignore, best effort cleanup
          }
        });
  }

  @Test
  public void testDecodeToNewFile() throws IOException {
    // Define output file path
    String outputFilePath = tempDir.resolve("decoded-file.txt").toString();

    // Execute the action
    DecodeBase64ToFile.action(base64Content, outputFilePath, true);

    // Verify the file was created and contains the expected content
    File outputFile = new File(outputFilePath);
    Assert.assertTrue(outputFile.exists(), "Output file should exist");
    String decodedContent = Files.readString(outputFile.toPath());
    Assert.assertEquals(decodedContent, testContent,
        "The decoded file content does not match the expected value");
  }

  @Test
  public void testDecodeToExistingFileWithOverwrite() throws IOException {
    // Create an existing file
    Path existingFilePath = tempDir.resolve("existing-file.txt");
    Files.write(existingFilePath, "Original content".getBytes(StandardCharsets.UTF_8));

    // Execute the action with overwrite=true
    DecodeBase64ToFile.action(base64Content, existingFilePath.toString(), true);

    // Verify the file was overwritten with the new content
    String decodedContent = Files.readString(existingFilePath);
    Assert.assertEquals(decodedContent, testContent, "The file content should be overwritten");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testDecodeToExistingFileWithoutOverwrite() throws IOException {
    // Create an existing file
    Path existingFilePath = tempDir.resolve("existing-file-no-overwrite.txt");
    Files.write(existingFilePath, "Original content".getBytes(StandardCharsets.UTF_8));

    // Execute the action with overwrite=false
    // Should throw exception since file exists and overwrite is false
    DecodeBase64ToFile.action(base64Content, existingFilePath.toString(), false);
  }

  @Test
  public void testDecodeToNestedDirectoryPath() throws IOException {
    // Define a nested directory path that doesn't exist yet
    Path nestedDir = tempDir.resolve("nested/dir/path");
    String outputFilePath = nestedDir.resolve("decoded-file.txt").toString();

    // Execute the action - should create the directory structure
    DecodeBase64ToFile.action(base64Content, outputFilePath, true);

    // Verify the file was created with correct content
    File outputFile = new File(outputFilePath);
    Assert.assertTrue(outputFile.exists(), "Output file should exist in nested directory");
    String decodedContent = Files.readString(outputFile.toPath());
    Assert.assertEquals(decodedContent, testContent,
        "The decoded file content in nested path does not match");
  }

  @Test
  public void testDecodeBinaryContent() throws IOException {
    // Create binary content
    byte[] binaryContent = new byte[100];
    for (int i = 0; i < binaryContent.length; i++) {
      binaryContent[i] = (byte) i;
    }
    String binaryBase64 = Base64.getEncoder().encodeToString(binaryContent);

    // Define output file path
    String outputFilePath = tempDir.resolve("decoded-binary.bin").toString();

    // Execute the action
    DecodeBase64ToFile.action(binaryBase64, outputFilePath, true);

    // Verify the binary file was created with correct content
    File outputFile = new File(outputFilePath);
    Assert.assertTrue(outputFile.exists(), "Binary output file should exist");
    byte[] decodedBytes = Files.readAllBytes(outputFile.toPath());
    Assert.assertEquals(decodedBytes.length, binaryContent.length, "Binary file size should match");

    // Compare each byte
    for (int i = 0; i < binaryContent.length; i++) {
      Assert.assertEquals(decodedBytes[i], binaryContent[i],
          "Binary content mismatch at index " + i);
    }
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testDecodeInvalidBase64() {
    // Invalid Base64 string (not properly padded)
    String invalidBase64 = "ThisIsNotValidBase64!@#$";

    // Execute the action
    DecodeBase64ToFile.action(invalidBase64, tempDir.resolve("invalid-output.txt").toString(),
        true);
  }

  @Test
  public void testDecodeEmptyBase64() throws IOException {
    // Empty Base64 string
    String emptyBase64 = "";

    // Define output file path
    String outputFilePath = tempDir.resolve("empty-file.txt").toString();

    // Execute the action
    DecodeBase64ToFile.action(emptyBase64, outputFilePath, true);

    // Verify the file was created and is empty
    File outputFile = new File(outputFilePath);
    Assert.assertTrue(outputFile.exists(), "Empty output file should exist");
    Assert.assertEquals(Files.size(outputFile.toPath()), 0, "File should be empty");
  }

}