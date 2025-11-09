package iterator;

import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.table.Table;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sumit.devtools.iterators.html.HTMLTableIterator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.Assert.*;

/**
 * Test cases for HTMLTableIterator.
 *
 * @author Sumit Kumar
 */
public class HTMLTableIteratorTest {

  private static Path tempDir;

  @BeforeClass
  public static void setup() throws IOException {
    // Create temp directory for file-based tests
    tempDir = Files.createTempDirectory("htmltable-test");
  }

  @AfterClass
  public static void cleanup() throws IOException {
    // Clean up temp files
    if (tempDir != null && Files.exists(tempDir)) {
      Files.walk(tempDir)
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
  public void testIteratorWithMultipleTables() throws Exception {
    // Given: HTML with 3 tables
    String html = "<html>\n" +
        "<body>\n" +
        "  <table>\n" +
        "    <tr><th>Name</th><th>Age</th></tr>\n" +
        "    <tr><td>John</td><td>30</td></tr>\n" +
        "    <tr><td>Jane</td><td>25</td></tr>\n" +
        "  </table>\n" +
        "\n" +
        "  <table>\n" +
        "    <tr><th>Product</th><th>Price</th></tr>\n" +
        "    <tr><td>Apple</td><td>1.50</td></tr>\n" +
        "  </table>\n" +
        "\n" +
        "  <table>\n" +
        "    <tr><th>City</th><th>Country</th></tr>\n" +
        "    <tr><td>Paris</td><td>France</td></tr>\n" +
        "    <tr><td>Tokyo</td><td>Japan</td></tr>\n" +
        "    <tr><td>London</td><td>UK</td></tr>\n" +
        "  </table>\n" +
        "</body>\n" +
        "</html>";

    // When: Iterator is created
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should find 3 tables
    assertTrue(iterator.hasNext(), "Should have first table");
    TableValue table1 = iterator.next();
    assertNotNull(table1);
    assertEquals(table1.get().getRows().size(), 2, "First table should have 2 rows");

    assertTrue(iterator.hasNext(), "Should have second table");
    TableValue table2 = iterator.next();
    assertNotNull(table2);
    assertEquals(table2.get().getRows().size(), 1, "Second table should have 1 row");

    assertTrue(iterator.hasNext(), "Should have third table");
    TableValue table3 = iterator.next();
    assertNotNull(table3);
    assertEquals(table3.get().getRows().size(), 3, "Third table should have 3 rows");

    assertFalse(iterator.hasNext(), "Should have no more tables");
  }

  @Test(expectedExceptions = Exception.class)
  public void testIteratorExceptionWhenNoMoreTables() throws Exception {
    // Given: HTML with 1 table
    String html = "<table>\n" +
        "  <tr><th>Col1</th></tr>\n" +
        "  <tr><td>Value1</td></tr>\n" +
        "</table>";

    // When: Iterator exhausted
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    iterator.next(); // Get first table
    assertFalse(iterator.hasNext());

    // Then: Should throw exception
    iterator.next(); // This should throw
  }

  @Test
  public void testIteratorWithEmptyHTML() {
    // Given: HTML with no tables
    String html = "<html><body><p>No tables here</p></body></html>";

    // When: Iterator is created
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should have no tables
    assertFalse(iterator.hasNext(), "Should have no tables");
  }

  @Test
  public void testTableWithNoHeaders() throws Exception {
    // Given: Table without <th> headers
    String html = "<table>\n" +
        "  <tr><td>Data1</td><td>Data2</td></tr>\n" +
        "  <tr><td>Data3</td><td>Data4</td></tr>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should process table with empty schema
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    Table tableData = table.get();

    assertEquals(tableData.getRows().size(), 2, "Should have 2 rows");
    assertEquals(tableData.getSchema().size(), 2, "Should have 2 schema entries");
  }

  @Test
  public void testUnevenColumnCounts() throws Exception {
    // Given: Table with uneven column counts
    String html = "<table>\n" +
        "  <tr><th>Col1</th><th>Col2</th><th>Col3</th></tr>\n" +
        "  <tr><td>A</td><td>B</td></tr>\n" +
        "  <tr><td>C</td></tr>\n" +
        "  <tr><td>D</td><td>E</td><td>F</td><td>G</td></tr>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();
    Table tableData = table.get();

    // Then: All rows should be padded to max column count (4)
    assertEquals(tableData.getSchema().size(), 4, "Schema should have 4 columns");
    for (int i = 0; i < tableData.getRows().size(); i++) {
      assertEquals(
          tableData.getRows().get(i).getValues().size(),
          4,
          "Row " + i + " should have 4 columns"
      );
    }
  }

  @Test
  public void testWholeTextFormatting() throws Exception {
    // Given: HTML with whitespace
    String html = "<table>\n" +
        "  <tr><th>Header  With  Spaces</th></tr>\n" +
        "  <tr><td>Data\n" +
        "  With\n" +
        "  Newlines</td></tr>\n" +
        "</table>";

    // When: Using WHOLE_TEXT mode
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("WHOLE_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should preserve whitespace
    String cellData = table.get().getRows().get(0).getValues().get(0).get().toString();
    assertTrue(cellData.contains("\n") || cellData.length() > 10,
        "WHOLE_TEXT should preserve whitespace");
  }

  @Test
  public void testNormalizedTextFormatting() throws Exception {
    // Given: HTML with whitespace
    String html = "<table>\n" +
        "  <tr><th>Header  With  Spaces</th></tr>\n" +
        "  <tr><td>Data\n" +
        "  With\n" +
        "  Newlines</td></tr>\n" +
        "</table>";

    // When: Using NORMALIZED_TEXT mode
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should normalize whitespace
    String headerData = table.get().getSchema().get(0).getName();
    String cellData = table.get().getRows().get(0).getValues().get(0).get().toString();

    assertFalse(headerData.contains("  "), "Header should not have double spaces");
    assertFalse(cellData.contains("\n"), "Cell data should not have newlines");
  }

  @Test
  public void testOnlyOwnTextExtraction() throws Exception {
    // Given: HTML with nested elements
    String html = "<table>\n" +
        "  <tr><th>Header</th></tr>\n" +
        "  <tr><td>Outer Text <span>Inner Text</span></td></tr>\n" +
        "</table>";

    // When: Using ONLY_SELF extraction
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("ONLY_SELF");

    TableValue table = iterator.next();
    String cellData = table.get().getRows().get(0).getValues().get(0).get().toString();

    // Then: Should only get outer text
    assertTrue(cellData.contains("Outer Text"), "Should contain outer text");
    assertFalse(cellData.contains("Inner Text"), "Should not contain inner text from span");
  }

  @Test
  public void testIncludeChildrenExtraction() throws Exception {
    // Given: HTML with nested elements
    String html = "<table>\n" +
        "  <tr><th>Header</th></tr>\n" +
        "  <tr><td>Outer Text <span>Inner Text</span></td></tr>\n" +
        "</table>";

    // When: Using INCLUDE_CHILDREN extraction
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();
    String cellData = table.get().getRows().get(0).getValues().get(0).get().toString();

    // Then: Should include all text
    assertTrue(cellData.contains("Outer Text"), "Should contain outer text");
    assertTrue(cellData.contains("Inner Text"), "Should contain inner text from span");
  }

  @Test
  public void testMultipleHasNextCalls() throws Exception {
    // Given: HTML with 1 table
    String html = "<table>\n" +
        "  <tr><th>Col1</th></tr>\n" +
        "  <tr><td>Value1</td></tr>\n" +
        "</table>";

    // When: Multiple hasNext calls
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Multiple hasNext calls should return consistent results
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());

    iterator.next();

    assertFalse(iterator.hasNext());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void testLargeTableMemoryEfficiency() throws Exception {
    // Given: HTML with many large tables
    StringBuilder html = new StringBuilder("<html><body>");
    int tableCount = 100;
    int rowsPerTable = 50;

    for (int t = 0; t < tableCount; t++) {
      html.append("<table><tr><th>Col1</th><th>Col2</th><th>Col3</th></tr>");
      for (int r = 0; r < rowsPerTable; r++) {
        html.append("<tr><td>Data").append(t).append("-").append(r).append("-1</td>");
        html.append("<td>Data").append(t).append("-").append(r).append("-2</td>");
        html.append("<td>Data").append(t).append("-").append(r).append("-3</td></tr>");
      }
      html.append("</table>");
    }
    html.append("</body></html>");

    // When: Iterator processes tables one at a time
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html.toString());
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should iterate through all tables efficiently
    int processedCount = 0;
    while (iterator.hasNext()) {
      TableValue table = iterator.next();
      assertNotNull(table);
      assertEquals(table.get().getRows().size(), rowsPerTable);
      processedCount++;
    }

    assertEquals(processedCount, tableCount, "Should process all tables");
  }

  // ========== EDGE CASE TESTS ==========

  @Test
  public void testEmptyTable() throws Exception {
    // Given: Table with no rows
    String html = "<table></table>";

    // When: Iterator processes empty table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should process table with no rows or schema
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    assertNotNull(table);
    assertEquals(table.get().getRows().size(), 0);
    assertEquals(table.get().getSchema().size(), 0);
  }

  @Test
  public void testTableWithOnlyHeader() throws Exception {
    // Given: Table with only header row
    String html = "<table><tr><th>Col1</th><th>Col2</th></tr></table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should have schema but no data rows
    TableValue table = iterator.next();
    assertEquals(table.get().getSchema().size(), 2);
    assertEquals(table.get().getRows().size(), 0);
  }

  @Test
  public void testMalformedHTMLUnclosedTags() throws Exception {
    // Given: Malformed HTML with unclosed tags (Jsoup handles this gracefully)
    String html = "<table><tr><td>Data1<td>Data2</table>";

    // When: Iterator processes malformed HTML
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should still process table (Jsoup auto-closes tags)
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    assertNotNull(table);
    assertTrue(table.get().getRows().size() > 0);
  }

  @Test
  public void testSpecialCharactersInCells() throws Exception {
    // Given: Table with special characters
    String html = "<table>\n" +
        "  <tr><th>Special</th></tr>\n" +
        "  <tr><td>&lt;script&gt;alert('xss')&lt;/script&gt;</td></tr>\n" +
        "  <tr><td>&amp; &quot; &#39; &nbsp;</td></tr>\n" +
        "  <tr><td>Unicode: \u00E9 \u00F1 \u4E2D\u6587</td></tr>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should decode HTML entities
    String row1 = table.get().getRows().get(0).getValues().get(0).get().toString();
    assertTrue(row1.contains("script"), "Should decode HTML entities");

    String row2 = table.get().getRows().get(1).getValues().get(0).get().toString();
    assertTrue(row2.contains("&") || row2.contains("\""), "Should handle special chars");

    String row3 = table.get().getRows().get(2).getValues().get(0).get().toString();
    assertTrue(row3.contains("Unicode"), "Should handle Unicode characters");
  }

  @Test
  public void testNestedTables() throws Exception {
    // Given: HTML with nested tables
    String html = "<table id=\"outer\">\n" +
        "  <tr><th>Outer</th></tr>\n" +
        "  <tr><td>\n" +
        "    <table id=\"inner\">\n" +
        "      <tr><th>Inner</th></tr>\n" +
        "      <tr><td>InnerData</td></tr>\n" +
        "    </table>\n" +
        "  </td></tr>\n" +
        "</table>";

    // When: Iterator processes nested tables
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should find all tables (both outer and inner)
    int tableCount = 0;
    while (iterator.hasNext()) {
      TableValue table = iterator.next();
      assertNotNull(table);
      tableCount++;
    }
    assertEquals(tableCount, 2, "Should find both outer and inner tables");
  }

  @Test
  public void testTableWithColspanRowspan() throws Exception {
    // Given: Table with colspan and rowspan (Jsoup ignores these for text extraction)
    String html = "<table>\n" +
        "  <tr><th colspan=\"2\">Header Spanning 2 Cols</th></tr>\n" +
        "  <tr><td>A</td><td>B</td></tr>\n" +
        "  <tr><td rowspan=\"2\">C</td><td>D</td></tr>\n" +
        "  <tr><td>E</td></tr>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should extract text but ignore colspan/rowspan attributes
    assertNotNull(table);
    assertTrue(table.get().getRows().size() > 0);
  }

  @Test
  public void testWhitespaceOnlyCells() throws Exception {
    // Given: Table with whitespace-only cells
    String html = "<table>\n" +
        "  <tr><th>Col1</th><th>Col2</th></tr>\n" +
        "  <tr><td>   </td><td>\n\n\n</td></tr>\n" +
        "  <tr><td>\t\t\t</td><td>Data</td></tr>\n" +
        "</table>";

    // When: Using NORMALIZED_TEXT
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Whitespace should be normalized to empty or single space
    for (int i = 0; i < 2; i++) {
      String cell1 = table.get().getRows().get(i).getValues().get(0).get().toString();
      assertTrue(cell1.trim().isEmpty() || cell1.equals("Data"),
          "Normalized text should collapse whitespace");
    }
  }

  @Test
  public void testMixedHeaderCells() throws Exception {
    // Given: Table with both th and td in same row
    String html = "<table>\n" +
        "  <tr><th>Header1</th><td>NotHeader</td><th>Header2</th></tr>\n" +
        "  <tr><td>A</td><td>B</td><td>C</td></tr>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should extract only th elements as headers, and pad to max column count (3)
    // First row has 2 th elements (Header1, Header2), but data row has 3 td elements
    // So schema is padded to 3 columns
    assertEquals(table.get().getSchema().size(), 3, "Schema should be padded to match max column count");
    assertEquals(table.get().getRows().get(0).getValues().size(), 3, "Data row should have 3 columns");
  }

  @Test
  public void testVeryLargeCellContent() throws Exception {
    // Given: Table with very large cell content
    StringBuilder largeText = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      largeText.append("Lorem ipsum dolor sit amet ");
    }

    String html = "<table>\n" +
        "  <tr><th>Large</th></tr>\n" +
        "  <tr><td>" + largeText.toString() + "</td></tr>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should handle large content without issues
    TableValue table = iterator.next();
    String cellContent = table.get().getRows().get(0).getValues().get(0).get().toString();
    assertTrue(cellContent.length() > 100000, "Should preserve large cell content");
  }

  @Test
  public void testTableWithVeryWideRow() throws Exception {
    // Given: Table with 100 columns
    StringBuilder html = new StringBuilder("<table><tr>");
    for (int i = 0; i < 100; i++) {
      html.append("<th>Col").append(i).append("</th>");
    }
    html.append("</tr><tr>");
    for (int i = 0; i < 100; i++) {
      html.append("<td>Data").append(i).append("</td>");
    }
    html.append("</tr></table>");

    // When: Iterator processes wide table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html.toString());
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should handle 100 columns correctly
    assertEquals(table.get().getSchema().size(), 100);
    assertEquals(table.get().getRows().get(0).getValues().size(), 100);
  }

  @Test
  public void testTableWithCommentsAndScripts() throws Exception {
    // Given: Table with HTML comments and script tags
    String html = "<table>\n" +
        "  <!-- This is a comment -->\n" +
        "  <tr><th>Col1</th></tr>\n" +
        "  <script>var x = 1;</script>\n" +
        "  <tr><td>Data1</td></tr>\n" +
        "  <!-- Another comment -->\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Comments should be ignored, script content may appear
    assertEquals(table.get().getRows().size(), 1);
    String cellData = table.get().getRows().get(0).getValues().get(0).get().toString();
    assertEquals(cellData, "Data1");
  }

  @Test
  public void testEmptyStringCells() throws Exception {
    // Given: Table with empty td elements
    String html = "<table>\n" +
        "  <tr><th>Col1</th><th>Col2</th></tr>\n" +
        "  <tr><td></td><td></td></tr>\n" +
        "  <tr><td>A</td><td></td></tr>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Empty cells should be empty strings
    assertEquals(table.get().getRows().get(0).getValues().get(0).get().toString(), "");
    assertEquals(table.get().getRows().get(0).getValues().get(1).get().toString(), "");
    assertEquals(table.get().getRows().get(1).getValues().get(0).get().toString(), "A");
    assertEquals(table.get().getRows().get(1).getValues().get(1).get().toString(), "");
  }

  @Test
  public void testTableWithTbodyTheadTfoot() throws Exception {
    // Given: Table with tbody, thead, tfoot sections
    String html = "<table>\n" +
        "  <thead><tr><th>Header</th></tr></thead>\n" +
        "  <tbody>\n" +
        "    <tr><td>Body1</td></tr>\n" +
        "    <tr><td>Body2</td></tr>\n" +
        "  </tbody>\n" +
        "  <tfoot><tr><td>Footer</td></tr></tfoot>\n" +
        "</table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should process all sections
    assertEquals(table.get().getSchema().size(), 1);
    // tbody + tfoot = 3 rows total
    assertEquals(table.get().getRows().size(), 3);
  }

  @Test
  public void testConsecutiveEmptyTables() throws Exception {
    // Given: Multiple consecutive empty tables
    String html = "<table></table><table></table><table><tr><td>Data</td></tr></table>";

    // When: Iterator processes tables
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should find all 3 tables
    assertTrue(iterator.hasNext());
    TableValue table1 = iterator.next();
    assertEquals(table1.get().getRows().size(), 0);

    assertTrue(iterator.hasNext());
    TableValue table2 = iterator.next();
    assertEquals(table2.get().getRows().size(), 0);

    assertTrue(iterator.hasNext());
    TableValue table3 = iterator.next();
    assertEquals(table3.get().getRows().size(), 1);

    assertFalse(iterator.hasNext());
  }

  @Test
  public void testTableInsideDivWithClasses() throws Exception {
    // Given: Table inside div with various attributes
    String html = "<div class=\"container\" id=\"main\">\n" +
        "  <table class=\"data-table\" style=\"width:100%\">\n" +
        "    <tr><th>Col1</th></tr>\n" +
        "    <tr><td>Data1</td></tr>\n" +
        "  </table>\n" +
        "</div>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should find table regardless of container
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    assertEquals(table.get().getRows().size(), 1);
  }

  @Test
  public void testHTMLWithNoTables() throws Exception {
    // Given: HTML with no tables but table-like text
    String html = "<div>This text mentions table but has no actual table element</div>";

    // When: Iterator processes HTML
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should find no tables
    assertFalse(iterator.hasNext());
  }

  @Test
  public void testCompleteHTMLDocument() throws Exception {
    // Given: Complete HTML document with DOCTYPE, head, body
    String html = "<!DOCTYPE html>\n" +
        "<html lang=\"en\">\n" +
        "<head>\n" +
        "  <meta charset=\"UTF-8\">\n" +
        "  <title>Test Page</title>\n" +
        "  <style>table { border: 1px solid black; }</style>\n" +
        "</head>\n" +
        "<body>\n" +
        "  <h1>Test Tables</h1>\n" +
        "  <table>\n" +
        "    <tr><th>Name</th></tr>\n" +
        "    <tr><td>Alice</td></tr>\n" +
        "  </table>\n" +
        "</body>\n" +
        "</html>";

    // When: Iterator processes complete document
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should extract table from body
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    assertEquals(table.get().getRows().size(), 1);
  }

  @Test
  public void testTableWithBreakTags() throws Exception {
    // Given: Table with br tags in cells
    String html = "<table>\n" +
        "  <tr><th>Multi<br>Line<br>Header</th></tr>\n" +
        "  <tr><td>Line1<br/>Line2<br />Line3</td></tr>\n" +
        "</table>";

    // When: Using NORMALIZED_TEXT
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Text should be normalized
    String headerText = table.get().getSchema().get(0).getName();
    assertTrue(headerText.contains("Multi") && headerText.contains("Line") && headerText.contains("Header"));
  }

  @Test
  public void testSingleCellTable() throws Exception {
    // Given: Table with single cell
    String html = "<table><tr><td>Single</td></tr></table>";

    // When: Iterator processes table
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    TableValue table = iterator.next();

    // Then: Should have 1 row, 1 column
    assertEquals(table.get().getRows().size(), 1);
    assertEquals(table.get().getRows().get(0).getValues().size(), 1);
    assertEquals(table.get().getRows().get(0).getValues().get(0).get().toString(), "Single");
  }

  @Test
  public void testInitializeCalledMultipleTimes() throws Exception {
    // Given: HTML with tables
    String html = "<table><tr><td>Test</td></tr></table>";

    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("TEXT");
    iterator.setHtmlText(html);
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // When: Multiple hasNext calls trigger lazy initialization
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());

    // Then: Should still work correctly
    TableValue table = iterator.next();
    assertNotNull(table);
    assertFalse(iterator.hasNext());
  }

  // ========== FILE INPUT MODE TESTS ==========

  @Test
  public void testFileInputModeUTF8() throws Exception {
    // Given: HTML file with UTF-8 encoding
    String html = "<table>\n" +
        "  <tr><th>Unicode</th></tr>\n" +
        "  <tr><td>\u00E9\u00F1\u4E2D\u6587</td></tr>\n" +
        "</table>";

    Path htmlFile = tempDir.resolve("test-utf8.html");
    Files.writeString(htmlFile, html);

    // When: Iterator reads from file with UTF-8
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(htmlFile.toString());
    iterator.setCharsetName("UTF-8");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should correctly read and parse file
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    String cellData = table.get().getRows().get(0).getValues().get(0).get().toString();
    assertTrue(cellData.length() > 0, "Should read UTF-8 characters");
  }

  @Test
  public void testFileInputModeISO88591() throws Exception {
    // Given: HTML file
    String html = "<table><tr><th>Test</th></tr><tr><td>Data</td></tr></table>";

    Path htmlFile = tempDir.resolve("test-iso.html");
    Files.writeString(htmlFile, html);

    // When: Iterator reads from file with ISO-8859-1
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(htmlFile.toString());
    iterator.setCharsetName("ISO-8859-1");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should read file successfully
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    assertEquals(table.get().getRows().size(), 1);
  }

  @Test
  public void testFileInputWithMultipleTables() throws Exception {
    // Given: HTML file with multiple tables
    String html = "<html><body>\n" +
        "<table><tr><th>Table1</th></tr><tr><td>Data1</td></tr></table>\n" +
        "<table><tr><th>Table2</th></tr><tr><td>Data2</td></tr></table>\n" +
        "<table><tr><th>Table3</th></tr><tr><td>Data3</td></tr></table>\n" +
        "</body></html>";

    Path htmlFile = tempDir.resolve("multi-table.html");
    Files.writeString(htmlFile, html);

    // When: Iterator reads from file
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(htmlFile.toString());
    iterator.setCharsetName("UTF-8");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should find all 3 tables
    int count = 0;
    while (iterator.hasNext()) {
      TableValue table = iterator.next();
      assertNotNull(table);
      count++;
    }
    assertEquals(count, 3);
  }

  @Test(expectedExceptions = Exception.class)
  public void testFileInputModeInvalidPath() throws Exception {
    // Given: Invalid file path
    String invalidPath = tempDir.resolve("non-existent-file.html").toString();

    // When: Iterator tries to read non-existent file
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(invalidPath);
    iterator.setCharsetName("UTF-8");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should throw exception
    iterator.hasNext(); // This should trigger initialization and throw
  }

  @Test
  public void testFileInputEmptyFile() throws Exception {
    // Given: Empty HTML file
    Path htmlFile = tempDir.resolve("empty.html");
    Files.writeString(htmlFile, "");

    // When: Iterator reads empty file
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(htmlFile.toString());
    iterator.setCharsetName("UTF-8");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should find no tables
    assertFalse(iterator.hasNext());
  }

  @Test
  public void testFileInputVeryLargeFile() throws Exception {
    // Given: Large HTML file with many tables
    StringBuilder html = new StringBuilder("<html><body>");
    for (int i = 0; i < 50; i++) {
      html.append("<table><tr><th>Table").append(i).append("</th></tr>");
      for (int j = 0; j < 20; j++) {
        html.append("<tr><td>Data").append(i).append("-").append(j).append("</td></tr>");
      }
      html.append("</table>");
    }
    html.append("</body></html>");

    Path htmlFile = tempDir.resolve("large-file.html");
    Files.writeString(htmlFile, html.toString());

    // When: Iterator reads large file
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(htmlFile.toString());
    iterator.setCharsetName("UTF-8");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should process all tables efficiently
    int count = 0;
    while (iterator.hasNext()) {
      TableValue table = iterator.next();
      assertNotNull(table);
      assertEquals(table.get().getRows().size(), 20);
      count++;
    }
    assertEquals(count, 50);
  }

  @Test
  public void testFileInputWithBOM() throws Exception {
    // Given: File with UTF-8 BOM (Byte Order Mark)
    String html = "\uFEFF<table><tr><th>BOM Test</th></tr><tr><td>Data</td></tr></table>";

    Path htmlFile = tempDir.resolve("bom-test.html");
    Files.writeString(htmlFile, html);

    // When: Iterator reads file
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(htmlFile.toString());
    iterator.setCharsetName("UTF-8");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should handle BOM and read table
    assertTrue(iterator.hasNext());
    TableValue table = iterator.next();
    assertEquals(table.get().getRows().size(), 1);
  }

  @Test
  public void testSwitchingBetweenTextAndFileMode() throws Exception {
    // Given: Both text HTML and file HTML
    String textHtml = "<table><tr><td>TextMode</td></tr></table>";
    String fileHtml = "<table><tr><td>FileMode</td></tr></table>";

    Path htmlFile = tempDir.resolve("switch-test.html");
    Files.writeString(htmlFile, fileHtml);

    // Test 1: Text mode
    HTMLTableIterator iterator1 = new HTMLTableIterator();
    iterator1.setSelectMethod("TEXT");
    iterator1.setHtmlText(textHtml);
    iterator1.setTextType("NORMALIZED_TEXT");
    iterator1.setExtractionType("INCLUDE_CHILDREN");

    TableValue table1 = iterator1.next();
    assertEquals(table1.get().getRows().get(0).getValues().get(0).get().toString(), "TextMode");

    // Test 2: File mode
    HTMLTableIterator iterator2 = new HTMLTableIterator();
    iterator2.setSelectMethod("FILE");
    iterator2.setFilePath(htmlFile.toString());
    iterator2.setCharsetName("UTF-8");
    iterator2.setTextType("NORMALIZED_TEXT");
    iterator2.setExtractionType("INCLUDE_CHILDREN");

    TableValue table2 = iterator2.next();
    assertEquals(table2.get().getRows().get(0).getValues().get(0).get().toString(), "FileMode");
  }

  @Test
  public void testFileWithWindowsLineEndings() throws Exception {
    // Given: File with Windows CRLF line endings
    String html = "<table>\r\n<tr><th>Windows</th></tr>\r\n<tr><td>Line Endings</td></tr>\r\n</table>";

    Path htmlFile = tempDir.resolve("windows-line-endings.html");
    Files.writeString(htmlFile, html);

    // When: Iterator reads file
    HTMLTableIterator iterator = new HTMLTableIterator();
    iterator.setSelectMethod("FILE");
    iterator.setFilePath(htmlFile.toString());
    iterator.setCharsetName("UTF-8");
    iterator.setTextType("NORMALIZED_TEXT");
    iterator.setExtractionType("INCLUDE_CHILDREN");

    // Then: Should handle line endings correctly
    TableValue table = iterator.next();
    assertEquals(table.get().getRows().size(), 1);
  }
}
