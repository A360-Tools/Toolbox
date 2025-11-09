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
import java.util.Set;
import java.util.stream.Collectors;
import sumit.devtools.utils.ValueUtil;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "List difference",
    description = "Creates an independent copy containing items from the first list that are not in the second list. The original lists remain unchanged.",
    icon = "List.svg",
    name = "diffList",
    group_label = "List",
    node_label = "Create difference copy from {{firstList}} and {{secondList}} and assign to {{returnTo}}",
    return_description = "Independent copy with items present in first list but not in second",
    return_required = true,
    return_label = "Assign difference list to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)

public class DiffList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select first List")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> firstList,

      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Select second List")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> secondList,

      @Idx(index = "3", type = AttributeType.BOOLEAN)
      @Pkg(label = "Case-sensitive comparison",
          description = "Controls whether item comparison should be case-sensitive. " +
              "When false (default): Case-insensitive (e.g., 'Test' matches 'test', 'TEST'). " +
              "When true: Case-sensitive (e.g., 'Test' does not match 'test').",
          default_value = "false",
          default_value_type = DataType.BOOLEAN)
      @NotEmpty
      Boolean caseSensitive

  ) {
    try {
      ListValue retListValue = new ListValue();

      Set<String> comparisonSet = secondList.stream()
          .map(value -> caseSensitive
              ? value.get().toString()
              : value.get().toString().toLowerCase())
          .collect(Collectors.toSet());

      // Filter firstList to include only elements not present in secondList
      // Deep copy each filtered item to ensure independence
      List<Value> differenceList = firstList.stream()
          .filter(item -> !comparisonSet.contains(caseSensitive
              ? item.get().toString()
              : item.get().toString().toLowerCase()))
          .map(ValueUtil::deepCopyValue)
          .collect(Collectors.toList());

      retListValue.set(differenceList);
      return retListValue;

    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while finding list difference: " + e.getMessage(), e);
    }
  }

}
