package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.HELP;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Inject;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sumit.devtools.utils.ValueUtil;

@BotCommand
@CommandPkg(
    label = "Remove empty columns",
    name = "RemoveEmptyColumns",
    icon = "Table.svg",
    group_label = "Table",
    description = "Creates an independent copy of the table with empty columns removed. The original table remains unchanged.",
    node_label = "Create copy of {{inputTable}} without empty columns and assign to {{returnTo}}",
    return_description = "Independent copy of table with empty columns removed",
    return_required = true,
    return_label = "Assign modified table to",
    return_type = DataType.TABLE
)

public class RemoveEmptyColumns {

  @Execute
  public static TableValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(index = "2", type = HELP)
      @Pkg(label = "Tip", description = "This action creates an independent copy. To update the original table, assign the output back to the same variable.")
      @Inject
      String help
  ) {
    try {
      // 1. Identify non-empty columns
      List<Schema> inputSchema = inputTable.getSchema();
      List<Row> inputRows = inputTable.getRows();
      int columnCount = inputSchema.size();

      List<Integer> nonEmptyColumnIndices = new ArrayList<>();

      // Check each column to see if it has at least one non-empty cell
      for (int colIndex = 0; colIndex < columnCount; colIndex++) {
        boolean hasNonEmptyCell = false;

        for (Row row : inputRows) {
          List<Value> rowValues = row.getValues();
          if (colIndex < rowValues.size()) {
            Value cellValue = rowValues.get(colIndex);
            if (cellValue != null && !cellValue.get().toString().isEmpty()) {
              hasNonEmptyCell = true;
              break;
            }
          }
        }

        if (hasNonEmptyCell) {
          nonEmptyColumnIndices.add(colIndex);
        }
      }

      // 2. Deep copy schema for non-empty columns only
      List<Schema> copySchema = nonEmptyColumnIndices.stream()
          .map(index -> {
            Schema originalSchema = inputSchema.get(index);
            return new Schema(originalSchema.getName(), originalSchema.getType());
          })
          .collect(Collectors.toList());

      // 3. Deep copy rows with only non-empty columns
      List<Row> deepCopiedRows = inputRows.stream()
          .map(row -> {
            List<Value> deepCopiedValues = new ArrayList<>();
            for (Integer colIndex : nonEmptyColumnIndices) {
              Value value = null;
              if (colIndex < row.getValues().size()) {
                value = row.getValues().get(colIndex);
              }
              deepCopiedValues.add(ValueUtil.deepCopyValue(value));
            }
            return new Row(deepCopiedValues);
          })
          .collect(Collectors.toList());

      // 4. Build result table
      Table Output = new Table();
      Output.setSchema(copySchema);
      Output.setRows(deepCopiedRows);

      return new TableValue(Output);
    } catch (Exception e) {
      throw new BotCommandException("Error Occurred while removing empty columns: " + e.getMessage(),
          e);
    }

  }

}
