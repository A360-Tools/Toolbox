package file;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.file.GetTextFromFile;

/**
 * @author Sumit Kumar
 */
public class GetTextFromFileTest {

  private File testFile;

  @BeforeMethod
  public void setUp() throws IOException {
    // Create a temporary test file
    testFile = File.createTempFile("testfile", ".txt");
    testFile.deleteOnExit();

    // Write test content to the file
    try (FileWriter writer = new FileWriter(testFile)) {
      writer.write("  This is test content.\nWith multiple lines.  ");
    }
  }

  @AfterMethod
  public void tearDown() {
    if (testFile != null && testFile.exists()) {
      testFile.delete();
    }
  }

  @Test
  public void testReadFileWithDefaultOptions() {
    StringValue result = GetTextFromFile.action(testFile.getAbsolutePath(), "UTF-8", false, false);
    Assert.assertEquals(result.get(), "  This is test content.\nWith multiple lines.  ");
  }

  @Test
  public void testReadFileWithTrimLeadingSpaces() {
    StringValue result = GetTextFromFile.action(testFile.getAbsolutePath(), "UTF-8", true, false);
    Assert.assertEquals(result.get(), "This is test content.\nWith multiple lines.  ");
  }

  @Test
  public void testReadFileWithTrimTrailingSpaces() {
    StringValue result = GetTextFromFile.action(testFile.getAbsolutePath(), "UTF-8", false, true);
    Assert.assertEquals(result.get(), "  This is test content.\nWith multiple lines.");
  }

  @Test
  public void testReadFileWithBothTrimOptions() {
    StringValue result = GetTextFromFile.action(testFile.getAbsolutePath(), "UTF-8", true, true);
    Assert.assertEquals(result.get(), "This is test content.\nWith multiple lines.");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testReadNonExistentFile() {
    GetTextFromFile.action("non_existent_file.txt", "UTF-8", false, false);
  }

  @Test
  public void testWithDifferentCharset() throws IOException {
    // Create a file with a specific encoding
    String content = "こんにちは世界"; // Hello World in Japanese
    Path tempFile = Files.createTempFile("utf16_test", ".txt");
    Files.writeString(tempFile, content, StandardCharsets.UTF_16);
    tempFile.toFile().deleteOnExit();

    // Read with UTF-16 encoding
    StringValue result = GetTextFromFile.action(tempFile.toString(), "UTF-16", false, false);
    Assert.assertEquals(result.get(), content);
  }

}