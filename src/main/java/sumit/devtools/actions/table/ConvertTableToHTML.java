package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.text.StringEscapeUtils;

@BotCommand
@CommandPkg(label = "Convert Table To HTML",
    description = "Converts a Data Table to a customizable HTML table string",
    icon = "Table.svg",
    name = "convertTableToHTML",
    group_label = "Table",
    node_label = "Generate HTML from {{inputTable}} with styling {{enableInlineStyling}}",
    return_description = "Returns an HTML string representing the table.",
    return_required = true,
    return_type = STRING)
public class ConvertTableToHTML { // Renamed class

  @Execute
  public static StringValue action(
      @Idx(index = "1", type = AttributeType.TABLE)
      @Pkg(label = "Input Data Table")
      @NotEmpty
      Table inputTable,

      @Idx(index = "2", type = AttributeType.CHECKBOX)
      @Pkg(label = "Include table header (<thead>)", default_value_type = BOOLEAN, default_value = "true", description = "If checked, the table schema will be used to generate <th> elements within a <thead>.")
      @NotEmpty
      Boolean includeHeader,

      @Idx(index = "3", type = AttributeType.GROUP)
      @Pkg(label = "Table Attributes & Caption")
      String tableAttributesGroup,

      @Idx(index = "3.1", type = AttributeType.TEXT)
      @Pkg(label = "Table ID", description = "HTML id attribute for the <table> tag.")
      String tableId,

      @Idx(index = "3.2", type = AttributeType.LIST)
      @Pkg(label = "Table CSS Class(es)", description = "List of CSS class(es) for the <table> tag.")
      @ListType(DataType.STRING)
      List<StringValue> tableCssClasses,

      @Idx(index = "3.3", type = AttributeType.TEXT)
      @Pkg(label = "Table Caption", description = "Text for the <caption> element within the table.")
      String tableCaption,

      @Idx(index = "4", type = AttributeType.CHECKBOX)
      @Pkg(label = "Enable Inline Styling", default_value_type = BOOLEAN, default_value = "true", description = "Enable or disable all inline CSS styling options below.")
      @NotEmpty
      Boolean enableInlineStyling,

      @Idx(index = "4.1", type = AttributeType.TEXTAREA)
      @Pkg(label = "<table> Style", description = "Inline CSS for the <table> element. e.g., border-collapse:collapse; width:100%;",
          default_value_type = STRING, default_value = "border-collapse:collapse; border: 1px solid #dddddd; width: 100%; font-family: Arial, sans-serif;")
      String tableStyle,

      @Idx(index = "4.2", type = AttributeType.TEXTAREA)
      @Pkg(label = "<thead> Style", description = "Inline CSS for the <thead> element.")
      String theadStyle,

      @Idx(index = "4.3", type = AttributeType.TEXTAREA)
      @Pkg(label = "<tbody> Style", description = "Inline CSS for the <tbody> element.")
      String tbodyStyle,

      @Idx(index = "4.4", type = AttributeType.TEXTAREA)
      @Pkg(label = "<th> Style", description = "Inline CSS for table header (<th>) cells. e.g., background-color:#f2f2f2; text-align:left; padding:8px;",
          default_value_type = STRING, default_value = "background-color:#f0f0f0; border:1px solid #cccccc; color:#333333; padding:10px 5px; text-align:left; vertical-align:top; font-weight:bold;")
      String thStyle,

      @Idx(index = "4.5", type = AttributeType.TEXTAREA)
      @Pkg(label = "Default <tr> Style", description = "Inline CSS for all table rows (<tr>) in <tbody>.")
      String trStyle,

      @Idx(index = "4.6", type = AttributeType.TEXTAREA)
      @Pkg(label = "Even <tr> Style", description = "Inline CSS for even table rows (<tr>) in <tbody>. Overrides Default <tr> Style.")
      String trEvenStyle,

      @Idx(index = "4.7", type = AttributeType.TEXTAREA)
      @Pkg(label = "Odd <tr> Style", description = "Inline CSS for odd table rows (<tr>) in <tbody>. Overrides Default <tr> Style.")
      String trOddStyle,

      @Idx(index = "4.8", type = AttributeType.TEXTAREA)
      @Pkg(label = "<td> Style", description = "Inline CSS for table data (<td>) cells. e.g., padding:8px; border:1px solid #ddd;",
          default_value_type = STRING, default_value = "border:1px solid #cccccc; color:#333333; padding:10px 5px; text-align:left; vertical-align:top;")
      String tdStyle,

      @Idx(index = "5", type = AttributeType.CHECKBOX)
      @Pkg(label = "Escape HTML in cell data", default_value_type = BOOLEAN, default_value = "true", description = "If checked, special HTML characters in cell data will be escaped (e.g., '<' becomes '&lt;').")
      @NotEmpty
      Boolean escapeHtmlInCells,

      @Idx(index = "6", type = AttributeType.GROUP)
      @Pkg(label = "Full HTML Page Generation")
      String fullPageGroup,

      @Idx(index = "6.1", type = AttributeType.CHECKBOX)
      @Pkg(label = "Generate complete HTML page", default_value_type = BOOLEAN, default_value = "false", description = "If checked, the table will be wrapped in a full HTML document structure.")
      @NotEmpty
      Boolean generateCompleteHtmlPage,

      @Idx(index = "6.1.1", type = AttributeType.TEXT)
      @Pkg(label = "Page Title", description = "Title for the HTML page. Used if 'Generate complete HTML page' is checked.")
      String customPageTitle,

      @Idx(index = "6.1.2", type = AttributeType.TEXTAREA)
      @Pkg(label = "Additional <head> Content", description = "Additional HTML content for the <head> section (e.g., <style> tags, <link> to CSS). Used if 'Generate complete HTML page' is checked.")
      String additionalHeadContent
  ) {
    try {
      List<Schema> inputTableSchema = inputTable.getSchema();
      List<Row> inputTableRows = inputTable.getRows();
      StringBuilder html = new StringBuilder();

      if (Boolean.TRUE.equals(generateCompleteHtmlPage)) {
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append(
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        if (customPageTitle != null && !customPageTitle.isEmpty()) {
          html.append("  <title>").append(
              Boolean.TRUE.equals(escapeHtmlInCells) ? StringEscapeUtils.escapeHtml4(
                  customPageTitle) : customPageTitle).append("</title>\n");
        } else {
          html.append("  <title>Generated Table</title>\n");
        }
        if (additionalHeadContent != null && !additionalHeadContent.isEmpty()) {
          html.append("  ").append(additionalHeadContent).append("\n");
        }
        html.append("</head>\n");
        html.append("<body>\n\n");
      }

      html.append("<table");
      if (tableId != null && !tableId.trim().isEmpty()) {
        html.append(" id=\"").append(StringEscapeUtils.escapeHtml4(tableId.trim())).append("\"");
      }
      if (tableCssClasses != null && !tableCssClasses.isEmpty()) {
        String classes = tableCssClasses.stream()
            .map(StringValue::get)
            .filter(s -> s != null && !s.trim().isEmpty())
            .map(s -> StringEscapeUtils.escapeHtml4(s.trim()))
            .collect(Collectors.joining(" "));
        if (!classes.isEmpty()) {
          html.append(" class=\"").append(classes).append("\"");
        }
      }
      if (Boolean.TRUE.equals(enableInlineStyling) && tableStyle != null && !tableStyle.trim()
          .isEmpty()) {
        html.append(" style=\"").append(StringEscapeUtils.escapeHtml4(tableStyle.trim()))
            .append("\"");
      }
      html.append(">\n");

      if (tableCaption != null && !tableCaption.trim().isEmpty()) {
        html.append("  <caption>").append(
            Boolean.TRUE.equals(escapeHtmlInCells) ? StringEscapeUtils.escapeHtml4(tableCaption)
                : tableCaption).append("</caption>\n");
      }

      if (Boolean.TRUE.equals(includeHeader) && inputTableSchema != null
          && !inputTableSchema.isEmpty()) {
        html.append("  <thead");
        if (Boolean.TRUE.equals(enableInlineStyling) && theadStyle != null && !theadStyle.trim()
            .isEmpty()) {
          html.append(" style=\"").append(StringEscapeUtils.escapeHtml4(theadStyle.trim()))
              .append("\"");
        }
        html.append(">\n");
        html.append("    <tr");
        // For <thead> row, trStyle is applied if present. Even/Odd styles are for <tbody>.
        if (Boolean.TRUE.equals(enableInlineStyling) && trStyle != null && !trStyle.trim()
            .isEmpty()) {
          html.append(" style=\"").append(StringEscapeUtils.escapeHtml4(trStyle.trim()))
              .append("\"");
        }
        html.append(">\n");
        for (Schema schema : inputTableSchema) {
          html.append("      <th");
          if (Boolean.TRUE.equals(enableInlineStyling) && thStyle != null && !thStyle.trim()
              .isEmpty()) {
            html.append(" style=\"").append(StringEscapeUtils.escapeHtml4(thStyle.trim()))
                .append("\"");
          }
          html.append(">");
          String headerName = schema.getName();
          html.append(Boolean.TRUE.equals(escapeHtmlInCells) && headerName != null
              ? StringEscapeUtils.escapeHtml4(headerName) : (headerName != null ? headerName : ""));
          html.append("</th>\n");
        }
        html.append("    </tr>\n");
        html.append("  </thead>\n");
      }

      if (inputTableRows != null && !inputTableRows.isEmpty()) {
        html.append("  <tbody");
        if (Boolean.TRUE.equals(enableInlineStyling) && tbodyStyle != null && !tbodyStyle.trim()
            .isEmpty()) {
          html.append(" style=\"").append(StringEscapeUtils.escapeHtml4(tbodyStyle.trim()))
              .append("\"");
        }
        html.append(">\n");
        for (int i = 0; i < inputTableRows.size(); i++) {
          Row row = inputTableRows.get(i);
          String currentRowStyle = "";
          if (Boolean.TRUE.equals(enableInlineStyling)) {
            List<String> styleParts = new ArrayList<>();
            if (trStyle != null && !trStyle.trim().isEmpty()) {
              String trimmed = trStyle.trim();
              styleParts.add(
                  trimmed.endsWith(";") ? trimmed.substring(0, trimmed.length() - 1).trim()
                      : trimmed);
            }

            boolean isEvenRow = (i + 1) % 2 == 0;
            if (isEvenRow && trEvenStyle != null && !trEvenStyle.trim().isEmpty()) {
              String trimmed = trEvenStyle.trim();
              styleParts.add(
                  trimmed.endsWith(";") ? trimmed.substring(0, trimmed.length() - 1).trim()
                      : trimmed);
            } else if (!isEvenRow && trOddStyle != null && !trOddStyle.trim().isEmpty()) {
              String trimmed = trOddStyle.trim();
              styleParts.add(
                  trimmed.endsWith(";") ? trimmed.substring(0, trimmed.length() - 1).trim()
                      : trimmed);
            }

            currentRowStyle = styleParts.stream().filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining("; "));
          }

          html.append("    <tr");
          if (!currentRowStyle.isEmpty()) {
            html.append(" style=\"").append(StringEscapeUtils.escapeHtml4(currentRowStyle))
                .append("\"");
          }
          html.append(">\n");

          for (Value<?> data : row.getValues()) {
            html.append("      <td");
            if (Boolean.TRUE.equals(enableInlineStyling) && tdStyle != null && !tdStyle.trim()
                .isEmpty()) {
              html.append(" style=\"").append(StringEscapeUtils.escapeHtml4(tdStyle.trim()))
                  .append("\"");
            }
            html.append(">");
            String cellData = (data != null && data.get() != null) ? data.get().toString() : "";
            html.append(
                Boolean.TRUE.equals(escapeHtmlInCells) ? StringEscapeUtils.escapeHtml4(cellData)
                    : cellData);
            html.append("</td>\n");
          }
          html.append("    </tr>\n");
        }
        html.append("  </tbody>\n");
      }

      html.append("</table>");

      if (Boolean.TRUE.equals(generateCompleteHtmlPage)) {
        html.append("\n\n</body>\n");
        html.append("</html>\n");
      }

      return new StringValue(html.toString());
    } catch (Exception e) {
      throw new BotCommandException("Error generating HTML table: " + e.getMessage(), e);
    }
  }
}
