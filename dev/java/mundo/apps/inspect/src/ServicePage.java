import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.GUID;
import org.mundo.rt.Publisher;
import org.mundo.rt.Signal;
import org.mundo.service.DoIServiceManager;
import org.mundo.service.DebugService;
import org.mundo.service.DoDebugService;

class ServicePage extends Page implements AsyncCall.IResultListener
{
  ServicePage(DoDebugService ds, DoIServiceManager sm, GUID sid)
  {
    doDebugService=ds;
    doServiceManager=sm;
    serviceId=sid;
  }
  @Override
  String getURI()
  {
    return "service:"+doDebugService._getPublisher().getChannel().getName()+"/"+serviceId;
  }
  @Override
  void load()
  {
    AsyncCall ac=doDebugService.getServiceInfo(serviceId, DoObject.CREATEONLY);
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
      si=(DebugService.ServiceEntry)c.getObj();

      JPanel panel=new JPanel();
      panel.setLayout(new SpringLayout());

      JLabel label=new JLabel("Service Information", JLabel.LEFT);
      label.setBorder(new EmptyBorder(4, 0, 2, 0));
      panel.add(label);
      panel.add(new JSeparator());
      JPanel infoPanel=new JPanel();
      infoPanel.setBorder(new EmptyBorder(0, 0, 4, 0));
      infoPanel.setLayout(new SpringLayout());
      infoPanel.add(new JLabel("guid:"));
      infoPanel.add(new JLabel(si.guid.toString()));
      infoPanel.add(new JLabel("className:"));
      infoPanel.add(new JLabel(si.className));
      infoPanel.add(new JLabel("instanceName:"));
      infoPanel.add(new JLabel(si.instanceName));
      SpringUtilities.makeCompactGrid(infoPanel, 3, 2, 0, 0, 4, 0);
      panel.add(infoPanel);

      panel.add(new JSeparator());
      JPanel buttonPane=new JPanel();
      label.setBorder(new EmptyBorder(4, 0, 4, 0));
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
      JButton button;
      buttonPane.add(button=new JButton("Configure"));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          try
          {
            window.open(new ServiceConfigPage(doServiceManager, serviceId), ev.getModifiers());
          }
          catch(Exception x)
          {
            x.printStackTrace();
          }
        }
      });
      buttonPane.add(button=new JButton("Shutdown"));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          try
          {
            AsyncCall call=doServiceManager.shutdownService(serviceId, DoObject.CREATEONLY);
            call.setResultListener(new AsyncCall.IResultListener() {
              public void resultReceived(AsyncCall c)
              {
                window.open(new ServiceInstancesPage(doDebugService, doServiceManager));
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
      panel.add(buttonPane);

      label=new JLabel("Ports", JLabel.LEFT);
      label.setBorder(new EmptyBorder(8, 0, 2, 0));
      panel.add(label);
      panel.add(new JSeparator());

      label=new JLabel("Subscribers", JLabel.LEFT);
      label.setBorder(new EmptyBorder(8, 0, 2, 0));
      panel.add(label);
      panel.add(new JSeparator());
      if (si.subscribers!=null)
      {
        subTable=new JTable(new SubscriberTableModel(si.subscribers));
//        subTable.addMouseListener(new SubscriberTableMouseListener(mainWnd, subTable, si.subscribers));
        panel.add(subTable.getTableHeader());
        panel.add(subTable);
        subTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent ev) {
            if (!ev.getValueIsAdjusting() && !subTable.getSelectionModel().isSelectionEmpty())
            {
              pubTable.clearSelection();
              openButton.setEnabled(true);
              queryButton.setEnabled(true);
            }
          }
        });
      }
      else
        panel.add(new JLabel("subscribers==null"));

      label=new JLabel("Publishers", JLabel.LEFT);
      label.setBorder(new EmptyBorder(8, 0, 2, 0));
      panel.add(label);
      panel.add(new JSeparator());
      if (si.publishers!=null)
      {
        pubTable=new JTable(new PublisherTableModel(si.publishers));
//        pubTable.addMouseListener(new PublisherTableMouseListener(mainWnd, pubTable, si.publishers));
        panel.add(pubTable.getTableHeader());
        panel.add(pubTable);
        pubTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent ev) {
            if (!ev.getValueIsAdjusting() && !pubTable.getSelectionModel().isSelectionEmpty())
            {
              subTable.clearSelection();
              openButton.setEnabled(true);
              queryButton.setEnabled(true);
            }
          }
        });
      }
      else
        panel.add(new JLabel("publishers==null"));

      buttonPane=new JPanel();
      label.setBorder(new EmptyBorder(4, 0, 4, 0));
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
      buttonPane.add(openButton=new JButton("Open Channel"));
      openButton.setEnabled(false);
      openButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          openChannel(ev.getModifiers());
        }
      });
      buttonPane.add(queryButton=new JButton("Query Interface"));
      queryButton.setEnabled(false);
      queryButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          queryInterface(ev.getModifiers());
        }
      });
      panel.add(buttonPane);

      SpringUtilities.makeCompactGrid(panel, panel.getComponentCount(), 1, 4, 4, 0, 0);

      JPanel view=new JPanel();
      view.setLayout(new BorderLayout());
      view.add(panel, BorderLayout.NORTH);
 
      JPanel mainPane=new JPanel();
      mainPane.setLayout(new BorderLayout());
      mainPane.add(new JScrollPane(view), BorderLayout.CENTER);

      window.showComponent(mainPane);
    }
    catch(Exception x)
    {
      window.showException(x);
    }
  }

  private PortInfo getSelectedChannel()
  {
    int i=pubTable.getSelectedRow();
    if (i>=0)
    {
      DebugService.PublisherEntry pe=si.publishers.get(i);
      return new PortInfo(pe.channelName, pe.interfaceName);
    }
    i=subTable.getSelectedRow();
    if (i>=0)
    {
      DebugService.SubscriberEntry se=si.subscribers.get(i);
      return new PortInfo(se.channelName, se.interfaceName);
    }
    return null;
  }
  private void openChannel(int mods)
  {
    PortInfo port=getSelectedChannel();
    if (port==null)
      return;
    window.open(new ChannelPage(port.channelName), mods);
  }
  private void queryInterface(int mods)
  {
    PortInfo port=getSelectedChannel();
    if (port==null)
      return;
    window.open(new RMCPage(port.channelName, port.interfaceName), mods);
  }

  private class PortInfo
  {
    PortInfo(String cn, String in)
    {
      channelName=cn;
      interfaceName=in;
    }
    String channelName;
    String interfaceName;
  }
  
  private DoDebugService doDebugService;
  private DoIServiceManager doServiceManager;
  private GUID serviceId;
  private JTable pubTable;
  private JTable subTable;
  private JButton openButton;
  private JButton queryButton;
  private DebugService.ServiceEntry si;
}
