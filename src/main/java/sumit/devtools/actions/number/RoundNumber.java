package sumit.devtools.actions.number;

import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NumberInteger;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.math.RoundingMode;
import org.apache.commons.math3.util.Precision;

/**
 * @author Sumit Kumar
 */
@BotCommand
@CommandPkg(label = "Round number",
    description = "Rounds a number to a specified number of decimal places. " +
        "'Up' rounds towards positive infinity (ceiling), " +
        "'Down' rounds towards negative infinity (floor), and " +
        "'Normal' rounds to the nearest value (half away from zero for equidistant values).", // Clarified
    // normal rounding
    icon = "Number.svg",
    name = "roundNumber",
    group_label = "Number",
    node_label = "Round {{inputNumber}} {{roundingType}} to {{decimalPlaces}} places and assign to {{returnTo}}",
    return_description = "The number after applying the specified rounding.",
    return_required = true,
    return_label = "Assign rounded number to",
    return_type = DataType.NUMBER)
public class RoundNumber {

  @Execute
  public static NumberValue action(
      @Idx(index = "1", type = AttributeType.NUMBER)
      @Pkg(label = "Number to Round", description = "The input number that needs to be rounded.")
      @NotEmpty
      Double inputNumber,

      @Idx(index = "2", type = AttributeType.NUMBER)
      @Pkg(label = "Decimal Places", default_value = "0", default_value_type = DataType.NUMBER, description =
          "The number of digits to keep after the decimal point. Must be a non-negative integer.")
      @NotEmpty
      @NumberInteger // SDK should ensure this is an integer representation
      @GreaterThanEqualTo("0") // SDK should ensure this is >= 0
      Double decimalPlaces,

      @Idx(index = "3", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "3.1", pkg = @Pkg(label = "Normal (Round half away from zero)", value =
              "normal",
              description =
                  "Rounds to the nearest number. If equidistant, rounds half away from zero. " +
                      "Example: 1.235 normal to 2 places is 1.24; 1.234 normal to 2 places is 1.23; -1"
                      +
                      ".235 normal to 2 places is -1.24.")), // Corrected example for negative half
          @Idx.Option(index = "3.2", pkg = @Pkg(label = "Up (towards +infinity / Ceiling)", value = "up",
              description =
                  "Rounds towards positive infinity. Example: 1.236 up to 2 places is 1.24; " +
                      "-1.236 up to 2 places is -1.23; 1.230 up to 2 places is 1.23.")),
          @Idx.Option(index = "3.3", pkg = @Pkg(label = "Down (towards -infinity / Floor)", value = "down",
              description =
                  "Rounds towards negative infinity. Example: 1.236 down to 2 places is 1.23;" +
                      " -1.231 down to 2 places is -1.24; 1.239 down to 2 places is 1.23."))
      })
      @Pkg(label = "Rounding Type", default_value = "normal", default_value_type = DataType.STRING, description =
          "Specifies the rounding strategy.")
      @SelectModes
      @NotEmpty
      String roundingType
  ) {
    if (inputNumber == null) { // Explicit null check for robustness in testing
      throw new BotCommandException("Input number cannot be null.");
    }
    if (decimalPlaces == null) { // Explicit null check
      throw new BotCommandException("Decimal places cannot be null.");
    }
    try {
      int dp = decimalPlaces.intValue();
      if (dp < 0) {
        throw new BotCommandException(
            "Decimal places must be a non-negative integer. Received: " + dp);
      }

      int roundingModeInt = getRoundingMode(roundingType);

      double result = Precision.round(inputNumber, dp, roundingModeInt);
      return new NumberValue(result, true);
    } catch (Exception e) { // Catch other exceptions like those from Precision.round or .intValue()
      throw new BotCommandException(
          "Error occurred while rounding the number '" + inputNumber + "': " + e.getMessage(), e);
    }
  }

  private static int getRoundingMode(String roundingType) {
    if (roundingType == null) {
      throw new BotCommandException("Rounding type cannot be null.");
    }
    int roundingMode;

    if (roundingType.equalsIgnoreCase("normal")) {
      roundingMode = RoundingMode.HALF_UP.ordinal(); // This is 4, same as BigDecimal.ROUND_HALF_UP
    } else if (roundingType.equalsIgnoreCase("up")) {
      roundingMode = RoundingMode.CEILING.ordinal(); // Constant value is 2
    } else if (roundingType.equalsIgnoreCase("down")) {
      roundingMode = RoundingMode.FLOOR.ordinal();   // Constant value is 3
    } else {
      throw new BotCommandException(
          "Invalid rounding type specified: " + roundingType + ". Allowed types " +
              "are 'normal', 'up', or 'down'.");
    }
    return roundingMode;
  }

}