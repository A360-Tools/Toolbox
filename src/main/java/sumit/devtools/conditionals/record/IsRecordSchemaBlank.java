package sumit.devtools.conditionals.record;

/**
 * @author Sumit Kumar
 */

import static com.automationanywhere.commandsdk.annotations.BotCommand.CommandType.Condition;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
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
 * Condition to check if all header names in a Record's schema are blank. A blank header name is
 * defined as null, zero-length, or consisting only of whitespace characters.
 */
@BotCommand(commandType = Condition)
@CommandPkg(
    description = "Checks if all header names in the Record's schema are considered blank (null, zero-length, or whitespace-only).",
    name = "isRecordSchemaBlank",
    label = "Record schema: All headers are blank",
    node_label = "If all headers in schema of {{inputRecord}} are blank (Condition: {{conditiontype}})"
)
public class IsRecordSchemaBlank {

  @ConditionTest
  public static Boolean validate(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Input Record variable", description = "The Record variable whose schema headers will be checked.")
      @NotEmpty
      @VariableType(value = DataType.RECORD)
      Record inputRecord,

      @Idx(
          index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "All headers are blank", value = "ALL_HEADERS_BLANK", description = "True if all schema header names are null, zero-length, or whitespace-only.")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "At least one header is not blank", value = "ANY_HEADER_NOT_BLANK", description = "True if at least one schema header name is not null, not zero-length, and not whitespace-only."))
      }
      )
      @Pkg(label = "Condition to check", default_value = "ANY_HEADER_NOT_BLANK", default_value_type = STRING, description = "Specify the condition to evaluate against the record's schema.")
      @NotEmpty
      String conditiontype
  ) {

    try {
      if (inputRecord == null || inputRecord.getSchema() == null) {
        return conditiontype.equalsIgnoreCase("ALL_HEADERS_BLANK");
      }
      if (inputRecord.getSchema().isEmpty()) {
        return conditiontype.equalsIgnoreCase("ALL_HEADERS_BLANK");
      }

      // Check if any schema header name is NOT blank
      boolean anyHeaderIsNotBlank = inputRecord.getSchema().stream()
          .map(Schema::getName)
          .anyMatch(name -> name != null && !name.isBlank());

      if (conditiontype.equalsIgnoreCase("ANY_HEADER_NOT_BLANK")) {
        return anyHeaderIsNotBlank;
      } else { // ALL_HEADERS_BLANK
        return !anyHeaderIsNotBlank; // If no header is "not blank", then all are "blank"
      }
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking if record schema headers are blank: " + e.getMessage(),
          e);
    }
  }
}
