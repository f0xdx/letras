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
import org.mundo.net.ProtocolStackBuilder;
import org.mundo.net.routing.RSSubscription;

class ChatService extends Service implements IReceiver
{
  ChatService()
  {
  }
  public void init()
  {
    ProtocolStackBuilder psb = new ProtocolStackBuilder();
    psb.add(org.mundo.net.ActivationService.class);
    psb.add(org.mundo.net.routing.RoutingService.class);
    psb.add(org.mundo.net.BinSerializationHandler.class);
    psb.add(org.mundo.net.transport.ip.IPTransportService.class);
    ProtocolStack stack = psb.getStack();

    Channel channel = RSSubscription.getChannel(session, MIME_TYPE);
    channel.setStack(stack);
    subscriber = RSSubscription.subscribe(session, channel, this);
    publisher = RSSubscription.publish(session, channel);
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
        RSSubscription.sendToZone(publisher, new Message(map));
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

    System.out.println("(trace: "+msg.getMap("rs", "passive").getArray("trace")+")");
  }
  private Publisher publisher;
  private Subscriber subscriber;
  private static final String MIME_TYPE = "message/mc-broadcastchat";
  private static final String ZONE = "lan";
}

class BroadcastChat
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
