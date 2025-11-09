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
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import sumit.devtools.utils.ListUtil;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Add item",
    description = "Creates an independent copy of the list and adds an item to it. The original list remains unchanged.",
    icon = "List.svg",
    name = "addItemList",
    group_label = "List",
    node_label = "Create copy of {{inputList}}, add {{item}}, and assign to {{returnTo}}",
    return_description = "Independent copy of list with the item added",
    return_required = true,
    return_label = "Assign modified list to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)
public class AddItem {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select list to copy from")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> inputList,

      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Item to be added")
      @NotEmpty
      Value item,

      @Idx(index = "3", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "3.1", pkg = @Pkg(label = "End", value = "END")),
          @Idx.Option(index = "3.2", pkg = @Pkg(label = "Index", value = "INDEX")),
      })
      @Pkg(label = "Add item at position", default_value = "END", default_value_type = DataType.STRING)
      @SelectModes
      @NotEmpty String insertType,

      @Idx(index = "3.2.1", type = AttributeType.NUMBER)
      @Pkg(label = "Enter item position index", description = "Should be between 0 and the size of the list.")
      @GreaterThanEqualTo("0")
      Double itemPosition
  ) {
    try {
      List<Value> newList = ListUtil.deepCopyList(inputList);

      if ("END".equals(insertType)) {
        newList.add(item);
      } else {
        int index = itemPosition.intValue();
        if (index > newList.size()) {
          throw new BotCommandException(
              "Item position index out of bounds. Must be between 0 and " +
                  newList.size() + ".");
        }
        newList.add(index, item);
      }
      ListValue retList = new ListValue();
      retList.set(newList);

      return retList;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while adding item to list: " + e.getMessage(),
          e);
    }
  }

}
