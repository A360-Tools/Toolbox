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
import com.automationanywhere.commandsdk.annotations.rules.GreaterThan;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.VariableType;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import sumit.devtools.utils.ListUtil;


/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Chunk list",
    description = "Creates independent sublists by dividing the list into chunks of specified size. The original list remains unchanged.",
    icon = "List.svg",
    name = "chunkList",
    group_label = "List",
    node_label = "Create independent chunks from {{inputList}} and assign to {{returnTo}}",
    return_description = "List of independent sublists, each with size <= specified chunk size",
    return_required = true,
    return_label = "Assign chunked list to",
//        allowed_agent_targets = AllowedTarget.HEADLESS,
    return_sub_type = ANY,
    return_type = DataType.LIST)

public class ChunkList {

  @Execute
  public static ListValue action(
      @Idx(index = "1", type = AttributeType.VARIABLE)
      @Pkg(label = "Select list")
      @NotEmpty
      @VariableType(value = DataType.LIST)
      List<Value> inputList,
      @Idx(index = "2", type = AttributeType.NUMBER)
      @Pkg(label = "Enter max size of sub lists")
      @NotEmpty
      @GreaterThan("0")
      Double chunkSize

  ) {
    try {
      if (chunkSize.intValue() <= 0) {
        throw new BotCommandException("Invalid chunk size, provide value greater than 0");
      }

      ListValue retListValue = new ListValue();
      ArrayList<ListValue> returnList = new ArrayList<>();

      List<List<Value>> batches = getBatches(inputList, chunkSize.intValue());

      for (List<Value> batch : batches) {
        ListValue currentbatch = new ListValue();
        // Deep copy each batch to ensure independence
        currentbatch.set(ListUtil.deepCopyList(batch));
        returnList.add(currentbatch);
      }
      retListValue.set(returnList);
      return retListValue;

    } catch (Exception e) {
      throw new BotCommandException("Error occurred while chunking list: " + e.getMessage(), e);
    }

  }

  public static <T> List<List<T>> getBatches(List<T> collection, int batchSize) {
    return IntStream.iterate(0, i -> i < collection.size(), i -> i + batchSize)
        .mapToObj(i -> collection.subList(i, Math.min(i + batchSize, collection.size())))
        .collect(Collectors.toList());
  }

}
