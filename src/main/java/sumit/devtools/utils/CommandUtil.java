package sumit.devtools.utils;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Sumit Kumar
 */
public final class CommandUtil {

  private static final Integer WAIT_BETWEEN_CONDITION_RETRY = 100;
  private static final Logger LOGGER = LogManager.getLogger(CommandUtil.class);

  public static boolean waitForCondition(Integer waitTimeOut, Map<String, Object> parameters,
      BotVerifiable condition) throws InterruptedException {
    LocalTime nowInUtc = LocalTime.now(Clock.systemUTC());
    LocalTime waitTillUtc = nowInUtc.plusSeconds((long) waitTimeOut);
    LOGGER.debug("timeOut:{}", waitTimeOut);

    do {
      if (condition.test(parameters)) {
        return true;
      }

      if (waitTimeOut > 0) {
        LOGGER.debug("Before Sleep");
        Thread.sleep((long) WAIT_BETWEEN_CONDITION_RETRY);
      }

      nowInUtc = LocalTime.now(Clock.systemUTC());
    } while (nowInUtc.isBefore(waitTillUtc));

    // Return false when condition is not met, regardless of timeout
    // This allows conditional actions to properly handle false cases
    return false;
  }

}
