package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.HELP;
import static com.automationanywhere.commandsdk.model.AttributeType.SELECT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Inject;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import java.util.stream.Collectors;
import sumit.devtools.utils.TableUtil;


@BotCommand
@CommandPkg(label = "Set schema", description = "Creates an independent copy of the table with updated schema. The original table remains unchanged.", icon = "Table.svg", name =
    "copySchema", group_label = "Table",
    node_label =
        "Create copy of {{inputTable}} with schema from |{{inputRecord}} || {{inputSchemaTable}}| and assign to " +
            "{{returnTo}}", return_description = "Independent copy of table with updated schema",
    return_required = true,
    return_label = "Assign modified table to",
    return_type = DataType.TABLE)

public class SetTableSchema {

  @Execute
  public static TableValue action(

      @Idx(index = "1", type = HELP)
      @Pkg(label = "Tip", description = "This action creates an independent copy. To update the original table, assign the output back to the same variable.")
      @Inject
      String help,

      @Idx(index = "2", type = SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Record", value = "record")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Table", value = "table"))})
      @Pkg(label = "Source of schema", default_value = "record", default_value_type = STRING)
      @NotEmpty
      @SelectModes
      String selectMethod,

      @Idx(index = "2.1.1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select record whose schema is to be copied")
      @NotEmpty
      @VariableType(value = DataType.RECORD)
      Record inputRecord,

      @Idx(index = "2.2.1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select Table whose schema is to be copied")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputSchemaTable,

      @Idx(index = "3", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(index = "4", type = AttributeType.RADIO, options = {
          @Idx.Option(index = "4.1", pkg = @Pkg(label = "Overwrite schema", value = "overwrite_schema")),
          @Idx.Option(index = "4.2", pkg = @Pkg(label = "Append schema", value = "append_schema"))
      })
      @Pkg(label = "Update method", default_value_type = STRING, description = "Choose method to update",
          default_value = "overwrite_schema")
      @NotEmpty
      String updatecriteria

  ) {
    try {
      Table Output = TableUtil.copyTable(inputTable);
      List<Schema> sourceSchema;
      if (selectMethod.equals("record")) {
        sourceSchema = inputRecord.getSchema();
      } else if (selectMethod.equals("table")) {
        sourceSchema = inputSchemaTable.getSchema();
      } else {
        throw new BotCommandException("Invalid select method: " + selectMethod);
      }

      if (updatecriteria.equals("append_schema")) {
        for (Schema curentRSchema : sourceSchema) {
          if (!Output.getSchema().contains(curentRSchema)) {
            Output.getSchema().add(new Schema(curentRSchema.getName(), curentRSchema.getType()));
          }
        }

      } else if (updatecriteria.equals("overwrite_schema")) {
        List<Schema> copySchema = sourceSchema.stream()
            .map(schema -> new Schema(schema.getName(), schema.getType()))
            .collect(Collectors.toList());
        Output.setSchema(copySchema);
      } else {
        throw new BotCommandException("Invalid update method: " + updatecriteria);
      }

      return new TableValue(Output);

    } catch (Exception e) {
      throw new BotCommandException(
          "Error Occurred while setting table schema value: " + e.getMessage(), e);
    }


  }

}
