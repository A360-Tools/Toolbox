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
    label = "Remove empty rows",
    name = "RemoveEmptyRows",
    icon = "Table.svg",
    group_label = "Table",
    description = "Creates an independent copy of the table with empty rows removed. The original table remains unchanged.",
    node_label = "Create copy of {{inputTable}} without empty rows and assign to {{returnTo}}",
    return_description = "Independent copy of table with empty rows removed",
    return_required = true,
    return_label = "Assign modified table to",
    return_type = DataType.TABLE
)

public class RemoveEmptyRows {

  @Execute
  public static TableValue action(
      @Idx(index = "1", type = HELP)
      @Pkg(label = "Tip", description = "This action creates an independent copy. To update the original table, assign the output back to the same variable.")
      @Inject
      String help,

      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable
  ) {
    try {
      // 1. Filter rows from original table (read-only, no copying yet)
      List<Row> nonEmptyRows = new ArrayList<>();
      for (Row row : inputTable.getRows()) {
        for (Value cellValue : row.getValues()) {
          if (cellValue != null && !cellValue.get().toString().isEmpty()) {
            nonEmptyRows.add(row);
            break;
          }
        }
      }

      // 2. Deep copy schema (lightweight operation)
      List<Schema> copySchema = inputTable.getSchema().stream()
          .map(schema -> new Schema(schema.getName(), schema.getType()))
          .collect(Collectors.toList());

      // 3. Deep copy ONLY the filtered non-empty rows
      List<Row> deepCopiedRows = nonEmptyRows.stream()
          .map(row -> {
            List<Value> deepCopiedValues = new ArrayList<>();
            for (Value value : row.getValues()) {
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
      throw new BotCommandException("Error Occurred while removing empty rows: " + e.getMessage(),
          e);
    }

  }

}
