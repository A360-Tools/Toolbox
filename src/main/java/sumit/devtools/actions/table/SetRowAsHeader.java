package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.HELP;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Inject;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import java.util.stream.Collectors;
import sumit.devtools.utils.TableUtil;


@BotCommand
@CommandPkg(label = "Set row as header", description = "Creates an independent copy with a specific row set as header. The original table remains unchanged.", icon =
    "Table" +
        ".svg", name = "setRowAsHeader", group_label = "Table",
    node_label = "Create copy with row {{rowPosition}} as header and assign to {{returnTo}}", return_description =
    "Independent copy of table with updated headers",
    return_required = true,
    return_label = "Assign modified table to",
    return_type = DataType.TABLE)

public class SetRowAsHeader {

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
      Table inputTable,

      @Idx(index = "3", type = AttributeType.NUMBER)
      @Pkg(label = "Row index", description =
          "Index numbers start with 0. For example, first row has an index " +
              "of 0")
      @NotEmpty
      @GreaterThanEqualTo("0")
      Double rowPosition,

      @Idx(index = "4", type = AttributeType.BOOLEAN)
      @Pkg(label = "Remove row from table",
          description = "Controls whether the row used as header should be removed from the table data. " +
              "When false: Keeps the row in the table body (may result in duplicate headers). " +
              "When true (default): Removes the row from table data after using it as header.",
          default_value = "true",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean isRowToBeRemoved

  ) {
    try {
      Table Output = TableUtil.copyTable(inputTable);

      if (rowPosition > Output.getRows().size() - 1 || rowPosition < 0) {
        throw new BotCommandException("Row not found at specified index");
      }

      List<Schema> updatedSchema = Output.getRows().get(rowPosition.intValue()).getValues().stream()
          .map(value -> new Schema(value.get().toString()))
          .collect(Collectors.toList());

      Output.setSchema(updatedSchema);

      if (isRowToBeRemoved) {
        Output.getRows().remove(rowPosition.intValue());
      }

      return new TableValue(Output);

    } catch (Exception e) {
      throw new BotCommandException("Error Occurred while setting row as header: " + e.getMessage(),
          e);
    }


  }

}
