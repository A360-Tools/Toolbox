package sumit.devtools.actions.list;

import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.ListType;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(
    label = "Strip list items",
    description = "Removes leading and trailing whitespace from each string in a list",
    icon = "List.svg",
    name = "stripList",
    group_label = "List",
    node_label = "Strip items in {{inputList}} and assign to {{returnTo}}",
    return_description = "List with stripped string values",
    return_required = true,
    return_label = "Assign stripped list to",
    return_sub_type = STRING,
    return_type = DataType.LIST
)
public class StripList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.LIST)
      @Pkg(label = "Select list")
      @NotEmpty
      @ListType(value = DataType.STRING)
      List<StringValue> inputList
  ) {
    try {
      // Create a new list for the stripped values
      List<StringValue> strippedList = inputList.stream()
          .map(value -> {
            // Get the string value and strip it
            String strippedString = value.get().strip();
            // Create a new StringValue with the stripped string
            return new StringValue(strippedString);
          })
          .collect(Collectors.toList());

      // Create and return the ListValue containing the stripped values
      ListValue retList = new ListValue();
      retList.set(strippedList);
      return retList;

    } catch (Exception e) {
      throw new BotCommandException(
          "Error occurred while stripping values in list: " + e.getMessage(), e);
    }
  }

}