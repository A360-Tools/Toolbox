package sumit.devtools.conditionals.table;

import static com.automationanywhere.commandsdk.annotations.BotCommand.CommandType.Condition;
import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.ConditionTest;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sumit Kumar
 */
@BotCommand(commandType = Condition)
@CommandPkg(
    description = "Checks if table has all specified column headers in schema",
    name = "hasTableColumns",
    label = "Table: Has all specified columns",
    icon = "Table.svg",
    node_label = "If table {{inputTable}} {{conditiontype}} all columns {{columnNames}}"
)
public class HasTableColumns {

  @ConditionTest
  public static Boolean validate(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Has", value = "CONTAINS")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Does not have", value = "NOTCONTAINS"))
      })
      @Pkg(label = "Condition", default_value = "CONTAINS", default_value_type = STRING)
      @NotEmpty
      String conditiontype,

      @Idx(index = "3", type = AttributeType.LIST)
      @Pkg(label = "Column names", description = "List of column names to check for existence in the table")
      @NotEmpty
      @ListType(STRING)
      List<StringValue> columnNames,

      @Idx(index = "4", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether column name comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name' matches 'name', 'NAME'). " +
              "When true: Case-sensitive (e.g., 'Name' does not match 'name').",
          default_value = "false",
          default_value_type = BOOLEAN)
      @NotEmpty
      Boolean caseSensitive
  ) {
    try {
      if (inputTable == null || inputTable.getSchema() == null) {
        throw new BotCommandException("Input table or table schema cannot be null");
      }

      if (columnNames == null || columnNames.isEmpty()) {
        throw new BotCommandException("Column names list cannot be null or empty");
      }

      // Get list of actual column names from schema
      List<String> schemaColumns = inputTable.getSchema().stream()
          .map(Schema::getName)
          .collect(Collectors.toList());

      // Convert input column names to strings
      List<String> columnsToCheck = columnNames.stream()
          .map(StringValue::get)
          .collect(Collectors.toList());

      // Check if all columns exist
      boolean allColumnsExist = columnsToCheck.stream()
          .allMatch(columnName -> columnExists(schemaColumns, columnName, caseSensitive));

      // Return based on condition type
      if (conditiontype.equalsIgnoreCase("CONTAINS")) {
        return allColumnsExist;
      } else {
        return !allColumnsExist;
      }

    } catch (BotCommandException e) {
      throw e;
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking existence of columns in table: " + e.getMessage(), e);
    }
  }

  /**
   * Checks if a column exists in the schema columns list.
   *
   * @param schemaColumns   List of column names from table schema
   * @param columnName      Column name to search for
   * @param caseSensitive   Whether to perform case-sensitive comparison
   * @return true if column exists, false otherwise
   */
  private static boolean columnExists(List<String> schemaColumns, String columnName,
      boolean caseSensitive) {
    if (caseSensitive) {
      return schemaColumns.contains(columnName);
    } else {
      return schemaColumns.stream()
          .anyMatch(col -> col.equalsIgnoreCase(columnName));
    }
  }
}
