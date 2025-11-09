package sumit.devtools.conditionals.record;

import static com.automationanywhere.commandsdk.model.DataType.RECORD;

import com.automationanywhere.botcommand.data.Value;
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
import java.util.List;

/**
 * Condition to check if all cell values in a Record are blank. A blank cell value is defined as
 * null, its string representation having a length of 0, or its string representation consisting
 * only of whitespace characters.
 */
@BotCommand(commandType = BotCommand.CommandType.Condition)
@CommandPkg(description = "Checks if all cell values in the Record are blank (null, zero-length, or whitespace-only).",
    name = "isRecordBlank",
    label = "Record: All cell values are blank",
    node_label = "If all cell values in {{rowRecord}} are blank")
public class IsRecordBlank {

  @ConditionTest
  public static Boolean test(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Input Record variable", description = "The Record variable whose cell values will be checked.")
      @NotEmpty
      @VariableType(value = RECORD)
      Record rowRecord
  ) {
    try {
      if (rowRecord == null || rowRecord.getValues() == null) {
        // If record or its values list is null, it can be considered as having all blank values.
        return true;
      }
      List<Value> rowvalues = rowRecord.getValues();
      if (rowvalues.isEmpty()) {
        // If there are no cells, then all (zero) cells are effectively blank.
        return true;
      }

      for (Value cellValue : rowvalues) {
        // A cell is considered NOT blank if it's not null, its underlying value is not null,
        // and its string representation is not blank (i.e., has at least one non-whitespace character).
        if (cellValue != null && cellValue.get() != null && !cellValue.get().toString().isBlank()) {
          return false; // Found a non-blank cell
        }
      }
      return true; // All cells were blank
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking if record cell values are blank: " + e.getMessage(), e);
    }
  }
}
