import java.util.Collection;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;

import org.mundo.service.DebugService;

class PublisherTableModel extends AbstractTableModel
{
  PublisherTableModel(Collection<DebugService.PublisherEntry> clt)
  {
    int i=0;
    entries=new DebugService.PublisherEntry[clt.size()];
    Iterator<DebugService.PublisherEntry> iter=clt.iterator();
    while (iter.hasNext())
      entries[i++]=iter.next();
  }
  public int getRowCount()
  {
    return entries.length;
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
        return "channel";
      case 1:
        return "zone";
      case 2:
        return "sender";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    DebugService.PublisherEntry pe=entries[row];
    switch (column)
    {
      case 0:
        return pe.channelName;
      case 1:
        return pe.zoneName;
      case 2:
        return pe.interfaceName;
    }
    return null;
  }
  private DebugService.PublisherEntry entries[];
}

