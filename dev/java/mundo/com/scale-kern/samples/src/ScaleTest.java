import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.IReceiver;
import org.mundo.util.DefaultApplication;
//@mcImport
import org.mundo.service.scale.ScaleEvent;

public class ScaleTest extends DefaultApplication implements IReceiver
{
  private String channelName;

  public ScaleTest(String channelName)
  {
	this.channelName = channelName;
  }
  @Override
  public void init()
  {
    super.init();
    session.subscribe("lan", channelName+".event", this);
  }
  public void received(Message msg, MessageContext ctx)
  {
    try
	{
	  ScaleEvent se = (ScaleEvent)msg.getObject();
	  if (se.stable)
	    System.out.println("weight: "+se.weight+" "+se.unit+" (tolerance: "+se.tolerance+" "+se.unit+")");
	  else
		System.out.println("(unstable: "+se.weight+" "+se.unit+")");
	}
    catch(Exception x)
	{
	  x.printStackTrace();
	}
  }

  public static void main(String args[])
  {
	start(new ScaleTest("scale"));
  }
}
