/**
 * @author Sumit Kumar
 */
package sumit.devtools.conditionals.record;

import static com.automationanywhere.commandsdk.annotations.BotCommand.CommandType.Condition;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

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
import sumit.devtools.utils.TableUtil;


@BotCommand(commandType = Condition)
@CommandPkg(
    description = "Checks if record has specified column header in schema",
    name = "hasRecordHeader",
    label = "Record has column",
    icon = "Record.svg",
    node_label = "Record {{inputRecord}} {{conditiontype}} header {{colName}}"
)
public class HasRecordHeader {

  @ConditionTest
  public static Boolean validate(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Record")
      @NotEmpty
      @VariableType(value = DataType.RECORD)
      Record inputRecord,
      @Idx(
          index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Has column", value = "CONTAINS")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Does not have column", value = "NOTCONTAINS"))
      }
      )
      @Pkg(label = "Condition", default_value = "CONTAINS", default_value_type = STRING)
      @NotEmpty
      String conditiontype,
      @Idx(index = "3", type = TEXT)
      @Pkg(label = "Column header")
      @NotEmpty
      String colName,

      @Idx(index = "4", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether column name comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name' matches 'name', 'NAME'). " +
              "When true: Case-sensitive (e.g., 'Name' does not match 'name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean caseSensitive
  ) {

    try {
      int schemaIndex = TableUtil.getColumnIndex(inputRecord.getSchema(), colName, caseSensitive);

      if (conditiontype.equalsIgnoreCase("CONTAINS")) {
        return schemaIndex >= 0;
      } else {
        return schemaIndex < 0;
      }
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while checking existence of header in record: " + e.getMessage(), e);
    }
  }

}
