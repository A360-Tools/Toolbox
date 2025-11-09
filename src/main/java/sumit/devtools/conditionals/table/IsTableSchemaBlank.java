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
 * Condition to check if all header names in a Table's schema are blank. A blank header name is
 * defined as null, zero-length, or consisting only of whitespace characters.
 */
@BotCommand(commandType = Condition)
@CommandPkg(
    description = "Checks if all header names in the Table's schema are blank (null, zero-length, or whitespace-only).",
    name = "isTableSchemaBlank",
    label = "Table schema: All headers are blank",
    node_label = "If all headers in schema of {{inputTable}} are blank (Condition: {{conditiontype}})"
)
public class IsTableSchemaBlank {

  @ConditionTest
  public static Boolean validate(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Input Table variable", description = "The Table variable whose schema headers will be checked.")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(
          index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "All headers are blank", value = "ALL_HEADERS_BLANK", description = "True if all schema header names are null, zero-length, or whitespace-only.")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "At least one header is not blank", value = "ANY_HEADER_NOT_BLANK", description = "True if at least one schema header name is not null and contains at least one non-whitespace character."))
      }
      )
      @Pkg(label = "Condition to check", default_value = "ANY_HEADER_NOT_BLANK", default_value_type = STRING, description = "Specify the condition to evaluate against the table's schema.")
      @NotEmpty
      String conditiontype
  ) {

    try {
      if (inputTable == null || inputTable.getSchema() == null) {
        // If table or its schema is null, it can be considered as having all blank headers.
        return conditiontype.equalsIgnoreCase("ALL_HEADERS_BLANK");
      }
      if (inputTable.getSchema().isEmpty()) {
        // If schema has no headers, then all (zero) headers are blank.
        return conditiontype.equalsIgnoreCase("ALL_HEADERS_BLANK");
      }

      // Check if any schema header name is NOT blank
      boolean anyHeaderIsNotBlank = inputTable.getSchema().stream()
          .map(Schema::getName)
          .anyMatch(name -> name != null && !name.isBlank());

      if (conditiontype.equalsIgnoreCase("ANY_HEADER_NOT_BLANK")) {
        return anyHeaderIsNotBlank;
      } else { // ALL_HEADERS_BLANK
        return !anyHeaderIsNotBlank; // If no header is "not blank", then all are "blank"
      }
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking if table schema headers are blank: " + e.getMessage(),
          e);
    }
  }
}
