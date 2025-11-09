package sumit.devtools.iterators.html;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.BotCommand.CommandType;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.HasNext;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Inject;
import com.automationanywhere.commandsdk.annotations.Next;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Iterator for HTML tables that processes one table at a time for memory efficiency.
 *
 * @author Sumit Kumar
 */
@BotCommand(commandType = CommandType.Iterator)
@CommandPkg(
    label = "HTML Table Iterator",
    description = "Iterates through tables in HTML content.",
    icon = "Table.svg",
    name = "htmlTableIterator",
    node_label = "from {{selectMethod}} and assign to {{returnTo}}",
    return_label = "Assign the current table to",
    return_description = "Table extracted from HTML",
    return_required = true,
    return_type = DataType.TABLE
)
public class HTMLTableIterator {

  // Input parameters - injected via setters
  @Idx(index = "1", type = AttributeType.SELECT, options = {
      @Idx.Option(index = "1.1", pkg = @Pkg(label = "TEXT", value = "TEXT")),
      @Idx.Option(index = "1.2", pkg = @Pkg(label = "FILE", value = "FILE"))
  })
  @Pkg(label = "Set value via",
      description = "Specifies input method for HTML content",
      default_value = "TEXT",
      default_value_type = DataType.STRING)
  @NotEmpty
  @Inject
  private String selectMethod;

  @Idx(index = "1.1.1", type = AttributeType.TEXTAREA)
  @Pkg(label = "Set HTML text value",
      description = "HTML content containing tables to iterate")
  @NotEmpty
  @Inject
  private String htmlText;

  @Idx(index = "1.2.1", type = AttributeType.FILE)
  @Pkg(label = "File path",
      description = "Path to HTML file containing tables to iterate")
  @NotEmpty
  @Inject
  private String filePath;

  @Idx(index = "1.2.2", type = AttributeType.SELECT, options = {
      @Idx.Option(index = "1.2.2.1", pkg = @Pkg(label = "UTF-8", value = "UTF-8")),
      @Idx.Option(index = "1.2.2.2", pkg = @Pkg(label = "ISO-8859-1", value = "ISO-8859-1")),
      @Idx.Option(index = "1.2.2.3", pkg = @Pkg(label = "US-ASCII", value = "US-ASCII")),
      @Idx.Option(index = "1.2.2.4", pkg = @Pkg(label = "UTF-16", value = "UTF-16")),
      @Idx.Option(index = "1.2.2.5", pkg = @Pkg(label = "UTF-16BE", value = "UTF-16BE")),
      @Idx.Option(index = "1.2.2.6", pkg = @Pkg(label = "UTF-16LE", value = "UTF-16LE")),
      @Idx.Option(index = "1.2.2.7", pkg = @Pkg(label = "UTF-32", value = "UTF-32")),
      @Idx.Option(index = "1.2.2.8", pkg = @Pkg(label = "UTF-32BE", value = "UTF-32BE")),
      @Idx.Option(index = "1.2.2.9", pkg = @Pkg(label = "UTF-32LE", value = "UTF-32LE"))
  })
  @Pkg(label = "Character Set",
      description = "Character encoding for file reading",
      default_value = "UTF-8",
      default_value_type = DataType.STRING)
  @NotEmpty
  @Inject
  private String charsetName;

  @Idx(index = "2", type = AttributeType.RADIO, options = {
      @Idx.Option(index = "2.1", pkg = @Pkg(label = "NORMALIZED (Whitespace is normalized and trimmed)", value = "NORMALIZED_TEXT")),
      @Idx.Option(index = "2.2", pkg = @Pkg(label = "WHOLE TEXT (Whitespace is not normalized and not trimmed)", value = "WHOLE_TEXT"))
  })
  @Pkg(label = "Text formatting options",
      description = "Controls how whitespace is handled in cell text",
      default_value = "NORMALIZED_TEXT",
      default_value_type = DataType.STRING)
  @NotEmpty
  @Inject
  private String textType;

  @Idx(index = "3", type = AttributeType.RADIO, options = {
      @Idx.Option(index = "3.1", pkg = @Pkg(label = "ALL CHILDREN TEXT (All data within table data node)", value = "INCLUDE_CHILDREN")),
      @Idx.Option(index = "3.2", pkg = @Pkg(label = "OWN TEXT (Only table data node)", value = "ONLY_SELF"))
  })
  @Pkg(label = "Data extraction options",
      description = "Controls whether to include nested element text in cell data",
      default_value = "INCLUDE_CHILDREN",
      default_value_type = DataType.STRING)
  @NotEmpty
  @Inject
  private String extractionType;

  // Internal state - lightweight references only
  private Elements tables;
  private int currentIndex = 0;

  /**
   * Initializes the iterator by parsing HTML and extracting table elements.
   * Called automatically after dependency injection.
   */
  public void initialize() {
    try {
      Document doc;

      if ("TEXT".equalsIgnoreCase(selectMethod)) {
        doc = Jsoup.parseBodyFragment(htmlText);
      } else {
        String fileContent = FileUtils.readFileToString(
            new File(filePath),
            Charset.forName(charsetName)
        );
        doc = Jsoup.parseBodyFragment(fileContent);
      }

      tables = doc.select("table");
      currentIndex = 0;

    } catch (Exception e) {
      throw new BotCommandException(
          "Failed to initialize HTML table iterator: " + e.getMessage(), e
      );
    }
  }

  /**
   * Checks if more tables are available for iteration.
   *
   * @return true if more tables exist, false otherwise
   */
  @HasNext
  public boolean hasNext() {
    // Lazy initialization on first hasNext call
    if (tables == null) {
      initialize();
    }
    return currentIndex < tables.size();
  }

  /**
   * Returns the next table from HTML as a TableValue.
   * Processes one table at a time for memory efficiency.
   *
   * @return TableValue containing the next table
   * @throws Exception if no more tables are available or processing fails
   */
  @Next
  public TableValue next() throws Exception {
    if (!hasNext()) {
      throw new BotCommandException(
          "No more tables available. Current index: " + currentIndex +
          ", Total tables: " + (tables != null ? tables.size() : 0)
      );
    }

    try {
      Element table = tables.get(currentIndex);
      currentIndex++;

      return buildTableValue(table);

    } catch (Exception e) {
      throw new BotCommandException(
          "Error processing table at index " + (currentIndex - 1) + ": " + e.getMessage(), e
      );
    }
  }

  /**
   * Builds a TableValue from a Jsoup table element.
   * Reuses the table parsing logic for consistency.
   */
  private TableValue buildTableValue(Element table) {
    List<Schema> schemaList = new ArrayList<>();
    List<Row> rowList = new ArrayList<>();

    Elements rows = table.select("tr");

    // Process headers
    Elements headers = rows.isEmpty() ? new Elements() : rows.get(0).select("th");
    for (Element header : headers) {
      schemaList.add(new Schema(getTextFromElement(header)));
    }

    int maxColumnCount = headers.size();
    int startRow = headers.size() > 0 ? 1 : 0;

    // Process data rows
    for (int i = startRow; i < rows.size(); i++) {
      Element row = rows.get(i);
      List<Value> rowValues = new ArrayList<>();
      Elements cells = row.select("td");

      for (Element cell : cells) {
        rowValues.add(new StringValue(getTextFromElement(cell)));
      }

      rowList.add(new Row(rowValues));
      maxColumnCount = Math.max(rowValues.size(), maxColumnCount);
    }

    // Ensure consistent column count
    addMissingColumnValues(rowList, maxColumnCount);

    while (schemaList.size() < maxColumnCount) {
      schemaList.add(new Schema(""));
    }

    Table outputTable = new Table(schemaList, rowList);
    return new TableValue(outputTable);
  }

  /**
   * Extracts text from an element based on formatting options.
   */
  private String getTextFromElement(Element element) {
    if ("WHOLE_TEXT".equalsIgnoreCase(textType) && "INCLUDE_CHILDREN".equalsIgnoreCase(extractionType)) {
      return element.wholeText();
    }
    if ("WHOLE_TEXT".equalsIgnoreCase(textType) && "ONLY_SELF".equalsIgnoreCase(extractionType)) {
      return element.wholeOwnText();
    }
    if ("NORMALIZED_TEXT".equalsIgnoreCase(textType) && "ONLY_SELF".equalsIgnoreCase(extractionType)) {
      return element.ownText();
    }
    return element.text();
  }

  /**
   * Pads rows with empty values to ensure consistent column count.
   */
  private void addMissingColumnValues(List<Row> rowList, int maxColumnCount) {
    for (Row row : rowList) {
      while (row.getValues().size() < maxColumnCount) {
        row.getValues().add(new StringValue());
      }
    }
  }

  // Setters for dependency injection
  public void setSelectMethod(String selectMethod) {
    this.selectMethod = selectMethod;
  }

  public void setHtmlText(String htmlText) {
    this.htmlText = htmlText;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public void setCharsetName(String charsetName) {
    this.charsetName = charsetName;
  }

  public void setTextType(String textType) {
    this.textType = textType;
  }

  public void setExtractionType(String extractionType) {
    this.extractionType = extractionType;
  }
}
