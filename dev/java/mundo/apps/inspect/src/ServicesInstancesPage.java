import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.GUID;
import org.mundo.service.DoDebugService;
import org.mundo.service.ServiceInstance;
import org.mundo.service.ServiceInfo;
import org.mundo.service.DoIServiceManager;

class ServiceInstancesPage extends Page implements AsyncCall.IResultListener
{
  ServiceInstancesPage(DoDebugService ds, DoIServiceManager sm)
  {
    doDebugService=ds;
    doServiceManager=sm;
  }
  @Override
  String getURI()
  {
    return "service-instances:"+doServiceManager._getPublisher().getChannel().getName();
  }
  @Override
  void load()
  {
    AsyncCall ac=doServiceManager.getServiceInstances(DoObject.CREATEONLY);
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

      TableModel tableModel;
      List l = (List)c.getObj();
      if (l.size()>0 && l.get(0) instanceof ServiceInstance)
      {
        instances = (List<ServiceInstance>)l;
        tableModel = new ServiceInstanceTableModel(instances, doServiceManager);
      }
      else
      {
        infos = (List<ServiceInfo>)l;
        tableModel = new ServiceInfoTableModel(infos, doServiceManager);
      }
      table = new JTable(tableModel);
//      table.addMouseListener(new ServiceInstanceMouseListener(table, tableModel));
      pane.add(new JScrollPane(table));

      JPanel buttonPane=new JPanel();
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
      
      buttonPane.add(openButton=new JButton("Open"));
      openButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          openInstance(ev.getModifiers());
        }
      });
      buttonPane.add(shutdownButton=new JButton("Shutdown"));
      shutdownButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          try
          {
            AsyncCall call = doServiceManager.shutdownService(
                getSelectedId(), DoObject.CREATEONLY);
            call.setResultListener(new AsyncCall.IResultListener() {
              public void resultReceived(AsyncCall c)
              {
                window.open(ServiceInstancesPage.this);
              }
            });
            call.invoke();
          }
          catch(Exception x)
          {
            x.printStackTrace();
          }
        }
      });
      pane.add(buttonPane);

      SpringUtilities.makeCompactGrid(pane, pane.getComponentCount(), 1, 4, 4, 0, 0);

      openButton.setEnabled(false);
      shutdownButton.setEnabled(false);
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent ev) {
          if (!ev.getValueIsAdjusting())
          {
            boolean b=!table.getSelectionModel().isSelectionEmpty();
            openButton.setEnabled(b);
            shutdownButton.setEnabled(b);
          }
        }
      });
      
      table.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent ev)
        {
          if (ev.getButton()==MouseEvent.BUTTON1 && ev.getClickCount()==2)
            openInstance(ev.getModifiers());
        }
      });

      window.showComponent(pane);
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }

  private GUID getSelectedId()
  {
    GUID id = null;
    if (instances != null)
      id = instances.get(table.getSelectedRow()).guid;
    if (infos != null)
      id = infos.get(table.getSelectedRow()).guid;
    if (id == null)
      throw new IllegalStateException("can't determine GUID of service");
    return id;
  }
  
  private void openInstance(int mod)
  {
    try
    {
      window.open(new ServicePage(doDebugService, doServiceManager, getSelectedId()), mod);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  private DoIServiceManager doServiceManager;
  private DoDebugService doDebugService;
  private List<ServiceInstance> instances;
  private List<ServiceInfo> infos;
  private JTable table;
  private JButton openButton;
  private JButton shutdownButton;
}
