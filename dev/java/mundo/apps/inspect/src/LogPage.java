import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumnModel;

import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.Logger;
import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;

class LogPage extends Page implements AsyncCall.IResultListener
{
  LogPage(DoDebugService ds)
  {
    doDebugService = ds;
    logLevel = Logger.FINEST;
  }
  @Override
  String getURI()
  {
    return "log:"+doDebugService._getPublisher().getChannel().getName();
  }
  @Override
  void load()
  {
    AsyncCall ac=doDebugService.getLogMessages(DoObject.CREATEONLY);
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
      log = (List<DebugService.LogEntry>)c.getObj();
      if (log==null)
        throw new NullPointerException();

      JPanel leftPane = new JPanel();
      leftPane.add(new JLabel("Level:"));
      levelCombo = new JComboBox(new String[] {
          "1 - SEVERE",
          "2 - WARNING",
          "3 - INFO",
          "4 - CONFIG",
          "5 - FINE",
          "6 - FINER",
          "7 - FINEST"
      });
      levelCombo.setSelectedIndex(logLevel-1);
      levelCombo.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          logLevel = levelCombo.getSelectedIndex()+1;
          tableModel = new LogTableModel(log, logLevel, filterField.getText());
          table.setModel(tableModel);
          adjustColumns();
        }
      });
      leftPane.add(levelCombo);
      
      JPanel rightPane = new JPanel();
      rightPane.add(new JLabel("Filter:"));
      rightPane.add(filterField = new JTextField(10));
      filterField.getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent ev) {
        }
        public void insertUpdate(DocumentEvent ev) {
          tableModel = new LogTableModel(log, logLevel, filterField.getText());
          table.setModel(tableModel);
          adjustColumns();
        }
        public void removeUpdate(DocumentEvent ev) {
          insertUpdate(ev);
        }
      });
      
      JPanel topPane = new JPanel();
      topPane.setLayout(new BorderLayout());
      topPane.add(leftPane, BorderLayout.WEST);
      topPane.add(rightPane, BorderLayout.EAST);
      
      tableModel = new LogTableModel(log, logLevel, "");
      table = new JTable(tableModel);
      adjustColumns();
      
      JPanel pane = new JPanel();
      pane.setLayout(new BorderLayout());
      pane.add(topPane, BorderLayout.NORTH);
      pane.add(new JScrollPane(table), BorderLayout.CENTER);
      window.showComponent(pane);
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }
  private void adjustColumns()
  {
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setMaxWidth(40);
    cm.getColumn(0).setPreferredWidth(20);
    cm.getColumn(1).setMaxWidth(100);
    cm.getColumn(1).setPreferredWidth(60);
    cm.getColumn(2).setPreferredWidth(400);
  }
  
  private DoDebugService doDebugService;
  private LogTableModel tableModel;
  private List<DebugService.LogEntry> log;
  private JTable table;
  private JComboBox levelCombo;
  private JTextField filterField;
  private int logLevel;
}
