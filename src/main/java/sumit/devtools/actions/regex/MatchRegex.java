package sumit.devtools.actions.regex;

import static com.automationanywhere.commandsdk.model.DataType.BOOLEAN;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Sumit Kumar This action finds matches for a regex pattern and extracts the full match,
 * all numbered capture groups, and specified named capture groups.
 */
@BotCommand
@CommandPkg(
    label = "Match Regex",
    name = "matchRegex",
    description = "Finds regex matches and extracts full matches, numbered groups, and specified named groups.",
    icon = "String.svg",
    group_label = "Regex",
    node_label = "Find matches and groups in {{inputString}} using pattern {{regexPattern}}",
    return_required = true,
    return_description =
        "Returns a dictionary containing lists of full matches, numbered groups per match, and " +
            "named groups per match.",
    multiple_returns = {
        @CommandPkg.Returns(return_name = "FullMatches", return_type = DataType.LIST, return_sub_type =
            STRING, return_label = "Full Matches", return_description = "A list of all full string "
            +
            "matches."),
        @CommandPkg.Returns(return_name = "NumberedGroupsByMatch", return_type = DataType.LIST,
            return_sub_type = DataType.LIST, return_label = "Numbered Groups by Match",
            return_description =
                "A list of lists. Each inner list contains numbered capture groups " +
                    "(group 1, 2, ...) for a corresponding full match."),
        @CommandPkg.Returns(return_name = "NamedGroupsByMatch", return_type = DataType.LIST, return_sub_type
            = DataType.DICTIONARY, return_label = "Named Groups by Match", return_description =
            "A list of dictionaries. Each inner dictionary contains specified named capture groups for a "
                + "corresponding full match.")
    }
)
public class MatchRegex {

  @Execute
  public static DictionaryValue action(
      @Idx(index = "1", type = AttributeType.TEXTAREA)
      @Pkg(label = "Input text", description = "Text to perform regex matching on.")
      @NotEmpty
      String inputString,

      @Idx(index = "2", type = AttributeType.TEXT)
      @Pkg(label = "Regular expression pattern", description =
          "The regex pattern, optionally with capturing " +
              "groups.")
      @NotEmpty
      String regexPattern,

      @Idx(index = "3", type = AttributeType.RADIO, options = {
          @Idx.Option(index = "3.1", pkg = @Pkg(label = "Find all matches", value = "FIND_ALL")),
          @Idx.Option(index = "3.2", pkg = @Pkg(label = "Find first match only", value = "FIND_FIRST"))
      })
      @Pkg(label = "Match scope", default_value = "FIND_ALL", default_value_type = STRING)
      @NotEmpty
      String matchScope,

      @Idx(index = "4", type = AttributeType.GROUP)
      @Pkg(label = "Regex options")
      String regexOptionsGroup, // UI Grouping element

      @Idx(index = "4.1", type = AttributeType.CHECKBOX)
      @Pkg(label = "Case insensitive", default_value_type = BOOLEAN, default_value = "false", description =
          "Enables case-insensitive matching (Pattern.CASE_INSENSITIVE).")
      Boolean caseInsensitive,

      @Idx(index = "4.2", type = AttributeType.CHECKBOX)
      @Pkg(label = "Multiline mode", default_value_type = BOOLEAN, default_value = "false", description =
          "^ " +
              "and $ match at the beginning and end of each line respectively (Pattern.MULTILINE).")
      Boolean multiline,

      @Idx(index = "4.3", type = AttributeType.CHECKBOX)
      @Pkg(label = "Dot matches all (DOTALL)", default_value_type = BOOLEAN, default_value = "false",
          description =
              "Enables dotall mode, where '.' matches any character, including line terminators " +
                  "(Pattern.DOTALL).")
      Boolean dotAll,

      @Idx(index = "4.4", type = AttributeType.CHECKBOX)
      @Pkg(label = "Unicode-aware case folding (UNICODE_CASE)", default_value_type = BOOLEAN, default_value =
          "false", description =
          "Enables Unicode-aware case folding when used with case-insensitive " +
              "matching (Pattern.UNICODE_CASE).")
      Boolean unicodeCase,

      @Idx(index = "4.5", type = AttributeType.CHECKBOX)
      @Pkg(label = "Permit whitespace and comments in pattern (COMMENTS)", default_value_type = BOOLEAN,
          default_value = "false", description =
          "Allows whitespace and comments in the regex pattern using" +
              " # (Pattern.COMMENTS).")
      Boolean comments,

      @Idx(index = "5", type = AttributeType.CHECKBOX)
      @Pkg(label = "Trim whitespace from matches and groups", default_value_type = BOOLEAN, default_value =
          "false", description =
          "If checked, removes leading and trailing whitespace from the full match " +
              "and each extracted group's value.")
      Boolean trimValues,

      @Idx(index = "6", type = AttributeType.CHECKBOX)
      @Pkg(label = "Include iterations for empty/blank full matches", default_value_type = BOOLEAN,
          default_value = "false", description =
          "If the full match (after potential trimming) is empty or " +
              "blank, checking this will still include its (empty) groups in the results. Otherwise, such "
              +
              "matches are skipped.")
      Boolean includeEmptyMatches,

      @Idx(index = "7", type = AttributeType.LIST)
      @Pkg(label = "Named groups to extract (Optional)", description =
          "Provide a list of specific named " +
              "capture group names you want to extract. e.g. [\"name\", \"email\"]")
      @ListType(value = DataType.STRING)
      List<Value> namedGroupsToExtract // Optional list of named groups provided by the user
  ) {
    // Initialize lists for different types of results
    List<Value> fullMatchesList = new ArrayList<>();
    List<Value> numberedGroupsByMatchList = new ArrayList<>(); // List of ListValue<StringValue>
    List<Value> namedGroupsByMatchList = new ArrayList<>();    // List of DictionaryValue

    try {
      // Compile regex flags
      int flags = 0;
      if (Boolean.TRUE.equals(caseInsensitive)) {
        flags |= Pattern.CASE_INSENSITIVE;
      }
      if (Boolean.TRUE.equals(multiline)) {
        flags |= Pattern.MULTILINE;
      }
      if (Boolean.TRUE.equals(dotAll)) {
        flags |= Pattern.DOTALL;
      }
      if (Boolean.TRUE.equals(unicodeCase)) {
        flags |= Pattern.UNICODE_CASE;
      }
      if (Boolean.TRUE.equals(comments)) {
        flags |= Pattern.COMMENTS;
      }

      // Compile the regex pattern
      Pattern pattern;
      try {
        pattern = Pattern.compile(regexPattern, flags);
      } catch (PatternSyntaxException e) {
        throw new BotCommandException("Invalid regular expression pattern: " + e.getMessage(), e);
      }

      Matcher matcher = pattern.matcher(inputString);

      // Loop through matches
      while (matcher.find()) {
        String currentFullMatch = matcher.group(0); // group(0) is the full match
        if (Boolean.TRUE.equals(trimValues) && currentFullMatch != null) {
          currentFullMatch = currentFullMatch.strip();
        }

        // Logic for includeEmptyMatches applies to the full match string
        boolean isFullMatchEffectivelyEmpty = (currentFullMatch == null
            || currentFullMatch.isBlank());

        if (!isFullMatchEffectivelyEmpty || Boolean.TRUE.equals(includeEmptyMatches)) {
          fullMatchesList.add(new StringValue(currentFullMatch != null ? currentFullMatch : ""));

          // Process numbered groups for this match
          ListValue<StringValue> currentNumberedGroupsListValue = new ListValue<>();
          List<Value> currentNumberedGroups = new ArrayList<>();
          for (int i = 1; i <= matcher.groupCount(); i++) { // Numbered groups start from 1
            String groupValue = matcher.group(i);
            if (Boolean.TRUE.equals(trimValues) && groupValue != null) {
              groupValue = groupValue.strip();
            }
            currentNumberedGroups.add(new StringValue(groupValue != null ? groupValue : ""));
          }
          currentNumberedGroupsListValue.set(currentNumberedGroups);
          numberedGroupsByMatchList.add(currentNumberedGroupsListValue);

          // Process specified named groups for this match
          Map<String, Value> currentNamedGroupsMap = new LinkedHashMap<>();
          if (namedGroupsToExtract != null && !namedGroupsToExtract.isEmpty()) {
            for (Value groupNameValue : namedGroupsToExtract) {
              if (groupNameValue != null && groupNameValue.get() != null) {
                String groupName = groupNameValue.get().toString();
                if (!groupName.isEmpty()) {
                  try {
                    String namedGroupValue = matcher.group(groupName);
                    if (Boolean.TRUE.equals(trimValues) && namedGroupValue != null) {
                      namedGroupValue = namedGroupValue.strip();
                    }
                    currentNamedGroupsMap.put(groupName, new StringValue(namedGroupValue != null
                        ? namedGroupValue : ""));
                  } catch (IllegalArgumentException e) {
                    // This means the named group doesn't exist in this pattern
                    // or was not matched. Add it with an empty string value.
                    currentNamedGroupsMap.put(groupName, new StringValue(""));
                  }
                }
              }
            }
          }
          namedGroupsByMatchList.add(new DictionaryValue(currentNamedGroupsMap));
        }

        // If only the first match is needed, break the loop
        if ("FIND_FIRST".equals(matchScope) && !fullMatchesList.isEmpty()) {
          break;
        }
      }

    } catch (Exception e) {
      throw new BotCommandException(
          "Error during regex match and group extraction: " + e.getMessage(), e);
    }

    // Prepare the final dictionary of results
    Map<String, Value> returnDictionaryMap = new LinkedHashMap<>();

    ListValue<StringValue> finalFullMatchesList = new ListValue<>();
    finalFullMatchesList.set(fullMatchesList);
    returnDictionaryMap.put("FullMatches", finalFullMatchesList);

    ListValue<ListValue<StringValue>> finalNumberedGroupsList = new ListValue<>();
    finalNumberedGroupsList.set(
        numberedGroupsByMatchList); // This should be List<Value> which contains
    // ListValue<StringValue>
    returnDictionaryMap.put("NumberedGroupsByMatch", finalNumberedGroupsList);

    ListValue<DictionaryValue> finalNamedGroupsList = new ListValue<>();
    finalNamedGroupsList.set(
        namedGroupsByMatchList); // This should be List<Value> which contains DictionaryValue
    returnDictionaryMap.put("NamedGroupsByMatch", finalNamedGroupsList);

    return new DictionaryValue(returnDictionaryMap);
  }

}
