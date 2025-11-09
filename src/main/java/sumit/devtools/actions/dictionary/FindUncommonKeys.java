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
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Find uncommon keys",
    description = "Finds keys that are unique to each of two dictionaries",
    icon = "Dictionary.svg",
    name = "findUncommonKeys",
    group_label = "Dictionary",
    node_label = "Find uncommon keys between {{firstDict}} & {{secondDict}} and assign to {{returnTo}}",
    return_description = "Returns a list of uncommon keys",
    return_required = true,
    return_label = "Assign uncommon keys to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.LIST, return_sub_type = DataType.STRING)
public class FindUncommonKeys {

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
      ListValue<StringValue> retList = new ListValue<>();

      // Combine and filter uncommon keys from both dictionaries
      List<Value> uncommonKeysList = Stream.concat(firstDict.keySet().stream(),
              secondDict.keySet().stream())
          .filter(key -> !(firstDict.containsKey(key) && secondDict.containsKey(key)))
          .distinct()
          .map(StringValue::new)
          .collect(Collectors.toList());

      retList.set(uncommonKeysList);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while finding uncommon keys " + e.getMessage(),
          e);
    }
  }

}
