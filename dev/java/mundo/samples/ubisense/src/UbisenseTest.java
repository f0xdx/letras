import org.mundo.util.DefaultApplication;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.IReceiver;

//@mcImport
import org.mundo.context.sensor.UbisenseLocation;
//@mcImport
import org.mundo.context.Vector3d;

public class UbisenseTest extends DefaultApplication implements IReceiver
{
  @Override
  public void init()
  {
    session.subscribe("lan", "ubisense", this);
  }
  public void received(Message msg, MessageContext ctx)
  {
    UbisenseLocation loc = (UbisenseLocation)msg.getObject();
    System.out.println(loc.name+": "+loc.p.x+", "+loc.p.y+", "+loc.p.z);
  }
  public static void main(String args[])
  {
    start(new UbisenseTest());
  }
}

