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

class TestSender extends Service
{
  TestSender()
  {
  }
  @Override
  public void init()
  {
    super.init();
    publisher = getSession().publish("lan", "nettest");
  }
  @Override
  public void shutdown()
  {
    super.shutdown();
  }
  public void run(String[] args) throws InterruptedException
  {
    int payloadSize = 4096;
    Blob payload = null;
    if (payloadSize>0)
    {
      byte[] buf = new byte[payloadSize];
      payload = new Blob();
      payload.write(buf);
    }
    for (int i=1; i<=10; i++)
    {
      TypedMap map = new TypedMap();
      map.putInt("seq", i);
      Message msg = new Message(map);
      if (payload!=null)
        msg.put("payload", "bin", payload);
      publisher.send(msg);
    }
  }
  public static void main(String args[])
  {
    Mundo.init();
    TestSender svc = new TestSender();
    Mundo.registerService(svc);
    try
    {
      svc.run(args);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
    Mundo.shutdown();
  }

  private Publisher publisher;
}
