import java.io.Writer;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import org.mundo.service.DebugService;

class LogTableModel extends AbstractTableModel
{
  LogTableModel(List<DebugService.LogEntry> l, int logLevel, String filter)
  {
    entries = new ArrayList<DebugService.LogEntry>();
    for (DebugService.LogEntry le : l)
    {
      if (le.level > logLevel)
        continue;
      if (filter.length()>0 && le.channel.indexOf(filter)<0 && le.text.indexOf(filter)<0)
        continue;
      entries.add(le);
    }
  }
  public int getRowCount()
  {
    return entries.size();
  }
  public int getColumnCount()
  {
    return 3;
  }
  public String getColumnName(int column)
  {
    switch (column)
    {
      case 0:
        return "L";
      case 1:
        return "Source";
      case 2:
        return "Text";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    DebugService.LogEntry le=entries.get(row);
    switch (column)
    {
      case 0:
        return le.severity;
      case 1:
        return le.channel;
      case 2:
        return le.text;
    }
    return null;
  }
  public void writeTo(Writer w)
  {
    PrintWriter pw=new PrintWriter(w);
    for (DebugService.LogEntry le : entries)
    {
      pw.println(le.severity+"|"+le.channel+"|"+le.text);
    }
  }
  private List<DebugService.LogEntry> entries;
}
