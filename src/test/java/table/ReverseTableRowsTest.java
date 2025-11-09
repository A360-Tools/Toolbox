package table;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sumit.devtools.actions.table.ReverseTableRows;

/**
 * @author Test Author
 */
public class ReverseTableRowsTest {

  private Table inputTable;

  @BeforeMethod
  public void setUp() {
    // Create a sample table for testing
    inputTable = new Table();

    // Create schema
    List<Schema> schema = new ArrayList<>();
    schema.add(new Schema("ID"));
    schema.add(new Schema("Name"));
    inputTable.setSchema(schema);

    // Create rows
    List<Row> rows = new ArrayList<>();

    // Row 1
    List<Value> row1Values = new ArrayList<>();
    row1Values.add(new StringValue("1"));
    row1Values.add(new StringValue("First"));
    rows.add(new Row(row1Values));

    // Row 2
    List<Value> row2Values = new ArrayList<>();
    row2Values.add(new StringValue("2"));
    row2Values.add(new StringValue("Second"));
    rows.add(new Row(row2Values));

    // Row 3
    List<Value> row3Values = new ArrayList<>();
    row3Values.add(new StringValue("3"));
    row3Values.add(new StringValue("Third"));
    rows.add(new Row(row3Values));

    // Row 4
    List<Value> row4Values = new ArrayList<>();
    row4Values.add(new StringValue("4"));
    row4Values.add(new StringValue("Fourth"));
    rows.add(new Row(row4Values));

    inputTable.setRows(rows);
  }

  @Test
  public void testReverseTableRows() {
    // Reverse the order of rows
    TableValue result = ReverseTableRows.action(
        null, // help parameter not used
        inputTable
    );

    Table resultTable = result.get();

    // Verify the result
    Assert.assertEquals(resultTable.getRows().size(), 4);

    // Verify that the rows are in reverse order
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "4");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "Fourth");

    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "3");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(1).toString(), "Third");

    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(0).toString(), "2");
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(1).toString(), "Second");

    Assert.assertEquals(resultTable.getRows().get(3).getValues().get(0).toString(), "1");
    Assert.assertEquals(resultTable.getRows().get(3).getValues().get(1).toString(), "First");
  }

  @Test
  public void testReverseTableRowsWithOneRow() {
    // Create a table with only one row
    Table oneRowTable = new Table();
    oneRowTable.setSchema(inputTable.getSchema());

    List<Row> rows = new ArrayList<>();
    List<Value> rowValues = new ArrayList<>();
    rowValues.add(new StringValue("1"));
    rowValues.add(new StringValue("Single"));
    rows.add(new Row(rowValues));
    oneRowTable.setRows(rows);

    // Reverse the order of rows
    TableValue result = ReverseTableRows.action(
        null,
        oneRowTable
    );

    Table resultTable = result.get();

    // Verify the result
    Assert.assertEquals(resultTable.getRows().size(), 1);
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "1");
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(1).toString(), "Single");
  }

  @Test
  public void testReverseEmptyTable() {
    // Create an empty table (no rows)
    Table emptyTable = new Table();
    emptyTable.setSchema(inputTable.getSchema());
    emptyTable.setRows(new ArrayList<>());

    // Reverse the order of rows
    TableValue result = ReverseTableRows.action(
        null,
        emptyTable
    );

    // Verify the result
    Assert.assertTrue(result.get().getRows().isEmpty());
  }

  @Test
  public void testPreservesOriginalTable() {
    // Get the original order of rows
    List<String> originalOrder = new ArrayList<>();
    for (Row row : inputTable.getRows()) {
      originalOrder.add(row.getValues().get(0).toString());
    }

    // Reverse the order of rows
    ReverseTableRows.action(
        null,
        inputTable
    );

    // Verify the original table hasn't changed
    for (int i = 0; i < inputTable.getRows().size(); i++) {
      Assert.assertEquals(inputTable.getRows().get(i).getValues().get(0).toString(),
          originalOrder.get(i));
    }
  }

  @Test
  public void testReverseAndReverseAgain() {
    // Reverse the order of rows
    TableValue firstResult = ReverseTableRows.action(
        null,
        inputTable
    );

    // Reverse the order of rows again
    TableValue secondResult = ReverseTableRows.action(
        null,
        firstResult.get()
    );

    Table resultTable = secondResult.get();

    // Verify the result (should be back in the original order)
    Assert.assertEquals(resultTable.getRows().get(0).getValues().get(0).toString(), "1");
    Assert.assertEquals(resultTable.getRows().get(1).getValues().get(0).toString(), "2");
    Assert.assertEquals(resultTable.getRows().get(2).getValues().get(0).toString(), "3");
    Assert.assertEquals(resultTable.getRows().get(3).getValues().get(0).toString(), "4");
  }

  @Test(expectedExceptions = BotCommandException.class)
  public void testWithNullTable() {
    // Try to reverse a null table
    ReverseTableRows.action(
        null,
        null
    );
  }

}