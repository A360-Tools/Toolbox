package sumit.devtools.conditionals.table;

/**
 * @author Sumit Kumar
 */

import static com.automationanywhere.commandsdk.annotations.BotCommand.CommandType.Condition;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.ConditionTest;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;

/**
 * Condition to check if all header names in a Table's schema are empty. An empty header name is
 * defined as null or a zero-length string. Whitespace-only strings are NOT considered empty by this
 * condition. For whitespace checks, use "Table schema: All headers are blank".
 */
@BotCommand(commandType = Condition)
@CommandPkg(
    description = "Checks if all header names in the Table's schema are empty (null or zero-length string). Whitespace-only strings are NOT considered empty.",
    name = "isTableSchemaEmpty", // Updated name
    label = "Table schema: All headers are empty", // Updated label
    node_label = "If all headers in schema of {{inputTable}} are empty (Condition: {{conditiontype}})"
)
public class IsTableSchemaEmpty {

  @ConditionTest
  public static Boolean validate(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Input Table variable", description = "The Table variable whose schema headers will be checked.")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(
          index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "All headers are empty", value = "ALL_HEADERS_EMPTY", description = "True if all schema header names are null or zero-length.")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "At least one header is not empty", value = "ANY_HEADER_NOT_EMPTY", description = "True if at least one schema header name is not null and not zero-length."))
      }
      )
      @Pkg(label = "Condition to check", default_value = "ANY_HEADER_NOT_EMPTY", default_value_type = STRING, description = "Specify the condition to evaluate against the table's schema.")
      @NotEmpty
      String conditiontype
  ) {

    try {
      if (inputTable == null || inputTable.getSchema() == null) {
        // If table or its schema is null, it can be considered as having all empty headers.
        return conditiontype.equalsIgnoreCase("ALL_HEADERS_EMPTY");
      }
      if (inputTable.getSchema().isEmpty()) {
        // If schema has no headers, then all (zero) headers are empty.
        return conditiontype.equalsIgnoreCase("ALL_HEADERS_EMPTY");
      }

      // Check if any schema header name is NOT empty (i.e., not null and not zero-length)
      boolean anyHeaderIsNotEmpty = inputTable.getSchema().stream()
          .map(Schema::getName)
          .anyMatch(name -> name != null && !name.isEmpty());

      if (conditiontype.equalsIgnoreCase("ANY_HEADER_NOT_EMPTY")) {
        return anyHeaderIsNotEmpty;
      } else { // ALL_HEADERS_EMPTY
        return !anyHeaderIsNotEmpty; // If no header is "not empty", then all are "empty"
      }
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking if table schema headers are empty: " + e.getMessage(),
          e);
    }
  }
}
