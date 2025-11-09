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
import java.util.List;
import java.util.stream.Collectors;
import sumit.devtools.utils.ValueUtil;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Remove empty/blank Values",
    description = "Creates an independent copy containing only non-empty items. The original list remains unchanged.",
    icon = "List.svg",
    name = "removeEmptyVal",
    group_label = "List",
    node_label = "Create copy of {{inputList}} without empty values and assign to {{returnTo}}",
    return_description = "Independent copy without empty or blank values",
    return_required = true,
    return_label = "Assign filtered list to",
    //allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)
public class RemoveEmptyItemsList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> inputList
  ) {
    try {
      ListValue retListValue = new ListValue();
      // Filter out empty values from the list
      // Deep copy each filtered item to ensure independence
      List<Value> filteredList = inputList.stream()
          .filter(item -> item != null && !item.get().toString().trim().isBlank())
          .map(ValueUtil::deepCopyValue)
          .collect(Collectors.toList());

      // Directly return the new ListValue containing the filtered list
      retListValue.set(filteredList);
      return retListValue;
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while removing empty values from lists " + e.getMessage(), e);
    }
  }

}
