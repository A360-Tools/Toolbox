package table; // Ensure this matches your package structure

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.text.StringEscapeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import sumit.devtools.actions.table.ConvertTableToHTML;

public class TableToHTMLTest {

  private Table createSampleTable(int numRows, boolean withHeaders) {
    Table table = new Table();
    List<Schema> schemas = new ArrayList<>();
    if (withHeaders) {
      schemas.add(new Schema("ID"));
      schemas.add(new Schema("Name"));
      schemas.add(new Schema("Value < > & \" '"));
    }
    table.setSchema(schemas);

    List<Row> rows = new ArrayList<>();
    for (int i = 1; i <= numRows; i++) {
      List<Value> rowValues = new ArrayList<>();
      rowValues.add(new StringValue(String.valueOf(i)));
      rowValues.add(new StringValue("Name " + i));
      rowValues.add(new StringValue("Val <b>" + i + "</b>"));
      rows.add(new Row(rowValues));
    }
    table.setRows(rows);
    return table;
  }

  private Table createEmptyTable() {
    Table table = new Table();
    table.setSchema(new ArrayList<>());
    table.setRows(new ArrayList<>());
    return table;
  }

  private Table createTableWithHeadersOnly() {
    Table table = new Table();
    List<Schema> schemas = new ArrayList<>();
    schemas.add(new Schema("Col1"));
    schemas.add(new Schema("Col2"));
    table.setSchema(schemas);
    table.setRows(new ArrayList<>());
    return table;
  }

  private Table createTableWithNullCellData() {
    Table table = new Table();
    List<Schema> schemas = new ArrayList<>();
    schemas.add(new Schema("Header1"));
    schemas.add(new Schema("Header2"));
    table.setSchema(schemas);

    List<Row> rows = new ArrayList<>();
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("Data1"));
    row1Values.add(null);
    rows.add(new Row(row1Values));

    List<Value> row2Values = new ArrayList<>();
    row2Values.add(null);
    row2Values.add(new StringValue("Data4"));
    rows.add(new Row(row2Values));

    table.setRows(rows);
    return table;
  }

  // Helper to build the expected combined style string for a row
  private String buildExpectedRowStyle(String baseTrStyle, String specificEvenOrOddStyle) {
    List<String> styleParts = new ArrayList<>();
    if (baseTrStyle != null && !baseTrStyle.trim().isEmpty()) {
      String trimmed = baseTrStyle.trim();
      styleParts.add(
          trimmed.endsWith(";") ? trimmed.substring(0, trimmed.length() - 1).trim() : trimmed);
    }
    if (specificEvenOrOddStyle != null && !specificEvenOrOddStyle.trim().isEmpty()) {
      String trimmed = specificEvenOrOddStyle.trim();
      styleParts.add(
          trimmed.endsWith(";") ? trimmed.substring(0, trimmed.length() - 1).trim() : trimmed);
    }
    return styleParts.stream().filter(s -> s != null && !s.isEmpty())
        .collect(Collectors.joining("; "));
  }


  // --- Basic Tests ---
  @Test
  public void testEmptyTable() {
    Table emptyTable = createEmptyTable();
    StringValue result = ConvertTableToHTML.action(
        emptyTable, true, null, null, null, null, true,
        "border:1px solid black;", null, null, null, null, null, null, null,
        true, null, false, null, null
    );
    String html = result.get();
    Assert.assertTrue(html.startsWith("<table"));
    Assert.assertTrue(html.endsWith("</table>"));
    Assert.assertFalse(html.contains("<thead"));
    Assert.assertFalse(html.contains("<tbody"));
  }

  @Test
  public void testTableWithHeadersOnly() {
    Table table = createTableWithHeadersOnly();
    StringValue result = ConvertTableToHTML.action(
        table, true, null, null, null, null, true,
        null, null, null, "color:red;", null, null, null, null,
        true, null, false, null, null
    );
    String html = result.get();
    Assert.assertTrue(html.contains("<thead"));
    Assert.assertTrue(html.contains("<th style=\"color:red;\">Col1</th>"));
    Assert.assertTrue(html.contains("<th style=\"color:red;\">Col2</th>"));
    Assert.assertFalse(html.contains("<tbody"));
  }

  @Test
  public void testTableWithDataNoHeader() {
    Table table = createSampleTable(2, true);
    StringValue result = ConvertTableToHTML.action(
        table, false,
        null, null, null, null, true,
        null, null, null, null, null, null, null, "color:blue;",
        true, null, false, null, null
    );
    String html = result.get();
    Assert.assertFalse(html.contains("<thead"));
    Assert.assertTrue(html.contains("<tbody"));
    Assert.assertTrue(html.contains("<td style=\"color:blue;\">Name 1</td>"));
  }

  // --- Styling Tests ---
  @Test
  public void testDisabledInlineStyling() {
    Table table = createSampleTable(1, true);
    StringValue result = ConvertTableToHTML.action(
        table, true, null, null, null, null, false,
        "color:red;", "color:green;", "color:blue;", "color:purple;",
        "color:orange;", "color:yellow;", "color:pink;", "color:brown;",
        true, null, false, null, null
    );
    String html = result.get();
    Assert.assertFalse(html.contains("style="));
    Assert.assertTrue(html.contains("<table>"));
    Assert.assertTrue(html.contains("<th>ID</th>"));
  }

  @Test
  public void testCustomTableStyle() {
    Table table = createSampleTable(0, false);
    String customStyle = "margin:auto; background-color:lightgray;";
    StringValue result = ConvertTableToHTML.action(
        table, false, null, null, null, null, true,
        customStyle, null, null, null, null, null, null, null,
        true, null, false, null, null
    );
    String html = result.get();
    Pattern p = Pattern.compile(
        "<table[^>]*style=[\"']" + Pattern.quote(StringEscapeUtils.escapeHtml4(customStyle))
            + "[\"'][^>]*>");
    Assert.assertTrue(p.matcher(html).find(),
        "Custom table style not applied as expected. HTML: " + html);
  }

  @Test
  public void testCustomTheadTbodyThTdTrStyles() {
    Table table = createSampleTable(2, true);
    String tableStyleVal = "border:1px solid #000;";
    String theadStyleVal = "background-color: #333; color: white;";
    String tbodyStyleVal = "font-style: italic;";
    String thStyleVal = "padding: 15px; border-bottom: 2px solid red;";
    String trStyleVal = "height: 50px;";
    String trEvenStyleVal = "background-color: #eee;";
    String trOddStyleVal = "background-color: #fff;";
    String tdStyleVal = "text-align: right; padding: 10px;";

    StringValue result = ConvertTableToHTML.action(
        table, true, null, null, null, null, true,
        tableStyleVal, theadStyleVal, tbodyStyleVal, thStyleVal,
        trStyleVal, trEvenStyleVal, trOddStyleVal, tdStyleVal,
        true, null, false, null, null
    );
    String html = result.get();

    Assert.assertTrue(
        html.contains("<table style=\"" + StringEscapeUtils.escapeHtml4(tableStyleVal) + "\">"),
        "Table style mismatch");
    Assert.assertTrue(
        html.contains("<thead style=\"" + StringEscapeUtils.escapeHtml4(theadStyleVal) + "\">"),
        "Thead style mismatch");
    Assert.assertTrue(
        html.contains("<tbody style=\"" + StringEscapeUtils.escapeHtml4(tbodyStyleVal) + "\">"),
        "Tbody style mismatch");
    Assert.assertTrue(
        html.contains("<th style=\"" + StringEscapeUtils.escapeHtml4(thStyleVal) + "\">ID</th>"),
        "TH style mismatch");

    String expectedOddRowStyleRaw = buildExpectedRowStyle(trStyleVal, trOddStyleVal);
    String expectedOddRowStyleEscaped = StringEscapeUtils.escapeHtml4(expectedOddRowStyleRaw);
    Assert.assertTrue(html.contains("<tr style=\"" + expectedOddRowStyleEscaped + "\">"),
        "Odd row style missing or incorrect. Expected: <tr style=\"" + expectedOddRowStyleEscaped
            + "\"> Actual HTML segment for 1st data row: " + extractRowHtml(html, 1)
            + " Full HTML: \n" + html);

    String expectedEvenRowStyleRaw = buildExpectedRowStyle(trStyleVal, trEvenStyleVal);
    String expectedEvenRowStyleEscaped = StringEscapeUtils.escapeHtml4(expectedEvenRowStyleRaw);
    Assert.assertTrue(html.contains("<tr style=\"" + expectedEvenRowStyleEscaped + "\">"),
        "Even row style missing or incorrect. Expected: <tr style=\"" + expectedEvenRowStyleEscaped
            + "\"> Actual HTML segment for 2nd data row: " + extractRowHtml(html, 2)
            + " Full HTML: \n" + html);

    Assert.assertTrue(html.contains(
            "<td style=\"" + StringEscapeUtils.escapeHtml4(tdStyleVal) + "\">Name 1</td>"),
        "TD style mismatch for Name 1");
  }

  private String extractRowHtml(String fullHtml, int dataRowNumber) {
    Pattern pattern = Pattern.compile("<tr[^>]*>(\\s*<td[^>]*>.*?</td>\\s*){3}\\s*</tr>");
    Matcher matcher = pattern.matcher(fullHtml);
    int count = 0;
    while (matcher.find()) {
      count++;
      if (count == dataRowNumber) {
        return matcher.group(0);
      }
    }
    return "DATA ROW " + dataRowNumber + " NOT FOUND";
  }


  // --- Attribute Tests ---
  @Test
  public void testTableIdAndClasses() {
    Table table = createSampleTable(0, false);
    List<StringValue> cssClasses = Arrays.asList(new StringValue("class1"),
        new StringValue("  class2  "));
    StringValue result = ConvertTableToHTML.action(
        table, false, null, "my-table-id", cssClasses, null, false,
        null, null, null, null, null, null, null, null,
        true, null, false, null, null
    );
    String html = result.get();
    Assert.assertTrue(html.contains("<table id=\"my-table-id\" class=\"class1 class2\">"));
  }

  @Test
  public void testTableCaption() {
    Table table = createSampleTable(0, false);
    StringValue result = ConvertTableToHTML.action(
        table, false, null, null, null, "My Table Caption <script>alert(1)</script>", false,
        null, null, null, null, null, null, null, null,
        true,
        null, false, null, null
    );
    String html = result.get();
    Assert.assertTrue(
        html.contains("<caption>My Table Caption &lt;script&gt;alert(1)&lt;/script&gt;</caption>"));

    StringValue resultUnescaped = ConvertTableToHTML.action(
        table, false, null, null, null, "My Table Caption <script>alert(1)</script>", false,
        null, null, null, null, null, null, null, null,
        false,
        null, false, null, null
    );
    String htmlUnescaped = resultUnescaped.get();
    Assert.assertTrue(
        htmlUnescaped.contains("<caption>My Table Caption <script>alert(1)</script></caption>"));
  }

  // --- HTML Escaping Tests ---
  @Test
  public void testHtmlEscapingInCells_Enabled() {
    Table table = createSampleTable(1, true);
    StringValue result = ConvertTableToHTML.action(
        table, true, null, null, null, null, false,
        null, null, null, null, null, null, null, null,
        true,
        null, false, null, null
    );
    String html = result.get();
    Assert.assertTrue(html.contains("<td>Val &lt;b&gt;1&lt;/b&gt;</td>"));
    Assert.assertTrue(html.contains("<th>Value &lt; &gt; &amp; &quot; '</th>"));
  }

  @Test
  public void testHtmlEscapingInCells_Disabled() {
    Table table = createSampleTable(1, true);
    StringValue result = ConvertTableToHTML.action(
        table, true, null, null, null, null, false,
        null, null, null, null, null, null, null, null,
        false,
        null, false, null, null
    );
    String html = result.get();
    Assert.assertTrue(html.contains("<td>Val <b>1</b></td>"));
    Assert.assertTrue(html.contains("<th>Value < > & \" '</th>"));
  }

  @Test
  public void testNullCellData() {
    Table table = createTableWithNullCellData();
    StringValue result = ConvertTableToHTML.action(
        table, true, null, null, null, null, false,
        null, null, null, null, null, null, null, null,
        true, null, false, null, null
    );
    String html = result.get();
    // Use regex to be flexible with whitespace between <td> tags
    Pattern firstRowPattern = Pattern.compile("<td>Data1</td>\\s*<td></td>");
    Assert.assertTrue(firstRowPattern.matcher(html).find(),
        "First row with null failed. HTML: " + html);

    Pattern secondRowPattern = Pattern.compile("<td></td>\\s*<td>Data4</td>");
    Assert.assertTrue(secondRowPattern.matcher(html).find(),
        "Second row with null failed. HTML: " + html);
  }


  // --- Full HTML Page Generation Tests ---
  @Test
  public void testGenerateCompleteHtmlPage_DefaultTitle() {
    Table table = createSampleTable(1, false);
    StringValue result = ConvertTableToHTML.action(
        table, false, null, null, null, null, false,
        null, null, null, null, null, null, null, null,
        true, null, true, null, null
    );
    String html = result.get();
    Assert.assertTrue(html.startsWith("<!DOCTYPE html>"));
    Assert.assertTrue(html.contains("<html lang=\"en\">"));
    Assert.assertTrue(html.contains("<head>"));
    Assert.assertTrue(html.contains("<title>Generated Table</title>"));
    Assert.assertTrue(html.contains("<body>"));
    Assert.assertTrue(html.contains("<table>"));
    Assert.assertTrue(html.endsWith("</html>\n"));
  }

  @Test
  public void testGenerateCompleteHtmlPage_CustomTitleAndHeadContent() {
    Table table = createSampleTable(1, false);
    String pageTitle = "My Custom Report";
    String headContent = "<style>body { font-family: 'Comic Sans MS'; }</style><meta name='author' content='Test'>";
    StringValue result = ConvertTableToHTML.action(
        table, false, null, null, null, null, false,
        null, null, null, null, null, null, null, null,
        true, null, true, pageTitle, headContent
    );
    String html = result.get();
    Assert.assertTrue(html.contains("<title>" + pageTitle + "</title>"));
    Assert.assertTrue(html.contains(headContent));
  }

  // --- Combination Test ---
  @Test
  public void testCombinedFeatures() {
    Table table = createSampleTable(3, true);
    List<StringValue> cssClasses = Collections.singletonList(new StringValue("report-table"));
    String tableStyleVal = "width: 80%; margin: 20px auto; box-shadow: 0 0 10px rgba(0,0,0,0.1);";
    String theadStyleVal = "background-color: #4CAF50; color: white;";
    String thStyleVal = "padding: 12px; text-transform: uppercase;";
    String trEvenStyleVal = "background-color: #f9f9f9;";
    String trOddStyleVal = "background-color: #ffffff;";
    String tdStyleVal = "padding: 8px; border-bottom: 1px solid #eee;";

    StringValue result = ConvertTableToHTML.action(
        table, true, null, "report001", cssClasses, "Sales Report Q1", true,
        tableStyleVal, theadStyleVal, null, thStyleVal,
        null, trEvenStyleVal, trOddStyleVal, tdStyleVal,
        true, null, true, "Quarterly Sales Data", "<link rel='stylesheet' href='styles.css'>"
    );

    String html = result.get();

    Assert.assertTrue(html.startsWith("<!DOCTYPE html>"));
    Assert.assertTrue(html.contains("<title>Quarterly Sales Data</title>"));
    Assert.assertTrue(html.contains("<link rel='stylesheet' href='styles.css'>"));
    Assert.assertTrue(html.contains(
        "<table id=\"report001\" class=\"report-table\" style=\"" + StringEscapeUtils.escapeHtml4(
            tableStyleVal) + "\">"));
    Assert.assertTrue(html.contains("<caption>Sales Report Q1</caption>"));
    Assert.assertTrue(
        html.contains("<thead style=\"" + StringEscapeUtils.escapeHtml4(theadStyleVal) + "\">"));
    Assert.assertTrue(
        html.contains("<th style=\"" + StringEscapeUtils.escapeHtml4(thStyleVal) + "\">ID</th>"));

    String expectedOddStyleRaw = buildExpectedRowStyle(null, trOddStyleVal);
    String expectedOddStyleEscaped = StringEscapeUtils.escapeHtml4(expectedOddStyleRaw);
    // Regex to find the <tr> tag with the exact style, allowing for any characters (including newlines) before the <td> containing "Name 1"
    Pattern oddRowPattern = Pattern.compile(
        "<tr\\s+style=[\"']" + Pattern.quote(expectedOddStyleEscaped)
            + "[\"']>[\\s\\S]*?<td[^>]*?>Name 1</td>");
    Assert.assertTrue(oddRowPattern.matcher(html).find(),
        "Odd row style or content not found as expected. Expected style: '"
            + expectedOddStyleEscaped + "'. HTML: \n" + html);

    String expectedEvenStyleRaw = buildExpectedRowStyle(null, trEvenStyleVal);
    String expectedEvenStyleEscaped = StringEscapeUtils.escapeHtml4(expectedEvenStyleRaw);
    Pattern evenRowPattern = Pattern.compile(
        "<tr\\s+style=[\"']" + Pattern.quote(expectedEvenStyleEscaped)
            + "[\"']>[\\s\\S]*?<td[^>]*?>Name 2</td>");
    Assert.assertTrue(evenRowPattern.matcher(html).find(),
        "Even row style or content not found as expected. Expected style: '"
            + expectedEvenStyleEscaped + "'. HTML: \n" + html);

    Assert.assertTrue(html.contains("<td style=\"" + StringEscapeUtils.escapeHtml4(tdStyleVal)
        + "\">Val &lt;b&gt;1&lt;/b&gt;</td>"));
    Assert.assertTrue(html.endsWith("</html>\n"));
  }
}
