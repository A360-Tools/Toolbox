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
 * Condition to check if all cell values in a Record are empty. An empty cell value is defined as
 * null or its string representation having a length of 0. It does not consider whitespace-only
 * strings as empty. For that, use "Record value is blank".
 */
@BotCommand(commandType = BotCommand.CommandType.Condition)
@CommandPkg(description = "Checks if all cell values in the Record are empty (null or zero-length string). Whitespace-only strings are NOT considered empty by this condition.",
    name = "isRecordEmpty", // Changed name for clarity
    label = "Record: All cell values are empty", // Updated label
    node_label = "If all cell values in {{rowRecord}} are empty")
public class IsRecordEmpty {

  // protected static final Logger LOGGER = LogManager.getLogger(IsRecordEmpty.class); // Optional

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
        // If record or its values list is null, it can be considered as having all empty values.
        return true;
      }
      List<Value> rowvalues = rowRecord.getValues();
      if (rowvalues.isEmpty()) {
        // If there are no cells, then all (zero) cells are effectively empty.
        return true;
      }

      for (Value cellValue : rowvalues) {
        // A cell is considered NOT empty if it's not null, its underlying value is not null,
        // and its string representation is not a zero-length string.
        if (cellValue != null && cellValue.get() != null && !cellValue.get().toString().isEmpty()) {
          return false; // Found a non-empty cell
        }
      }
      return true; // All cells were empty (null, or contained null/empty string)
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking if record cell values are empty: " + e.getMessage(), e);
    }
  }
}
