import org.mundo.rt.Blob;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.TypedMap;
import org.mundo.rt.Publisher;
import org.mundo.rt.Subscriber;
import org.mundo.util.DefaultApplication;

class BlobMsgServer extends DefaultApplication implements IReceiver
{
  @Override
  public void init()
  {
    getSession().subscribe("lan", "blobmsg.request", this);
    pub = getSession().publish("lan", "blobmsg.reply");
  }
  public void received(Message reqMsg, MessageContext ctx)
  {
    TypedMap map = reqMsg.getMap();
    Blob blob = reqMsg.getBlob("blob", "bin");

    Message msg = new Message(map);
    msg.put("blob", "bin", blob);
    pub.send(msg);
  }
  public static void main(String args[])
  {
    start(new BlobMsgServer());
  }
  private Publisher pub;
}
