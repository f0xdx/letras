import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;
import javax.swing.SpringLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mundo.rt.Blob;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Publisher;
import org.mundo.rt.TypedMap;
import org.mundo.service.DebugService;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;

import org.mundo.reflect.MCIFParser;
import org.mundo.reflect.MInterface;
import org.mundo.reflect.MMethod;
import org.mundo.reflect.MParam;

class InterfaceWindow extends JFrame
{
  InterfaceWindow(String cn, String ifName, String text) throws IOException
  {
    channelName=cn;
    interfaceName=ifName;
    MCIFParser parser=new MCIFParser();
    MInterface mif=parser.parseInterface(interfaceName, text);
    methods=new Vector<MMethod>(mif.getMethods());
  
    setTitle("Interface: "+interfaceName);
    setSize(600, 600);

    JPanel topPane=new JPanel();
    topPane.setLayout(new BorderLayout());
    JLabel label=new JLabel("Interface");
    topPane.add(label, BorderLayout.NORTH);
    label.setBorder(new EmptyBorder(2, 4, 2, 0));
    lbMethods=new JList(methods);
    topPane.add(new JScrollPane(lbMethods), BorderLayout.CENTER);
    lbMethods.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent ev) {
        if (!ev.getValueIsAdjusting())
          methodSelected();
      }
    });

    mainSplitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    getContentPane().add(mainSplitPane, BorderLayout.CENTER);
    mainSplitPane.setTopComponent(topPane);
    mainSplitPane.setBottomComponent(new JPanel());

    service=new Service();
    Mundo.registerService(service);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        Mundo.unregisterService(service);
        dispose();
      }
    });
  }
  private void methodSelected()
  {
    MMethod mtd=methods.get(lbMethods.getSelectedIndex());
    textFields=new ArrayList<JTextField>();
    JPanel panel=new JPanel();

    panel.add(new JLabel("channel"));
    JTextField fld=new JTextField(channelName);
    textFields.add(fld);
    panel.add(fld);

    panel.add(new JLabel("interface"));
    fld=new JTextField(interfaceName);
    textFields.add(fld);
    panel.add(fld);

    panel.add(new JLabel("request"));
    fld=new JTextField(mtd.getName());
    textFields.add(fld);
    panel.add(fld);

    panel.add(new JLabel("ptypes"));
    fld=new JTextField(mtd.getParamTypeCode());
    textFields.add(fld);
    panel.add(fld);

    int i=0;
    for (MParam param : mtd.getParams())
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

    JPanel bottomPane=new JPanel();
    bottomPane.setLayout(new BorderLayout());
    JLabel label=new JLabel("Request");
    bottomPane.add(label, BorderLayout.NORTH);
    label.setBorder(new EmptyBorder(2, 4, 2, 0));
    bottomPane.add(new JScrollPane(reqPane), BorderLayout.CENTER);

    JPanel buttonPane=new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
    bottomPane.add(buttonPane, BorderLayout.SOUTH);
    JButton btn=new JButton("Send");
    buttonPane.add(btn);
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        sendPressed();
      }
    });

    mainSplitPane.setBottomComponent(bottomPane);
  }
  private void sendPressed()
  {
    TypedMap req=new TypedMap();
    req.putString("interface", textFields.get(1).getText());
    req.putString("request", textFields.get(2).getText());
    req.putString("ptypes", textFields.get(3).getText());
    req.putGUID("sessionId", service.getSession().getId());
    int i, s=textFields.size()-4;
    for (i=0; i<s; i++)
    {
      req.putString("p"+i, textFields.get(i+4).getText());
    }
    System.out.println(req);
    Publisher pub=service.getSession().publish("lan", textFields.get(0).getText());
    pub.send(new Message(req));
    service.getSession().unpublish(pub);
  }

  private String channelName;
  private String interfaceName;
  private ArrayList<JTextField> textFields;
  private Vector<MMethod> methods;
  private JList lbMethods;
  private JSplitPane mainSplitPane;
  private Service service;
}
