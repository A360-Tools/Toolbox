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
import java.util.Collections;
import java.util.List;
import sumit.devtools.utils.ListUtil;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Reverse list",
    description = "Creates an independent copy of the list with items in reverse order. The original list remains unchanged.",
    icon = "List.svg",
    name = "reverseList",
    group_label = "List",
    node_label = "Create reversed copy of {{inputList}} and assign to {{returnTo}}",
    return_description = "Independent copy of list with reversed item order",
    return_required = true,
    return_label = "Assign reversed list to",
    //allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)
public class ReverseList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> inputList
  ) {
    try {
      List<Value> reversedList = ListUtil.deepCopyList(inputList);
      Collections.reverse(reversedList);

      ListValue retListValue = new ListValue();
      retListValue.set(reversedList);

      return retListValue;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while finding reversing list " + e.getMessage(),
          e);
    }
  }

}
