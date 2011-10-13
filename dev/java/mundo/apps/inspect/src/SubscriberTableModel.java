import java.util.Collection;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;

import org.mundo.service.DebugService;

class SubscriberTableModel extends AbstractTableModel
{
  SubscriberTableModel(Collection<DebugService.SubscriberEntry> clt)
  {
    int i=0;
    entries=new DebugService.SubscriberEntry[clt.size()];
    Iterator<DebugService.SubscriberEntry> iter=clt.iterator();
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
        return "receiver";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    DebugService.SubscriberEntry se=entries[row];
    switch (column)
    {
      case 0:
        return se.channelName;
      case 1:
        return se.zoneName;
      case 2:
        return se.interfaceName;
    }
    return null;
  }
  private DebugService.SubscriberEntry entries[];
}

