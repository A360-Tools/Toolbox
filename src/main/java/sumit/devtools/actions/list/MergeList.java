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
import sumit.devtools.utils.ListUtil;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Merge list", description = "Creates an independent merged copy combining items from both lists. The original lists remain unchanged.",
    icon = "List.svg",
    name = "mergeList",
    group_label = "List",
    node_label = "Create merged copy of {{firstList}} and {{secondList}} and assign to {{returnTo}}",
    return_description = "Independent merged copy containing elements from both lists",
    return_required = true,
    return_label = "Assign merged list to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)
public class MergeList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select first list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> firstList,

      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Select second list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> secondList
  ) {
    try {
      List<Value> mergedList = ListUtil.deepCopyList(firstList);
      mergedList.addAll(ListUtil.deepCopyList(secondList));

      // Create and return the ListValue containing the merged list
      ListValue retList = new ListValue();
      retList.set(mergedList);
      return retList;

    } catch (Exception e) {
      throw new BotCommandException("Error occurred while merging lists: " + e.getMessage(), e);
    }
  }

}
