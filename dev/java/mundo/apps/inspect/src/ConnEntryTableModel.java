import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.JTableHeader;

import org.mundo.rt.Tracer;
import org.mundo.service.DebugService;

class ConnEntryTableModel extends AbstractTableModel
{
  ConnEntryTableModel(InspectWindow w, Collection<DebugService.ConnEntry> clt)
  {
    window=w;
    sortColumn=2;
    doSet(clt);
  }
  private boolean isIPAddress(String s)
  {
    char c;
    for (int i=0; i<s.length(); i++)
    {
      c=s.charAt(i);
      if (!(c>='0' && c<='9' || c=='.'))
        return false;
    }
    return true;
  }
  private void doSet(Collection<DebugService.ConnEntry> clt)
  {
    Tracer t=new Tracer("ConnEntryTableModel.doSet");
    t.start();
    entries=new ArrayList<DebugService.ConnEntry>(clt);
/*
    for (DebugService.ConnEntry ce : entries)
    {
      String ipString=null;
      if (ce.addr.charAt(0)=='/')
        ipString=ce.addr.substring(1);
      else if (isIPAddress(ce.addr))
        ipString=ce.addr;
      if (ipString!=null)
      {
        String s=window.getResolver().getHostName(ipString);
        if (s!=null)
          ce.addr=s+"/"+ipString;
      }
    }
*/    
    t.before("doSort");
    doSort();
    t.stop();
  }
  private void doSort()
  {
    Collections.sort(entries, comparators[sortColumn]);
  }
  public void sortBy(int col)
  {
    if (col<0 || col>2)
      return;
    sortColumn=col;
    doSort();
    fireTableDataChanged();
  }
  public void set(Collection<DebugService.ConnEntry> clt)
  {
    doSet(clt);
    fireTableDataChanged();
  }
  public int getRowCount()
  {
    return entries.size();
  }
  public int getColumnCount()
  {
    return 9;
  }
  public String getColumnName(int column)
  {
    switch (column)
    {
      case 0:
        return "Name";
      case 1:
        return "ID";
      case 2:
        return "LocalExtAddr";
      case 3:
        return "PeerExtAddr";
      case 4:
        return "PeerIntAddr";
      case 5:
        return "Proto";
      case 6:
        return "Metric";
      case 7:
        return "State";
      case 8:
        return "Timeout";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    DebugService.ConnEntry ce=entries.get(row);
    switch (column)
    {
      case 0:
        if (ce.remoteName!=null)
          return ce.remoteName;
        return "";
      case 1:
        return ce.remoteId.toString();
      case 2:
        return ce.localExtAddr;
      case 3:
        return ce.peerExtAddr;
      case 4:
        return ce.peerIntAddr;
      case 5:
        return ce.protocol;
      case 6:
        return Integer.toString(ce.metric);
      case 7:
        return getStateAsString(ce);
      case 8:
        return Integer.toString(ce.timeout);
    }
    return null;
  }
  public DebugService.ConnEntry getEntry(int row)
  {
    return entries.get(row);
  }
  public String getStateAsString(DebugService.ConnEntry ce)
  {
    return (ce.active ? "* " : "")+(ce.open ? "open" : "closed");
  }
  public void setTableHeader(JTableHeader hdr)
  {
    if (tableHeader!=null)
      tableHeader.removeMouseListener(mouseListener);
    tableHeader=hdr;
    if (tableHeader!=null)
      tableHeader.addMouseListener(mouseListener);
  }
  public JTableHeader getTableHeader()
  {
    return tableHeader;
  }
  
  private class MouseHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent ev)
    {
      JTableHeader h=(JTableHeader)ev.getSource();
      TableColumnModel columnModel=h.getColumnModel();
      int viewColumn=columnModel.getColumnIndexAtX(ev.getX());
      int modelColumn=columnModel.getColumn(viewColumn).getModelIndex();
      sortBy(modelColumn);
    }
  }

  private static final Comparator NAME_COMPARATOR=new Comparator<DebugService.ConnEntry>()
  {
    public int compare(DebugService.ConnEntry e1, DebugService.ConnEntry e2)
    {
      String s1=e1.remoteName;
      if (s1==null)
        s1="";
      String s2=e2.remoteName;
      if (s2==null)
        s2="";
      int v=s1.compareTo(s2);
      if (v==0 && e1.addr!=null && e2.addr!=null)
        return e1.addr.compareTo(e2.addr);
      return v;
    }
  };

  private static final Comparator ID_COMPARATOR=new Comparator<DebugService.ConnEntry>()
  {
    public int compare(DebugService.ConnEntry e1, DebugService.ConnEntry e2)
    {
      int v=e1.remoteId.compareTo(e2.remoteId);
      if (v==0 && e1.addr!=null && e2.addr!=null)
        return e1.addr.compareTo(e2.addr);
      return v;
    }
  };

  private static final Comparator HOST_COMPARATOR=new Comparator<DebugService.ConnEntry>()
  {
    public int compare(DebugService.ConnEntry e1, DebugService.ConnEntry e2)
    {
      if (e1.addr==null || e2.addr==null)
        return 0;
      int v=e1.addr.compareTo(e2.addr);
      if (v==0)
        return e1.remoteId.compareTo(e2.remoteId);
      return v;
    }
  };
  
  private static final Comparator comparators[]={ NAME_COMPARATOR, ID_COMPARATOR, HOST_COMPARATOR };
    
  private ArrayList<DebugService.ConnEntry> entries;
  private MouseHandler mouseListener=new MouseHandler();
  private JTableHeader tableHeader;
  private int sortColumn;
  private InspectWindow window;
}
