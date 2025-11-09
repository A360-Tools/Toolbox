package sumit.devtools.actions.table;

import static com.automationanywhere.commandsdk.model.AttributeType.HELP;

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
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import sumit.devtools.utils.TableUtil;

@BotCommand
@CommandPkg(
    label = "Normalize headers",
    name = "NormalizeHeaders",
    icon = "Table.svg",
    group_label = "Table",
    description = "Creates an independent copy of the table with normalized headers. The original table remains unchanged.",
    node_label = "Create copy of {{inputTable}} with normalized headers and assign to {{returnTo}}",
    return_description = "Independent copy of table with normalized headers",
    return_required = true,
    return_label = "Assign modified table to",
    return_type = DataType.TABLE
)

public class NormalizeHeaders {

  @Execute
  public static TableValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Base table")
      @NotEmpty
      @VariableType(value = DataType.TABLE)
      Table inputTable,

      @Idx(index = "2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Normalize whitespace",
          description = "Trims leading/trailing whitespace and collapses multiple consecutive whitespace characters into a single space. " +
              "When false (default): Whitespace is preserved as-is (e.g., '  First  Name  ' remains '  First  Name  '). " +
              "When true: Whitespace is normalized (e.g., '  First  Name  ' becomes 'First Name').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean normalizeSpaces,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Remove all whitespace",
          description = "Removes all whitespace characters completely from header names. " +
              "When false (default): Whitespace is preserved (e.g., 'First Name' remains 'First Name'). " +
              "When true: All whitespace is removed (e.g., 'First Name' becomes 'FirstName').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean removeAllWhitespace,

      @Idx(index = "4", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "4.1", pkg = @Pkg(label = "None", value = "NONE")),
          @Idx.Option(index = "4.2", pkg = @Pkg(label = "Lowercase", value = "LOWERCASE")),
          @Idx.Option(index = "4.3", pkg = @Pkg(label = "Uppercase", value = "UPPERCASE"))
      })
      @Pkg(label = "Convert case",
          description = "Converts the case of all characters in header names. " +
              "None (default): Case is preserved (e.g., 'FirstName' remains 'FirstName'). " +
              "Lowercase: All letters become lowercase (e.g., 'FirstName' becomes 'firstname'). " +
              "Uppercase: All letters become uppercase (e.g., 'FirstName' becomes 'FIRSTNAME').",
          default_value = "NONE",
          default_value_type = DataType.STRING)
      @NotEmpty
      String convertCase,

      @Idx(index = "5", type = HELP)
      @Pkg(label = "Tip", description = "This action creates an independent copy. To update the original table, assign the output back to the same variable. " +
          "Options are applied in order: normalize whitespace → remove whitespace → convert case.")
      @Inject
      String help
  ) {
    try {
      // Create deep copy of the table
      Table Output = TableUtil.copyTable(inputTable);

      // Apply transformations to each header
      for (Schema schema : Output.getSchema()) {
        String headerName = schema.getName();

        // Step 1: Normalize whitespace (if enabled)
        if (normalizeSpaces) {
          // Trim leading/trailing whitespace
          headerName = headerName.strip();
          // Collapse multiple consecutive whitespace characters to single space
          headerName = headerName.replaceAll("\\s+", " ");
        }

        // Step 2: Remove all whitespace (if enabled)
        if (removeAllWhitespace) {
          headerName = headerName.replaceAll("\\s+", "");
        }

        // Step 3: Convert case (if specified)
        if (convertCase != null) {
          switch (convertCase.toUpperCase()) {
            case "LOWERCASE":
              headerName = headerName.toLowerCase();
              break;
            case "UPPERCASE":
              headerName = headerName.toUpperCase();
              break;
            case "NONE":
            default:
              // No case conversion
              break;
          }
        }

        // Update the schema with normalized name
        schema.setName(headerName);
      }

      return new TableValue(Output);
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while normalizing headers: " + e.getMessage(), e);
    }

  }

}
