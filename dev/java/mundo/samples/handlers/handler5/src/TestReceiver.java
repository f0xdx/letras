import java.io.DataOutputStream;
import java.io.FileOutputStream;

import org.mundo.rt.Blob;
import org.mundo.rt.Channel;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.Subscriber;
import org.mundo.rt.TypedMap;
import org.mundo.service.ServiceManager;
import org.mundo.util.DefaultApplication;

class TestReceiver extends Service implements IReceiver
{
  TestReceiver()
  {
  }
  @Override
  public void init()
  {
    super.init();
    subscriber = getSession().subscribe("lan", "nettest", this);
    try
    {
      os = new DataOutputStream(new FileOutputStream("receiver.trace"));
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  @Override
  public void shutdown()
  {
    if (os!=null)
    {
      try
      {
        os.close();
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
    }
    super.shutdown();
  }
  public void received(Message msg, MessageContext ctx)
  {
    TypedMap map = msg.getMap();
    int sz = 0;
    Blob blob = msg.getBlob("payload", "bin");
    if (blob!=null)
      sz = blob.size();

    if (os!=null)
    {
      try
      {
        os.writeInt(16);
        os.writeLong(System.currentTimeMillis());
        os.writeInt(sz);
        os.writeInt(map.getInt("seq"));
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
    }
  }
  public static void main(String args[])
  {
    Mundo.init();
    TestReceiver svc = new TestReceiver();
    Mundo.registerService(svc);
    DefaultApplication.pause();
    Mundo.shutdown();
  }

  private Subscriber subscriber;
  private DataOutputStream os;
}
