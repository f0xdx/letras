import java.util.List;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.DefaultListModel;

import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.service.ServiceManager;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ResultSet;
import org.mundo.service.IUS;
import org.mundo.service.DoIME;

class USService extends Service implements IUS
{
  USService(USWindow w)
  {
    wnd = w;
    services = new Service[2];
    services[0] = new KeyboardDevice();
    services[1] = new MouseDevice();
    for (Service s : services)
      Mundo.registerService(s);
  }
  public String getName() // IUS
  {
    return getServiceInstanceName();
  }
  public void associate(DoIME me) // IUS
  {
    wnd.setStatus(true, "associated with "+me.getName());
    String zone = me.getZoneName();
    System.out.println("zone: "+zone);
    setServiceZone(zone);
    for (Service s : services)
      s.setServiceZone(zone);
  }
  public void deassociate(DoIME me) // IUS
  {
    wnd.setStatus(false, "available");
    setServiceZone("lan");
    for (Service s : services)
      s.setServiceZone(null);
  }
  USWindow wnd;
  Service[] services;
}

class USWindow extends JFrame
{
  USWindow(int x, int y)
  {
    setLocation(x, y);
    setSize(400, 100);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        dispose();
        Mundo.shutdown();
      }
    });
    
    JPanel p1 = new JPanel();
    p1.setLayout(new BorderLayout());
    p1.add(new JLabel("Status:"), BorderLayout.WEST);
    meLabel = new JLabel("available");
    p1.add(meLabel, BorderLayout.CENTER);

    JPanel p2 = new JPanel();
    p2.setLayout(new FlowLayout());
    registerButton = new JButton("Register");
    registerButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        cmdRegister();
      }
    });
    registerButton.setEnabled(false);
    p2.add(registerButton);
    unregisterButton = new JButton("Unregister");
    unregisterButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        cmdUnregister();
      }
    });
    p2.add(unregisterButton);
    deassociateButton = new JButton("Deassociate");
    deassociateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        cmdDeassociate();
      }
    });
    deassociateButton.setEnabled(false);
    p2.add(deassociateButton);
    p1.add(p2, BorderLayout.SOUTH);

    setContentPane(p1);
  }
  void setService(USService s)
  {
    svc = s;
    setTitle("US: "+svc.getServiceInstanceName());
  }
  void setStatus(boolean associated, String status)
  {
    meLabel.setText(status);
    deassociateButton.setEnabled(associated);
  }
  void cmdDeassociate()
  {
    svc.deassociate(null);
  }
  void cmdRegister()
  {
    if (registered)
      return;
    Mundo.registerService(svc);
    registered = true;
    registerButton.setEnabled(false);
    unregisterButton.setEnabled(true);
  }
  void cmdUnregister()
  {
    if (!registered)
      return;
    Mundo.unregisterService(svc);
    registered = false;
    registerButton.setEnabled(true);
    unregisterButton.setEnabled(false);
  }
  
  private USService svc;
  private JLabel meLabel;
  private JButton registerButton;
  private JButton unregisterButton;
  private JButton deassociateButton;
  private boolean registered = true;
}

public class TestUS extends Service
{
  public static void main(String args[])
  {
    String name = "us";
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
  
    USWindow wnd = new USWindow(x, y);
    wnd.setVisible(true);

    Mundo.init();
    USService svc = new USService(wnd);
    svc.setServiceInstanceName(name);
    svc.setServiceZone("lan");
    Mundo.registerService(svc);
    wnd.setService(svc);
  }
}
