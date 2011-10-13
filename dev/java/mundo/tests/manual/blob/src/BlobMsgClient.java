import org.mundo.rt.Blob;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.TypedMap;
import org.mundo.rt.Publisher;
import org.mundo.rt.Subscriber;
import org.mundo.util.DefaultApplication;

class BlobMsgClient extends DefaultApplication implements IReceiver
{
  @Override
  public void init()
  {
    getSession().subscribe("lan", "blobmsg.reply", this);
    pub = getSession().publish("lan", "blobmsg.request");
  }
  public synchronized void received(Message msg, MessageContext ctx)
  {
    reply = msg;
    notify();
  }
  @Override
  public void run()
  {
    try
    {
      TypedMap map = new TypedMap();
      Blob blob = new Blob();
      for (int i=0; i<256; i++)
        blob.write((char)i);
      Message msg = new Message(map);
      msg.put("blob", "bin", blob);
      pub.send(msg);
      synchronized(this)
      {
        while (reply==null)
          wait();
      }
      Blob replyBlob = reply.getBlob("blob", "bin");
      if (blob.equals(replyBlob))
        System.out.println("SUCCESS!");
      else
      {
        System.out.println("ERROR!");
        System.out.println(replyBlob);
      }
    }
    catch(InterruptedException x)
    {
      x.printStackTrace();
    }
  }
  public static void main(String args[])
  {
    start(new BlobMsgClient());
  }
  private Publisher pub;
  private Message reply = null;
}
