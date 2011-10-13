import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import java.io.InputStream;
import java.io.IOException;

import org.mundo.rt.*;
import org.mundo.net.CLDCIPTransportService;
//import org.mundo.net.BluetoothTransportService;

/**
 * @author EA
 * @version 1.0
 */

public class RMCChatMIDlet extends MIDlet implements IChat, CLDCIPTransportService.IStatus
{
  /** TextField size constant */
  private static final int TEXTFIELD_SIZE = 256;
  /** Command priority constant */
  private static final int COMMAND_PRIORITY = 1;

  /** Command to connect to a desired address */
  private static final Command CMD_SEND = new Command("Send", Command.OK, COMMAND_PRIORITY);
  private static final Command CMD_CONNECT = new Command("IP Connect", Command.OK, COMMAND_PRIORITY);
  private static final Command CMD_INQUIRY = new Command("BT Inquiry", Command.OK, COMMAND_PRIORITY);
  private static final Command CMD_CANCEL = new Command("Cancel", Command.CANCEL, COMMAND_PRIORITY);
  private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, COMMAND_PRIORITY);
    
  /** TextField to enter url to connect to */
  private TextField textField;

  /** String constant for the socket address */
  private static final String DEFAULT_URL = "192.168.144.131:4242";

  private static final String CHANNEL_NAME = "chat_rmc";
  private static final String ZONE_NAME = "lan";

  private Display display;
  private MainForm mainForm;
  private Service service;
  private DoIChat doChat;

  /**
   * Creates the new text box, then set it to
   * current active component on screen
   */
  public void startApp()
  {
    if (display==null)
      display = Display.getDisplay(this);
    mainForm=new MainForm();
    display.setCurrent(mainForm);

    Mundo.init();
    Mundo.registerService(service = new Service());

    // connect channel to this object to receive chat messages
    Signal.connect(service.getSession().subscribe(ZONE_NAME, CHANNEL_NAME), this);

    // connect DoIChat stub to channel to send chat messages
    doChat = new DoIChat();
    Signal.connect(doChat, service.getSession().publish(ZONE_NAME, CHANNEL_NAME));

    // connection status    
    Signal.connect("rt", "org.mundo.net.CLDCIPTransportService_IStatus", this);
  }

  /**
   * Performs no operation, because there are no background
   * activities or record stores to be closed.
   */
  public void pauseApp()
  {
  }

  /**
   * Performs no operation because there is nothing which is not handled
   * by the garbage collector.
   *
   * @param unconditional ignored.
   */
  public void destroyApp(boolean unconditional)
  {
    Mundo.shutdown();
  }

  public void connectSucceeded()
  {
    displayAlert("connect succeeded");
  }
  public void connectFailed(String s)
  {
    displayAlert(s);
  }

  public void chatMessage(String msg) //IChat
  {
    mainForm.addPeerText(msg);
  }
  
  /** Displays an alert with the desired text to the user. */
  private void displayAlert(String alertText)
  {
    Alert alert = new Alert("Info", alertText, null, AlertType.INFO);
    display.setCurrent(alert, mainForm);
  }

  public class ConnectForm extends Form implements CommandListener
  {
    public ConnectForm()
    {
      super("chat-connect");
      tfAddress=new TextField("Connect to:", DEFAULT_URL, TEXTFIELD_SIZE, TextField.ANY);
      append(tfAddress);
      tfInterval=new TextField("Poll interval (0=off):", "0", 10, TextField.NUMERIC);
      append(tfInterval);
      addCommand(CMD_CONNECT);
      addCommand(CMD_CANCEL);
      setCommandListener(this);
    }
    public void commandAction(Command cmd, Displayable source)
    {
      if (cmd==CMD_CONNECT)
      {
        System.out.println("CMD_CONNECT");
        CLDCIPTransportService ipts=(CLDCIPTransportService)Mundo.getServiceByType(
                                     "org.mundo.net.CLDCIPTransportService");
        int interval=Integer.parseInt(tfInterval.getString());
        if (interval>0 && interval<1000)
          interval=1000;
        System.out.println("interval="+interval);
        ipts.setPollingInterval(interval);
        ipts.connect(tfAddress.getString());
        display.setCurrent(mainForm);
      }
      else if (cmd==CMD_CANCEL)
      {
        display.setCurrent(mainForm);
      }
    }
    private TextField tfAddress;
    private TextField tfInterval;
  }

  public class ComposeForm extends Form implements CommandListener
  {
    public ComposeForm()
    {
      super("chat-compose");
      tfText=new TextField("Message:", "", TEXTFIELD_SIZE, TextField.ANY);
      append(tfText);
      addCommand(CMD_SEND);
      addCommand(CMD_CANCEL);
      setCommandListener(this);
    }
    public void commandAction(Command cmd, Displayable source)
    {
      if (cmd==CMD_SEND)
      {
        doChat.chatMessage(tfText.getString(), doChat.ONEWAY);
        mainForm.addMyText(tfText.getString());
        display.setCurrent(mainForm);
      }
      else if (cmd==CMD_CANCEL)
      {
        display.setCurrent(mainForm);
      }
    }
    private TextField tfText;
  }

  public class MainForm extends Form implements CommandListener
  {
    public MainForm()
    {
      super("MundoCore Chat");
      // Create and append a StringItem to the form.
      stringItem=new StringItem(null, "");
      append(stringItem);
      addCommand(CMD_SEND);
      addCommand(CMD_CONNECT);
//      addCommand(CMD_INQUIRY);
      addCommand(CMD_EXIT);
      setCommandListener(this);
    }
    public void commandAction(Command cmd, Displayable source)
    {
      if (cmd==CMD_SEND)
      {
        display.setCurrent(new ComposeForm());
      }
      else if (cmd==CMD_CONNECT)
      {
        display.setCurrent(new ConnectForm());
      }
      else  if (cmd==CMD_INQUIRY)
      {
//        btTransportService.startInquiry();
      }
      else if (cmd==CMD_EXIT)
      {
        destroyApp(true);
        notifyDestroyed();
      }
    }
    public void addPeerText(String s)
    {
      stringItem.setText(stringItem.getText()+s+"\n");
    }
    public void addMyText(String s)
    {
      stringItem.setText(stringItem.getText()+"["+s+"]"+"\n");
    }
    StringItem stringItem;
  }
}
