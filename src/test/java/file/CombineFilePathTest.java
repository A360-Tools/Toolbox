package file;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.FileValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.file.CombineFilePath;

/**
 * Test class for the CombineFilePath action
 */
public class CombineFilePathTest {

    private String baseDir;
  private List<Value> pathSegments;

  @BeforeMethod
  public void setUp() {

    // Use system temp directory as base
    baseDir = System.getProperty("java.io.tmpdir");

    // Create path segments
    pathSegments = new ArrayList<>();
    pathSegments.add(new StringValue("folder1"));
    pathSegments.add(new StringValue("folder2"));
    pathSegments.add(new StringValue("test.txt"));
  }

  @Test
  public void testBasicPathCombination() {
    // Execute
    FileValue result = CombineFilePath.action(baseDir, pathSegments);

    // Expected path
    Path expectedPath = Paths.get(baseDir, "folder1", "folder2", "test.txt");

    // Verify
    Assert.assertEquals(result.get(), expectedPath.toString());
  }


  @Test
  public void testEmptySegmentsAreSkipped() {
    // Create path segments with empty strings
    List<Value> segmentsWithEmpty = new ArrayList<>();
    segmentsWithEmpty.add(new StringValue("folder1"));
    segmentsWithEmpty.add(new StringValue(""));
    segmentsWithEmpty.add(new StringValue("   "));  // Just spaces
    segmentsWithEmpty.add(new StringValue("folder3"));

    // Execute
    FileValue result = CombineFilePath.action(baseDir, segmentsWithEmpty);

    // Expected path - empty segments should be skipped
    Path expectedPath = Paths.get(baseDir, "folder1", "folder3");

    // Verify
    Assert.assertEquals(result.get(), expectedPath.toString());
  }

  @Test
  public void testNullSegmentsAreSkipped() {
    // Create path segments with null values
    List<Value> segmentsWithNull = new ArrayList<>();
    segmentsWithNull.add(new StringValue("folder1"));
    segmentsWithNull.add(null);  // This should be skipped
    segmentsWithNull.add(new StringValue("folder3"));

    // Execute
    FileValue result = CombineFilePath.action(baseDir, segmentsWithNull);

    // Expected path - null segments should be skipped
    Path expectedPath = Paths.get(baseDir, "folder1", "folder3");

    // Verify
    Assert.assertEquals(result.get(), expectedPath.toString());
  }

  @Test
  public void testAbsolutePathSegment() {
    // Create a segment with absolute path
    Path absolutePath = Paths.get(System.getProperty("java.io.tmpdir"), "absolute");

    List<Value> segmentsWithAbsolute = new ArrayList<>();
    segmentsWithAbsolute.add(new StringValue("folder1"));
    segmentsWithAbsolute.add(new StringValue(absolutePath.toString()));
    segmentsWithAbsolute.add(new StringValue("final.txt"));

    // Execute
    FileValue result = CombineFilePath.action(baseDir, segmentsWithAbsolute);

    // When an absolute path is encountered in a resolve operation,
    // it replaces the entire path built so far
    Path expectedPath = Paths.get(absolutePath.toString(), "final.txt");

    // Verify
    Assert.assertEquals(result.get(), expectedPath.toString());
  }

  @Test
  public void testPathSeparatorsInSegments() {
    // Path separators in segments should be handled correctly
    List<Value> segmentsWithSeparators = new ArrayList<>();
    segmentsWithSeparators.add(new StringValue("folder1"));

    // This segment contains path separators and should be handled as a relative path
    segmentsWithSeparators.add(new StringValue("nested/path/segment"));

    // Execute
    FileValue result = CombineFilePath.action(baseDir, segmentsWithSeparators);

    // Expected path with nested segments
    Path expectedPath = Paths.get(baseDir, "folder1", "nested", "path", "segment");

    // Verify
    Assert.assertEquals(result.get(), expectedPath.toString());
  }

  @Test
  public void testValidSpecialCharacters() {
    // Create path segments with valid special characters
    List<Value> validSpecialSegments = new ArrayList<>();
    validSpecialSegments.add(new StringValue("folder with spaces"));  // spaces are valid
    validSpecialSegments.add(new StringValue("folder-with-hyphens"));  // hyphens are valid
    validSpecialSegments.add(new StringValue("folder_with_underscores"));  // underscores are valid
    validSpecialSegments.add(new StringValue("folder.with.dots"));  // dots are valid

    // Execute
    FileValue result = CombineFilePath.action(baseDir, validSpecialSegments);

    // Expected path with valid special characters
    Path expectedPath = Paths.get(baseDir, "folder with spaces", "folder-with-hyphens",
        "folder_with_underscores", "folder.with.dots");

    // Verify
    Assert.assertEquals(result.get(), expectedPath.toString());
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testInvalidCharactersThrowException() {
    // Create path segments with invalid characters for file systems
    List<Value> invalidSegments = new ArrayList<>();
    invalidSegments.add(new StringValue("folder1"));
    // Add an invalid character like tab, which throws exception on Windows
    invalidSegments.add(new StringValue("folder\twith\ttabs"));

    // This should throw an exception
    CombineFilePath.action(baseDir, invalidSegments);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testInvalidCharactersInPathSegment() {
    // Create path segments with characters invalid in most file systems
    List<Value> segmentsWithInvalidChars = new ArrayList<>();
    segmentsWithInvalidChars.add(new StringValue("folder1"));
    segmentsWithInvalidChars.add(new StringValue("folder*with?invalid:chars"));

    // This should throw an exception
    CombineFilePath.action(baseDir, segmentsWithInvalidChars);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullBasePath() {
    // This should throw an exception
    CombineFilePath.action(null, pathSegments);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testEmptyBasePath() {
    // This should throw an exception
    CombineFilePath.action("", pathSegments);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testNullPathSegments() {
    // This should throw an exception
    CombineFilePath.action(baseDir, null);
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testEmptyPathSegmentsList() {
    // This should throw an exception
    CombineFilePath.action(baseDir, new ArrayList<>());
  }

}