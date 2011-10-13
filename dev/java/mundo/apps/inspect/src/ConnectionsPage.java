import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.RMCException;
import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;

class ConnectionsPage extends Page implements AsyncCall.IResultListener
{
  ConnectionsPage(DoDebugService ds)
  {
    doDebugService=ds;
	reloadToRefresh=true;
  }
  @Override
  String getURI()
  {
    return "connections:"+doDebugService._getPublisher().getChannel().getName();
  }
  @Override
  void load()
  {
    AsyncCall ac=doDebugService.getConnections(DoObject.CREATEONLY);
    ac.setResultListener(this);
    ac.invoke();
  }
  public void resultReceived(AsyncCall c)
  {
    window.pageLoaded(this);
    if (c.getException()!=null)
    {
      window.showException(c.getException());
      return;
    }
    try
    {
      List<DebugService.ConnEntry> connections=(List<DebugService.ConnEntry>)c.getObj();
      if (tableModel==null)
      {
        tableModel=new ConnEntryTableModel(window, connections);
        table=new JTable(tableModel);
        tableModel.setTableHeader(table.getTableHeader());
        table.addMouseListener(new ConnectionsMouseListener(this));
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent ev) {
            if (!ev.getValueIsAdjusting())
            {
              boolean b = !table.getSelectionModel().isSelectionEmpty();
              openButton.setEnabled(b);
            }
          }
        });

        JPanel buttonPane=new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.add(openButton=new JButton("Open"));
        openButton.setEnabled(false);
        openButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
            openNode();
          }
        });

        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(new JScrollPane(table), BorderLayout.CENTER);
        pane.add(buttonPane, BorderLayout.SOUTH);

        window.showComponent(pane);
      }
      else
      {
        int sel=table.getSelectedRow();
        tableModel.set(connections);
        if (sel>=0 && sel<table.getRowCount())
          table.setRowSelectionInterval(sel, sel);
      }
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }

  void openNode()
  {
    try
    {
      DebugService.ConnEntry ce = tableModel.getEntry(table.getSelectedRow());
      window.open(new NodePage(window.addNode(ce.remoteId).debugService));
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  void blockSends()
  {
    try
    {
      DebugService.ConnEntry ce=tableModel.getEntry(table.getSelectedRow());
      doDebugService.crippleBlockSendsTo(ce.remoteId);
    }
    catch(RMCException x)
    {
      x.printStackTrace();
    }
  }

  private DoDebugService doDebugService;
  private ConnEntryTableModel tableModel;
  JTable table;
  private JButton openButton;
}
