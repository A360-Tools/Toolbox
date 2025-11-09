package sumit.devtools.actions.list;

import static com.automationanywhere.commandsdk.model.DataType.ANY;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import sumit.devtools.utils.ListUtil;
import sumit.devtools.utils.SortUtil;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Sort list",
    description = "Creates an independent copy of the list with items sorted in the specified order. The original list remains unchanged.",
    icon = "List.svg",
    name = "sortList",
    group_label = "List",
    node_label = "Create sorted copy of {{inputList}} in {{sortType}} order and assign to {{returnTo}}",
    return_description = "Independent copy of list sorted in specified order",
    return_required = true,
    return_label = "Assign sorted list to",
    //allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)
public class SortList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> inputList,

      @Idx(index = "2", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "2.1", pkg = @Pkg(label = "Ascending", value = "ASCENDING")),
          @Idx.Option(index = "2.2", pkg = @Pkg(label = "Descending", value = "DESCENDING")),
      })
      @Pkg(label = "Sort order", default_value = "ASCENDING", default_value_type = STRING)
      @SelectModes
      @NotEmpty String sortType,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether string comparison should be case-sensitive during sorting. " +
              "When false (default): Case-insensitive (e.g., 'Apple', 'apple', 'Banana' sorts as 'Apple', 'apple', 'Banana'). " +
              "When true: Case-sensitive (e.g., 'Apple', 'Banana', 'apple' sorts as 'Apple', 'Banana', 'apple').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean caseSensitive
  ) {
    try {
      List<Value> sortedList = ListUtil.deepCopyList(inputList);
      // Sort using the case-sensitive parameter
      switch (sortType.toUpperCase()) {
        case "ASCENDING":
          SortUtil.sortAscending(sortedList, caseSensitive);
          break;
        case "DESCENDING":
          SortUtil.sortDescending(sortedList, caseSensitive);
          break;
        default:
          throw new IllegalArgumentException("Invalid sort type: " + sortType);
      }
      ListValue retList = new ListValue();
      retList.set(sortedList);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while sorting the list: " + e.getMessage(), e);
    }
  }

}
