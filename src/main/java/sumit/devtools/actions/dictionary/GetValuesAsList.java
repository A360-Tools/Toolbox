package sumit.devtools.actions.dictionary;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Get values as list",
    description = "Returns all values from a dictionary as a list",
    icon = "Dictionary.svg",
    name = "getValuesAsList",
    group_label = "Dictionary",
    node_label = "Get values from {{dictionary}} and assign to {{returnTo}}",
    return_description = "Returns a list of dictionary values",
    return_required = true,
    return_label = "Assign values list to",
    return_type = DataType.LIST, return_sub_type = DataType.ANY)
public class GetValuesAsList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Dictionary")
      @NotEmpty
      @VariableType(value = DataType.DICTIONARY)
      Map<String, Value> dictionary
  ) {
    try {
      List<Value> valuesList = new ArrayList<>(dictionary.values());

      ListValue retList = new ListValue();
      retList.set(valuesList);
      return retList;
    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while getting values as list: " + e.getMessage(), e);
    }
  }

}