import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.IReceiver;
import org.mundo.rt.TypedMap;
import org.mundo.util.DefaultApplication;

public class RFIDTestP extends DefaultApplication implements IReceiver
{
  private String channelName;

  public RFIDTestP(String channelName)
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
      TypedMap map = msg.getMap("main", "passive").getMap("object");

      System.out.println("id: " + map.getString("id") +
                         " readerId: " + map.getString("readerId") +
                         " tstmp: " + map.getLong("tstmp"));
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }

  public static void main(String args[])
  {
    start(new RFIDTestP("showroom.rfid"));
  }
}
