import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.mundo.service.DebugService;

class ERSExportsTableModel extends AbstractTableModel
{
  ERSExportsTableModel(List<DebugService.ERSExportEntry> l)
  {
    entries=l;
  }
  public int getRowCount()
  {
    return entries.size();
  }
  public int getColumnCount()
  {
    return 2;
  }
  public String getColumnName(int column)
  {
    switch (column)
    {
      case 0:
        return "Channel";
      case 1:
        return "ReceiverID";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    DebugService.ERSExportEntry ee=entries.get(row);
    switch (column)
    {
      case 0:
        return ee.channelName;
      case 1:
        return ee.receiverId.toString();
    }
    return null;
  }
  private List<DebugService.ERSExportEntry> entries;
}
