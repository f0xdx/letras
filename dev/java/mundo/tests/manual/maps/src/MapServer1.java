import org.mundo.rt.Blob;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.TypedMap;
import org.mundo.rt.Publisher;
import org.mundo.rt.Subscriber;
import org.mundo.util.DefaultApplication;

class MapServer1 extends DefaultApplication implements IReceiver
{
  @Override
  public void init()
  {
    getSession().subscribe("lan", "maptest1.request", this);
    pub = getSession().publish("lan", "maptest1.reply");
  }
  public void received(Message reqMsg, MessageContext ctx)
  {
    TypedMap map = reqMsg.getMap();
    Message msg = new Message(map);
    pub.send(msg);
  }
  public static void main(String args[])
  {
    start(new MapServer1());
  }
  private Publisher pub;
}
