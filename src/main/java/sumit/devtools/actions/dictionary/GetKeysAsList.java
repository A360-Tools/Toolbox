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


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Get keys as list",
    description = "Returns all keys from a dictionary as a list",
    icon = "Dictionary.svg",
    name = "getKeysAsList",
    group_label = "Dictionary",
    node_label = "Get keys from {{dictionary}} and assign to {{returnTo}}",
    return_description = "Returns a list of dictionary keys",
    return_required = true,
    return_label = "Assign keys list to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.LIST, return_sub_type = DataType.STRING)
public class GetKeysAsList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Dictionary")
      @NotEmpty
      @VariableType(value = DataType.DICTIONARY)
      Map<String, Value> dictionary
  ) {
    try {
      List<Value> keysList = dictionary.keySet().stream()
          .map(StringValue::new)
          .collect(Collectors.toList());

      ListValue<StringValue> retList = new ListValue<>();
      retList.set(keysList);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException("Error occurred while getting keys as list: " + e.getMessage(),
          e);
    }
  }

}