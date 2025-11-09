package sumit.devtools.triggers;

import static com.automationanywhere.commandsdk.model.DataType.RECORD;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DateTimeValue;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.StartListen;
import com.automationanywhere.commandsdk.annotations.StopAllTriggers;
import com.automationanywhere.commandsdk.annotations.StopListen;
import com.automationanywhere.commandsdk.annotations.TriggerConsumer;
import com.automationanywhere.commandsdk.annotations.TriggerId;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThanEqualTo;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.SelectModes;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "Time trigger", description = "Time based trigger", icon = "Calendar.svg", name = "datetimeTrigger",
    group_label = "Datetime",
    node_label = "at {{triggerOption}} | {{inputDate}} || {{secondsFromNow}} seconds from now|",
    return_type = RECORD, return_name = "TriggerData", return_description = "Available keys: triggerTime")
public class DatetimeTrigger {

  // Map storing multiple tasks
  private static final Logger LOGGER = LogManager.getLogger(DatetimeTrigger.class);
  private static Map<String, ScheduledFuture> taskMap = new ConcurrentHashMap<>();
  private static ScheduledExecutorService scheduledExecutorService = null;

  @TriggerId
  private String triggerUid;
  @TriggerConsumer
  private Consumer consumer;

  /*
   * Starts the trigger.
   */
  @StartListen
  public void startTrigger(
      @Idx(index = "1", type = AttributeType.SELECT, options = {
          @Idx.Option(index = "1.1", pkg = @Pkg(label = "Exact Datetime", value = "datetime")),
          @Idx.Option(index = "1.2", pkg = @Pkg(label = "Relative time", value = "relative")),
      })
      @Pkg(label = "Trigger options", default_value = "datetime", default_value_type = STRING)
      @NotEmpty
      @SelectModes
      String triggerOption,

      @Idx(index = "1.1.1", type = AttributeType.DATETIME)
      @Pkg(label = "Datetime to trigger at", default_value_type = DataType.DATETIME)
      @NotEmpty
      ZonedDateTime inputDate,

      @Idx(index = "1.2.1", type = AttributeType.NUMBER)
      @Pkg(label = "Seconds from now", default_value_type = DataType.DATETIME)
      @NotEmpty
      @GreaterThanEqualTo("0")
      Double secondsFromNow

  ) {
    long duration;
    if (triggerOption.equalsIgnoreCase("datetime")) {
      duration = Duration.between(ZonedDateTime.now(), inputDate).toMillis();
    } else {
      duration = (long) (secondsFromNow * 1000L);
    }
    scheduledExecutorService = this.getScheduledExecutorService();
    ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(
        () -> consumer.accept(getRecordValue()),
        duration,
        java.util.concurrent.TimeUnit.MILLISECONDS);
    taskMap.put(this.triggerUid, scheduledFuture);
  }

  public ScheduledExecutorService getScheduledExecutorService() {
    try {
      if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()
          || scheduledExecutorService.isTerminated()) {
        return Executors.newScheduledThreadPool(4);
      }
    } catch (Throwable var2) {
      LOGGER.error("Executor service for Datetime trigger failed to initialize");
    }

    return scheduledExecutorService;
  }

  private RecordValue getRecordValue() {
    List<Schema> schemas = new ArrayList<>();
    List<Value> values = new ArrayList<>();
    schemas.add(
        new Schema("triggerTime", com.automationanywhere.botcore.api.dto.AttributeType.DATETIME));
    values.add(new DateTimeValue(ZonedDateTime.now()));

    RecordValue recordValue = new RecordValue();
    recordValue.set(new Record(schemas, values));
    return recordValue;
  }

  /*
   * Cancel all the task and clear the map.
   */
  @StopAllTriggers
  public void stopAllTriggers() {
    LOGGER.info("StopTrigger Execution started");
    try {
      taskMap.forEach((k, task) -> {
        LOGGER.info("StopTrigger Execution started for triggerUid: {}", k);
        if (task.cancel(true)) {
          taskMap.remove(k);
        }
      });
    } finally {
      scheduledExecutorService.shutdownNow();
      taskMap = new ConcurrentHashMap<>();
    }


  }

  /*
   * Cancel the task and remove from map
   *
   * @param triggerUid
   */
  @StopListen
  public void stopListen(String triggerUid) {
    LOGGER.info("stopListen execution started for triggerUid: {}", triggerUid);
    if (taskMap.get(triggerUid).cancel(true)) {
      taskMap.remove(triggerUid);
    }
  }

  public String getTriggerUid() {
    return triggerUid;
  }

  public void setTriggerUid(String triggerUid) {
    this.triggerUid = triggerUid;
  }

  public Consumer getConsumer() {
    return consumer;
  }

  public void setConsumer(Consumer consumer) {
    this.consumer = consumer;
  }

  public void setTaskMap(Map<String, ScheduledFuture> taskMap) {
    DatetimeTrigger.taskMap = taskMap;
  }

}