import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;

class ERSExportsPage extends Page implements AsyncCall.IResultListener
{
  ERSExportsPage(DoDebugService ds)
  {
    doDebugService=ds;
  }
  @Override
  String getURI()
  {
    return "ers-exports:"+doDebugService._getPublisher().getChannel().getName();
  }
  @Override
  void load()
  {
    AsyncCall ac=doDebugService.getERSExports(DoObject.CREATEONLY);
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
      exports = (List<DebugService.ERSExportEntry>)c.getObj();
      if (exports==null)
        throw new NullPointerException();
      TableModel tableModel=new ERSExportsTableModel(exports);
      table = new JTable(tableModel);

      table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent ev) {
          if (!ev.getValueIsAdjusting())
          {
            boolean b = !table.getSelectionModel().isSelectionEmpty();
            openButton.setEnabled(b);
          }
        }
      });

      table.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent ev)
        {
          if (ev.getButton()==MouseEvent.BUTTON1 && ev.getClickCount()==2)
            openChannel(ev.getModifiers());
        }
      });

      JPanel buttonPane=new JPanel();
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
      buttonPane.add(openButton = new JButton("Open Channel"));
      openButton.setEnabled(false);
      openButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          openChannel(ev.getModifiers());
        }
      });
      
      JPanel pane = new JPanel();
      pane.setLayout(new BorderLayout());
      pane.add(new JScrollPane(table), BorderLayout.CENTER);
      pane.add(buttonPane, BorderLayout.SOUTH);
      window.showComponent(pane);
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }

  private void openChannel(int mod)
  {
    window.open(new ChannelPage(exports.get(table.getSelectedRow()).channelName), mod);
  }

  private DoDebugService doDebugService;
  private List<DebugService.ERSExportEntry> exports;
  private JTable table;
  private JButton openButton;
}
