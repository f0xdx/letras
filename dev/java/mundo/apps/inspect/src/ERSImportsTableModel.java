import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.mundo.service.DebugService;

class ERSImportsTableModel extends AbstractTableModel
{
  ERSImportsTableModel(List<DebugService.ERSImportEntry> l)
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
        return "Zone";
      case 1:
        return "Channel";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    DebugService.ERSImportEntry ie=entries.get(row);
    switch (column)
    {
      case 0:
        return ie.zoneName;
      case 1:
        return ie.channelName;
    }
    return null;
  }
  private List<DebugService.ERSImportEntry> entries;
}
