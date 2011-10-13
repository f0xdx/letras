import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mundo.rt.Blob;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Logger;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;
import org.mundo.xml.XMLDeserializer;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;

public class ChannelPage extends Page implements IReceiver
{
  ChannelPage(String cn)
  {
    channelName=cn;
  }
  @Override
  String getURI()
  {
    return "channel:"+channelName;
  }
  @Override
  public void load()
  {
    window.pageLoaded(this);

    JPanel pane=new JPanel();
    pane.setLayout(new BorderLayout());
    
    splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    lbMessagesModel=new DefaultListModel();
    splitPane.setLeftComponent(new JScrollPane(lbMessages=new JList(lbMessagesModel)));
    splitPane.setRightComponent(new JScrollPane());
    splitPane.setDividerLocation(0.5);

    JPanel pubPane=new JPanel();
    pubPane.setLayout(new BorderLayout());
    pubPane.add(new JLabel("Publish:"), BorderLayout.NORTH);
    pubPane.add(new JScrollPane(taPubMessage=new JTextArea()), BorderLayout.CENTER);

    JPanel buttonPane=new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    JButton sendButton=new JButton("Send");
    buttonPane.add(sendButton);
    sendButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        sendPressed();
      }
    });
    pubPane.add(buttonPane, BorderLayout.SOUTH);

    JSplitPane mainSplitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    pane.add(mainSplitPane, BorderLayout.CENTER);
    mainSplitPane.setTopComponent(splitPane);
    mainSplitPane.setBottomComponent(pubPane);
    
    final Service service=new Service();
    Mundo.registerService(service);
    service.getSession().subscribe("lan", channelName, this);
    publisher=service.getSession().publish("lan", channelName);
    publisher.enableLocalLoopback(true);
//    DebugService.addRawListener(this);
    
    lbMessages.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent ev) {
        int i=lbMessages.getSelectedIndex();
        if (i>=0)
          showMessage(messages.get(i)); //taMessage.setText((String)messages.get(i));
      }
    });

    window.showComponent(pane);
/*    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        Mundo.unregisterService(service);
        dispose();
      }
    });
*/
  }
  public void received(Message msg, MessageContext ctx) // IReceiver
  {
    int i=lbMessagesModel.size()+1;
    lbMessagesModel.addElement("#"+i);
    messages.add(msg);
    lbMessages.setSelectedIndex(i-1);
  }
  private void showMessage(Message msg)
  {
    // If the message has never passed a routing service, then create the passive
    // chunks now.
    try
    {
      if (msg.getMap("main", "passive")==null)
        msg.passivate();
    }
    catch(Exception x)
    {
      x.printStackTrace();
      return;
    }
  
    XMLSerializer ser=new XMLSerializer();
    JPanel panel=new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    for (Message.Chunk chunk : msg)
    {
      if ("passive".equals(chunk.type))
      {
        try
        {
          panel.add(new JLabel(chunk.name+" : passive", JLabel.LEFT));
          panel.add(new JSeparator());
          panel.add(new JTextArea(XMLFormatter.format(ser.serializeMap(chunk.name, (TypedMap)chunk.content))));
          panel.add(new JSeparator());
        }
        catch(Exception x)
        {
          x.printStackTrace();
        }
      }
      else if ("bin".equals(chunk.type))
      {
        Blob b=(Blob)chunk.content;
        panel.add(new JLabel(chunk.name+" : bin ("+b.size()+" bytes)"));
        panel.add(new JSeparator());
      }
    }
    JPanel panel2=new JPanel();
    panel2.setLayout(new BorderLayout());
    panel2.add(panel, BorderLayout.NORTH);
    splitPane.setRightComponent(new JScrollPane(panel2));
  }
  private void sendPressed()
  {
    try
    {
      XMLDeserializer deser=new XMLDeserializer();
      Object obj=deser.deserializeObject(taPubMessage.getText());
      Message msg=new Message();
      msg.put("main", "passive", (TypedMap)obj);
      try
      {
        msg.activate();
      }
      catch(Exception x)
      {
        log.exception(x);
      }
      publisher.send(msg);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }

  private String channelName;
  private JLabel lChannelName;
  private JList lbMessages;
  private JSplitPane splitPane;
  private ArrayList<Message> messages = new ArrayList<Message>();
  private DefaultListModel lbMessagesModel;
  private JTextArea taPubMessage;
  private Publisher publisher;
  private Logger log=Logger.getLogger("inspect.tap");
}
