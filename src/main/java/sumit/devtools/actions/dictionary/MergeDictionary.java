package sumit.devtools.actions.dictionary;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
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
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Merge dictionary",
    description = "Merges two dictionaries into one. " +
        "If the same key exists in both, the value from the second dictionary will be used.",
    icon = "Dictionary.svg",
    name = "mergeDictionary",
    group_label = "Dictionary",
    node_label = "Merge {{firstDict}} and {{secondDict}} and assign to {{returnTo}}",
    return_description = "Dictionary with merged keys and values",
    return_required = true,
    return_label = "Assign merged dictionary to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_type = DataType.DICTIONARY)
public class MergeDictionary {

  @Execute
  public static DictionaryValue action(
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
      Map<String, Value> mergedDict = new LinkedHashMap<>(firstDict);
      mergedDict.putAll(secondDict);
      return new DictionaryValue(mergedDict);

    } catch (Exception e) {
      throw new BotCommandException(
          "Error Occurred while merging dictionary values: " + e.getMessage(), e);
    }

  }

}
