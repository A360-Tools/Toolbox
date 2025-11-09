package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.HELP;
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

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
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import sumit.devtools.utils.TableUtil;

/**
 * @author Sumit Kumar
 */

@BotCommand
@CommandPkg(label = "Add column", description = "Creates an independent copy of the table with a new column added. The original table remains unchanged.", icon = "Table.svg", name
    = "addTableColumn", group_label = "Table",
    node_label = "Create copy of {{inputTable}} with {{colName}} added {{insertType}} and assign to {{returnTo}}", return_description =
    "Independent copy of table with column added",
    return_required = true,
    return_label = "Assign modified table to",
    return_type = DataType.TABLE)
public class AddColumn {

  @Execute
  public static TableValue action(

      @Idx(index = "1", type = HELP)
      @Pkg(label = "Tip", description = "This action creates an independent copy. To update the original table, assign the output back to the same variable.")
      @Inject
      String help
      ,
      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(index = "3", type = TEXT)
      @Pkg(label = "Column name")
      String colName,

      @Idx(index = "4", type = AttributeType.RADIO, options = {
          @Idx.Option(index = "4.1", pkg = @Pkg(label = "To end of table", value = "END")),
          @Idx.Option(index = "4.2", pkg = @Pkg(label = "At specific index", value = "INDEX")),
      })
      @Pkg(label = "Add column", default_value = "END", default_value_type = STRING)
      @NotEmpty
      String insertType,

      @Idx(index = "4.2.1", type = AttributeType.NUMBER)
      @Pkg(description = "Index numbers start with 0. For example, first column has an index of 0")
      @NotEmpty
      @GreaterThanEqualTo("0")
      Double itemPosition,

      @Idx(index = "5", type = SELECT, options = {
          @Idx.Option(index = "5.1", pkg = @Pkg(label = "Default", value = "DEFAULT")),
          @Idx.Option(index = "5.2", pkg = @Pkg(label = "List", value = "LIST"))})
      @Pkg(label = "Column value", description = "Added column's cell values", default_value = "DEFAULT",
          default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String insertMethod,

      @Idx(index = "5.1.1", type = AttributeType.VARIABLE)
      @Pkg(label = "Default value will be set for all rows in the new column")
      @NotEmpty
      Value defaultValue,

      @Idx(index = "5.2.1", type = AttributeType.LIST)
      @Pkg(label = "List of cell values", description = "List size should not be less than table row count")
      @NotEmpty
      List<Value> columnValues

  ) {
    try {
      Table Output = TableUtil.copyTable(inputTable);
      boolean insertAtEnd = insertType.equals("END");
      int insertIndex = insertAtEnd ? Output.getSchema().size() : itemPosition.intValue();

      boolean insertDefaultValues = insertMethod.equals("DEFAULT");

      if (!insertDefaultValues && columnValues.size() < Output.getRows().size()) {
        throw new BotCommandException(
            "List of added column's cell values size is less than the existing " +
                "number of rows");
      }

      Output.getSchema().add(insertIndex, new Schema(colName));
      int rowCounter = 0;
      for (Row row : Output.getRows()) {
        if (insertDefaultValues) {
          row.getValues().add(insertIndex, defaultValue);
        } else {
          row.getValues().add(insertIndex, columnValues.get(rowCounter));
        }
        rowCounter++;
      }

      return new TableValue(Output);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error Occurred while adding column to table: " + e.getMessage(), e);
    }

  }

}
