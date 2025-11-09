package sumit.devtools.actions.dictionary;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Find common keys",
    description = "Finds common keys between two dictionaries",
    icon = "Dictionary.svg",
    name = "findCommonKeys",
    group_label = "Dictionary",
    node_label = "Find common keys between {{firstDict}} & {{secondDict}} and assign to {{returnTo}}",
    return_description = "Returns list of common keys",
    return_required = true,
    return_label = "Assign common keys to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.LIST, return_sub_type = DataType.STRING)
public class FindCommonKeys {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "First dictionary")
      @NotEmpty
      @VariableType(value = DataType.DICTIONARY)
      Map<String, Value> firstDict,

      @Idx(index = "2", type = AttributeType.VARIABLE)
      @Pkg(label = "Second dictionary")
      @NotEmpty
      @VariableType(value = DataType.DICTIONARY)
      Map<String, Value> secondDict
  ) {
    try {
      Set<String> commonKeys = firstDict.keySet().stream()
          .filter(secondDict::containsKey)
          .collect(Collectors.toSet());

      List<Value> commonKeysList = commonKeys.stream()
          .map(StringValue::new)
          .collect(Collectors.toList());

      ListValue<?> retList = new ListValue<>();
      retList.set(commonKeysList);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while finding common keys " + e.getMessage(),
          e);
    }
  }

}
