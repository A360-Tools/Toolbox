package sumit.devtools.actions.record;

import static com.automationanywhere.commandsdk.model.AttributeType.NUMBER;
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.RECORD;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NumberInteger;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import sumit.devtools.utils.TableUtil;

@BotCommand
@CommandPkg(label = "Update cell", description = "Creates an independent copy of the record with the specified cell updated. The original record remains unchanged.", icon = "Record.svg", name = "updateRecordCell",
    group_label = "Record",
    node_label = "Create copy of {{inputRecord}} with updated cell (|{{colIndex}} || {{colName}}|) and assign to {{returnTo}}",
    return_description = "Independent copy of record with updated cell value",
    return_required = true,
    return_label = "Assign modified record to",
    return_type = DataType.RECORD)

public class UpdateCell {

  @Execute
  public static RecordValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base record")
      @NotEmpty
      @VariableType(value = RECORD)
      Record inputRecord,

      @Idx(index = "2", type = SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Header", value = "name")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Index", value = "index"))})
      @Pkg(label = "Select column by", description = "Column whose value is to be updated", default_value =
          "name", default_value_type = DataType.STRING)
      @NotEmpty
      @SelectModes
      String selectMethod,

      @Idx(index = "2.1.1", type = TEXT)
      @Pkg(label = "Column header")
      @NotEmpty
      String colName,

      @Idx(index = "2.1.2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether column name comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name' matches 'name', 'NAME'). " +
              "When true: Case-sensitive (e.g., 'Name' does not match 'name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean caseSensitive,

      @Idx(index = "2.2.1", type = NUMBER)
      @Pkg(label = "Column index", description =
          "Index numbers start with 0. For example,first column has an " +
              "index of 0.")
      @NotEmpty
      @GreaterThanEqualTo("0")
      @NumberInteger
      Double colIndex,

      @Idx(index = "3", type = AttributeType.VARIABLE)
      @Pkg(label = "New value of cell")
      @NotEmpty
      Value newValue

  ) {
    try {

      Record copyRecord = TableUtil.copyRecord(inputRecord);
      int schemaIndex;

      if (selectMethod.equals("name")) {
        schemaIndex = TableUtil.getColumnIndex(copyRecord.getSchema(), colName, caseSensitive);

      } else {
        schemaIndex = colIndex.intValue();
      }

      if (schemaIndex < 0) {
        throw new BotCommandException("Column could not be found, check column name/index");
      }

      copyRecord.getValues().set(schemaIndex, newValue);

      return new RecordValue(copyRecord.getSchema(), copyRecord.getValues());

    } catch (Exception e) {
      throw new BotCommandException("Error Occurred while updating record cell: " + e.getMessage(),
          e);
    }


  }

}
