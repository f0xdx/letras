import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.IReceiver;
import org.mundo.util.DefaultApplication;
//@mcImport
import org.mundo.rfid.RFIDEvent;

public class RFIDTest extends DefaultApplication implements IReceiver
{
  private String channelName;

  public RFIDTest(String channelName)
  {
    this.channelName = channelName;
  }
  @Override
  public void init()
  {
    super.init();
    session.subscribe("lan", channelName, this);
  }
  public void received(Message msg, MessageContext ctx)
  {
    try
    {
      RFIDEvent ev = (RFIDEvent)msg.getObject();
      System.out.println("id: "+ev.id+" readerId: "+ev.readerId+" tstmp: "+ev.tstmp);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }

  public static void main(String args[])
  {
    start(new RFIDTest("showroom.rfid"));
  }
}
