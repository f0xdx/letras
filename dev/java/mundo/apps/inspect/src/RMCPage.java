import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mundo.reflect.MInterface;
import org.mundo.reflect.MMethod;
import org.mundo.reflect.MParam;
import org.mundo.reflect.GenericClientStub;
import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.Publisher;
import org.mundo.rt.Signal;
import org.mundo.rt.AsyncCall.IResultListener;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;

public class RMCPage extends Page implements IResultListener
{
  RMCPage(String cn, String ifn)
  {
    channelName=cn;
//    interfaceName=ifn;
  }
  @Override
  String getURI()
  {
    return "rmc:"+channelName;
  }
  @Override
  public void load()
  {
    window.pageLoaded(this);
    stub=new GenericClientStub();
    Publisher p=window.getService().getSession().publish("lan", channelName);
    Signal.connect(stub, p);
    try
    {
      ifcs = stub.queryInterfaces();
    }
    catch(Exception x)
    {
      window.showException(x);
      ifcs = null;
    }
    window.getService().getSession().unpublish(p);
    if (ifcs==null)
      return;
    
    JPanel pane = new JPanel();
    pane.setLayout(new SpringLayout());

//    JLabel label=new JLabel("Interface: "+interfaceName);
//    pane.add(label);
//    label.setBorder(new EmptyBorder(2, 4, 2, 0));
    Vector<String> mtdNames = new Vector<String>();
    for (MInterface ifc : ifcs)
    {
      for (MMethod mtd : ifc.getMethods())
      {
        mtdNames.add(ifc.getName()+":"+mtd.getName());
      }
    }
    
    lbMethods = new JList(mtdNames);
    pane.add(new JScrollPane(lbMethods));
    lbMethods.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent ev) {
        if (!ev.getValueIsAdjusting())
          methodSelected();
      }
    });

    JLabel label;
    pane.add(label=new JLabel("Request"));
    label.setBorder(new EmptyBorder(2, 4, 2, 0));
    pane.add(reqScrollPane=new JScrollPane());
    
    pane.add(label=new JLabel("Reply"));
    label.setBorder(new EmptyBorder(2, 4, 2, 0));
    pane.add(replyScrollPane=new JScrollPane());    
    
    JPanel buttonPane=new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    JButton btn=new JButton("Invoke");
    buttonPane.add(btn);
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        invokePressed();
      }
    });
    pane.add(buttonPane);

    SpringUtilities.makeCompactGrid(pane, pane.getComponentCount(), 1, 4, 4, 0, 0);
    window.showComponent(pane);
  }
  private void methodSelected()
  {
    String s = (String)lbMethods.getSelectedValue();
    int i = s.indexOf(':');
    String ifName = s.substring(0, i);
    String mtdName = s.substring(i+1);
    for (MInterface ifc : ifcs)
    {
      if (ifc.getName().equals(ifName))
      {
        method = ifc.getMethod(mtdName);
        break;
      }
    }

    textFields=new ArrayList<JTextField>();
    JPanel panel=new JPanel();

    panel.add(new JLabel("channel"));
    JTextField fld=new JTextField(channelName);
    fld.setEditable(false);
    textFields.add(fld);
    panel.add(fld);

    panel.add(new JLabel("interface"));
    fld=new JTextField(ifName);
    fld.setEditable(false);
    textFields.add(fld);
    panel.add(fld);

    panel.add(new JLabel("request"));
    fld = new JTextField(method.getName());
    fld.setEditable(false);
    textFields.add(fld);
    panel.add(fld);

    panel.add(new JLabel("ptypes"));
    fld = new JTextField(method.getParamTypeCode());
    fld.setEditable(false);
    textFields.add(fld);
    panel.add(fld);

    i=0;
    for (MParam param : method.getParams())
    {
      panel.add(new JLabel(param.getName()+" ("+param.getType()+")"));
      fld=new JTextField();
      textFields.add(fld);
      panel.add(fld);
      i++;
    }

    panel.setLayout(new SpringLayout());
    SpringUtilities.makeCompactGrid(panel, i+4, 2, 8, 4, 8, 2);

    JPanel reqPane=new JPanel();
    reqPane.setLayout(new BorderLayout());
    reqPane.add(panel, BorderLayout.NORTH);
    reqPane.add(new JLabel(), BorderLayout.CENTER);
    
    reqScrollPane.setViewportView(reqPane);
  }
  private void invokePressed()
  {
    int i, s=textFields.size()-4;
    Object[] params=new Object[s];
    for (i=0; i<s; i++)
      params[i]=textFields.get(i+4).getText();

    Publisher pub = window.getService().getSession().publish("lan", channelName);
    Signal.connect(stub, pub);
    AsyncCall ac = stub.invoke(method, params, DoObject.CREATEONLY);
    window.getService().getSession().unpublish(pub);

    ac.setResultListener(this);
    ac.invoke();
  }
  public void resultReceived(AsyncCall c)
  {
    if (c.getException()!=null)
    {
      replyScrollPane.setViewportView(new JTextArea(c.getException().toString()));
      return;
    }
    String s;
    try
    { 
      XMLSerializer ser=new XMLSerializer();
      s=XMLFormatter.format(ser.serializeMap("config", c.getMap().passivateMap()));
    }
    catch(Exception x)
    {
      s=x.toString();
    }
    replyScrollPane.setViewportView(new JTextArea(s));
  }

  private GenericClientStub stub;
  private String channelName;
//  private String interfaceName;
  private ArrayList<JTextField> textFields;
  private MInterface ifcs[];
  private MMethod method;
  private JList lbMethods;
  private JScrollPane reqScrollPane;
  private JScrollPane replyScrollPane;
}
