
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.mundo.rt.Channel;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.Subscriber;
import org.mundo.rt.TypedMap;
import org.mundo.rt.ProtocolStack;
import org.mundo.rt.IMessageHandler;

class ChatService extends Service implements IReceiver
{
  ChatService()
  {
  }
  public void init()
  {
    Channel ch=getSession().getChannel("lan", "chattest");
    publisher=getSession().publish(ch);
    subscriber=getSession().subscribe(ch, this);
  }
  public void run()
  {
    try
    {
      BufferedReader r=new BufferedReader(new InputStreamReader(System.in));
      String ln;
      while ( (ln=r.readLine())!=null && !ln.equals(".") )
      {
        TypedMap map=new TypedMap();
        map.putString("ln", ln);
        publisher.send(new Message(map));
      }
    }
    catch (Exception x)
    {
      x.printStackTrace();
    }
  }
  public void received(Message msg, MessageContext ctx)
  {
    System.out.println(msg.getMap().getString("ln"));
  }
  private Publisher publisher;
  private Subscriber subscriber;
}

class SimpleChat
{
  public static void main(String args[]) throws Exception
  {
//    Mundo.setConfig(NodeConf.getMap());
    Mundo.init();

    ChatService cs = new ChatService();
    Mundo.registerService(cs);
    cs.run();

    Mundo.shutdown();
    System.exit(0);
  }
}
