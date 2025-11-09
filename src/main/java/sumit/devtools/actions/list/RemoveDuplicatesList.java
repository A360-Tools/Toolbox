package sumit.devtools.actions.list;

import static com.automationanywhere.commandsdk.model.DataType.ANY;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import sumit.devtools.utils.ValueUtil;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Remove duplicates",
    description = "Creates an independent copy containing only unique items. The original list remains unchanged.",
    icon = "List.svg",
    name = "removeDupList", group_label = "List",
    node_label = "Create copy of {{inputList}} without duplicates and assign to {{returnTo}}",
    return_description = "Independent copy with only unique values",
    return_required = true,
    return_label = "Assign unique list to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)
public class RemoveDuplicatesList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> inputList,

      @Idx(index = "2", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether duplicate detection should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Name', 'name', 'NAME' are duplicates). " +
              "When true: Case-sensitive (e.g., 'Name', 'name', 'NAME' are unique items).",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean caseSensitive
  ) {
    try {
      ListValue retListValue = new ListValue();

      Set<String> seen = new LinkedHashSet<>();
      // Filter to keep only unique items and deep copy each one
      List<Value> uniqueList = inputList.stream()
          .filter(item -> seen.add(caseSensitive
              ? item.get().toString()
              : item.get().toString().toLowerCase()))
          .map(ValueUtil::deepCopyValue)
          .collect(Collectors.toList());

      retListValue.set(uniqueList);
      return retListValue;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while removing duplicates: " + e.getMessage(),
          e);
    }
  }

}
