import java.util.List;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.filter.IFilter;
import org.mundo.filter.ActiveMapFilter;
import org.mundo.service.ServiceManager;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ResultSet;
import org.mundo.service.IME;
import org.mundo.service.DoIME;
import org.mundo.service.DoIUS;

class MEService extends Service implements IME
{
  MEService(String name)
  {
    setServiceInstanceName(name);
    setServiceZone("mine:"+name);
  }
  public String getName() // IME
  {
    return getServiceInstanceName();
  }
  public String getZoneName() // IME
  {
    return getServiceZone();
  }
}

class MEWindow extends JFrame
{
  MEWindow(int x, int y)
  {
    setLocation(x, y);
    setSize(400, 300);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        dispose();
        Mundo.shutdown();
      }
    });
    
    JPanel p1 = new JPanel();
    p1.setLayout(new BorderLayout());
    p1.add(new JLabel("USes:"), BorderLayout.NORTH);
    usListModel = new DefaultListModel();
    usListBox = new JList(usListModel);
    p1.add(usListBox, BorderLayout.CENTER);
    
    JPanel p3 = new JPanel();
    p3.setLayout(new FlowLayout());
    JButton btn = new JButton("Associate");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        cmdAssociate();
      }
    });
    p3.add(btn);
    btn = new JButton("Deassociate");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        cmdDeassociate();
      }
    });
    p3.add(btn);
    btn = new JButton("Refresh");
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        cmdRefresh();
      }
    });
    p3.add(btn);
    p1.add(p3, BorderLayout.SOUTH);
    
    JPanel p2 = new JPanel();
    p2.setLayout(new BorderLayout());
    p2.add(new JLabel("Services:"), BorderLayout.NORTH);
    svcListModel = new DefaultListModel();
    svcListBox = new JList(svcListModel);
    p2.add(svcListBox, BorderLayout.CENTER);
    setContentPane(p2);

    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    sp.setResizeWeight(0.5);
    sp.add(p1);
    sp.add(p2);
    setContentPane(sp);    
  }
  
  private void cmdDeassociate()
  {
    ServiceInfo si = (ServiceInfo)usListBox.getSelectedValue();
    if (si==null)
      return;
    DoIUS us = new DoIUS(si.doService);
    us.deassociate(DoIME._of(svc));
  }
  
  private void cmdAssociate()
  {
    ServiceInfo si = (ServiceInfo)usListBox.getSelectedValue();
    if (si==null)
      return;
    DoIUS us = new DoIUS(si.doService);
    System.out.println("associated US: " + us);
    us.associate(DoIME._of(svc));
  }
  
  private void cmdRefresh()
  {
    try
    {
      ServiceManager.getInstance().query(new ServiceInfoFilter(), null);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  public void setService(MEService s)
  {
    svc = s;
    setTitle("ME: "+svc.getName());

    try
    {
      ServiceInfoFilter filter = new ServiceInfoFilter();
      filter.filterInterface("org.mundo.service.IUS");
      ServiceManager.getInstance().contQuery(filter, svc.getSession(), US_UPDATE_HANDLER);
      
      filter = new ServiceInfoFilter();
      filter.filterInterface("IIODevice");
      filter.zone = s.getZoneName();
      filter._op_zone = IFilter.OP_EQUAL;
      ServiceManager.getInstance().contQuery(filter, svc.getSession(), SVC_UPDATE_HANDLER);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  private final ResultSet.ISignal US_UPDATE_HANDLER = new ResultSet.SignalAdapter()
  {
    @Override
    public void inserted(ResultSet rs, int i, int n)
    {
      updated(rs);
    }
    @Override
    public void removed(ResultSet rs, int i, int n)
    {
      updated(rs);
    }
    @Override
    public void propChanged(ResultSet rs, int i)
    {
      updated(rs);
    }
    public void updated(final ResultSet rs)
    {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          usListModel.removeAllElements();
          for (ServiceInfo si : (List<ServiceInfo>)rs.getList())
            usListModel.addElement(si);
        }
      });
    }
  };

  private final ResultSet.ISignal SVC_UPDATE_HANDLER = new ResultSet.SignalAdapter()
  {
    @Override
    public void inserted(ResultSet rs, int i, int n)
    {
      updated(rs);
    }
    @Override
    public void removed(ResultSet rs, int i, int n)
    {
      updated(rs);
    }
    @Override
    public void propChanged(ResultSet rs, int i)
    {
      updated(rs);
    }
    public void updated(final ResultSet rs)
    {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          svcListModel.removeAllElements();
          for (ServiceInfo si : (List<ServiceInfo>)rs.getList())
            svcListModel.addElement(si);
        }
      });
    }
  };
  
  private MEService svc;
  private DefaultListModel usListModel;
  private JList usListBox;
  private DefaultListModel svcListModel;
  private JList svcListBox;
}

public class TestME extends Service
{
  public static void main(String args[])
  {
    String name = "me";
    int x=0, y=0;
    for (int i=0; i<args.length; i++)
    {
      if (args[i].equals("-name"))
        name = args[++i];
      else if (args[i].equals("-x"))
        x = Integer.parseInt(args[++i]);
      else if (args[i].equals("-y"))
        y = Integer.parseInt(args[++i]);
    }
  
    MEWindow wnd = new MEWindow(x, y);
    wnd.setVisible(true);

    Mundo.init();
    MEService svc = new MEService(name);
    Mundo.registerService(svc);
    wnd.setService(svc);
  }
}
