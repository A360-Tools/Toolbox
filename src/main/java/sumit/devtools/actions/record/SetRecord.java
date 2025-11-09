package sumit.devtools.actions.record;

import static com.automationanywhere.commandsdk.model.AttributeType.ENTRYLIST;
import static com.automationanywhere.commandsdk.model.AttributeType.HELP;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.ANY;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Inject;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListAddButtonLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEmptyLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEntryUnique;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListLabel;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import java.util.Map;
import sumit.devtools.utils.TableUtil;


@BotCommand
@CommandPkg(label = "Set record", description = "Creates an independent copy of the record with specified columns and values set. The original record remains unchanged.", icon = "Record.svg", name =
    "SetRecord", group_label = "Record",
    node_label = "Create copy of {{rowRecord}} with updated columns and assign to {{returnTo}}",
    return_description = "Independent copy of record with updated column values",
    return_required = true,
    return_label = "Assign modified record to",
    return_type = DataType.RECORD)

public class SetRecord {

  @Idx(index = "3.3", type = TEXT, name = "NAME")
  @Pkg(label = "Name", default_value_type = DataType.STRING)
  @NotEmpty
  private String colName;

  @Idx(index = "3.4", type = AttributeType.VARIABLE, name = "VALUE")
  @Pkg(label = "Value", default_value_type = ANY)
  private Value colValue;

  @Execute
  public static RecordValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Template record")
      @NotEmpty
      @VariableType(value = DataType.RECORD)
      Record rowRecord,

      @Idx(index = "3", type = ENTRYLIST, options = {
          @Idx.Option(index = "3.1", pkg = @Pkg(title = "NAME", label = "Column Name", node_label =
              "{{NAME" +
                  "}}")),
          @Idx.Option(index = "3.2", pkg = @Pkg(title = "VALUE", label = "Column Value", node_label =
              "{{VALUE}}")),
      })
      //Label you see at the top of the control
      @Pkg(label = "Add required column name and corresponding value", description =
          "Update record value based" +
              " on column name")
      //Header of the entry form
      @EntryListLabel(value = "Provide entry")
      //Button label which displays the entry form
      @EntryListAddButtonLabel(value = "Add entry")
      //Uniqueness rule for the column, this value is the TITLE of the column requiring uniqueness.
      @EntryListEntryUnique(value = "NAME")
      //Message to display in table when no entries are present.
      @EntryListEmptyLabel(value = "No value to update")
      @NotEmpty List<Value> list,

      @Idx(index = "4", type = AttributeType.BOOLEAN)
      @Pkg(label = "Add column, if does not exist already", description = "Adds missing column and "
          +
          "corresponding value, if set to true", default_value_type = DataType.BOOLEAN, default_value =
          "true")
      @NotEmpty
      Boolean bAddIfMissing,

      @Idx(index = "5", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive header matching",
          description = "Controls whether column name comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name' matches 'name', 'NAME'). " +
              "When true: Case-sensitive (e.g., 'Name' does not match 'name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean caseSensitive,

      @Idx(index = "6", type = HELP)
      @Pkg(label = "Tip", description =
          "This action creates an independent copy. To update the original record, " +
              "assign the output back to the same variable.")
      @Inject
      String help

  ) {
    try {
      Record copyRecord = TableUtil.copyRecord(rowRecord);
      if (list != null && !list.isEmpty()) {

        for (Value element : list) {
          Map<String, Value> customValuesMap = ((DictionaryValue) element).get();

          String sColheader =
              customValuesMap.containsKey("NAME") ? ((StringValue) customValuesMap.get(
                  "NAME")).get() : "";
          Value sValue = (customValuesMap.getOrDefault("VALUE", null) == null) ? null :
              (customValuesMap.get("VALUE"));

          if (sValue != null) {
            if (sColheader.isBlank()) {
              throw new BotCommandException("Column name cannot be empty");
            }

            List<Schema> ls = copyRecord.getSchema();
            List<Value> lv = copyRecord.getValues();

            int col = TableUtil.getColumnIndex(ls, sColheader, caseSensitive);

            if (col == -1 && !bAddIfMissing) {
              throw new BotCommandException("Field name not found in record schema");
            }

            if (col == -1) {
              ls.add(new Schema(sColheader));
              lv.add(sValue);
            } else {
              lv.set(col, sValue);
            }
            copyRecord.setSchema(ls);
            copyRecord.setValues(lv);
          }

        }

      }

      RecordValue rv = new RecordValue();
      rv.set(copyRecord);
      return rv;
    } catch (Exception e) {
      throw new BotCommandException("Error Occurred while setting record value: " + e.getMessage(),
          e);
    }

  }

}
