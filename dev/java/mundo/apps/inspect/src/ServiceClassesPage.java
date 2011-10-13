import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import org.mundo.service.DoDebugService;
import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.service.ServiceClass;
import org.mundo.service.DoIServiceManager;

class ServiceClassesPage extends Page implements AsyncCall.IResultListener
{
  ServiceClassesPage(DoDebugService ds, DoIServiceManager sm)
  {
    doDebugService=ds;
    doIServiceManager=sm;
  }
  @Override
  String getURI()
  {
    return "service-classes:"+doIServiceManager._getPublisher().getChannel().getName();
  }
  @Override
  void load()
  {
    AsyncCall ac=doIServiceManager.getServiceClasses(DoObject.CREATEONLY);
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
      JPanel pane=new JPanel();
      pane.setLayout(new SpringLayout());

      serviceClasses=(List<ServiceClass>)c.getObj();
      ServiceClassTableModel tableModel=new ServiceClassTableModel(serviceClasses, doIServiceManager);
      table=new JTable(tableModel);
      pane.add(new JScrollPane(table));

      JPanel buttonPane=new JPanel();
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
      buttonPane.add(newButton=new JButton("New Instance"));
      newButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          window.open(new NewInstancePage(doDebugService, doIServiceManager,
                      serviceClasses.get(table.getSelectedRow()).className));
        }
      });

      buttonPane.add(deleteButton=new JButton("Delete JAR"));
      deleteButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          try
          {
            doIServiceManager.deleteFile(serviceClasses.get(table.getSelectedRow()).pluginName);
          }
          catch(Exception x)
          {
            x.printStackTrace();
          }
        }
      });
      pane.add(buttonPane);

      SpringUtilities.makeCompactGrid(pane, pane.getComponentCount(), 1, 4, 4, 0, 0);
      
      newButton.setEnabled(false);
      deleteButton.setEnabled(false);
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent ev) {
          if (!ev.getValueIsAdjusting())
          {
            boolean b=!table.getSelectionModel().isSelectionEmpty();
            newButton.setEnabled(b);
            deleteButton.setEnabled(b);
          }
        }
      });

      pane.setTransferHandler(new DeployTransferHandler(doIServiceManager));
      window.showComponent(pane);
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }

  private JTable table;
  private List<ServiceClass> serviceClasses;
  private DoDebugService doDebugService;
  private DoIServiceManager doIServiceManager;
  private JButton newButton;
  private JButton deleteButton;
}
