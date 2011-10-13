import org.mundo.net.AbstractHandler;
import org.mundo.net.ProtocolCoordinator;
import org.mundo.rt.IBCLProvider;
import org.mundo.rt.IMessageHandler;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Logger;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.Subscriber;
import org.mundo.rt.TypedMap;

/**
 * This is the skeleton for a message broker.
 * @author Erwin Aitenbichler
 */
public class DumbTopicBroker extends AbstractHandler implements IBCLProvider.ISignal, IReceiver
{
  public DumbTopicBroker()
  {
  }
  /**
   * Called on initialization of the service.
   */
  @Override
  public void init() // Service
  {
    super.init();
    log.fine("init");
    // Register our MIME Type with the protocol coordinator
    ProtocolCoordinator.register(mimeType, this);
    // Receive all local messages
    session.subscribe("rt", null, this);
  }
  /**
   * Called on shutdown of the service.
   */
  @Override
  public void shutdown() // Service
  {
    log.fine("shutdown");
    super.shutdown();
  }
  /**
   * Receives all local messages. Messages are forwarded to the topmost
   * protocol handler in the protocol stack.
   */
  public void received(Message msg, MessageContext c) // IReceiver
  {
    msg = msg.copyFrame();
    TypedMap pmap = new TypedMap();
    pmap.putString("channel", c.channel.getName());
    msg.put(headerChunkName, "param", pmap);
    ProtocolCoordinator.getInstance().firstDown(msg);
  }
  /**
   * Called when a packet travels down the stack.
   */
  public boolean down(Message msg) // IMessageHandler
  {
    TypedMap param = msg.getMap(headerChunkName, "param");
    if (param==null)
    {
      log.warning("no "+headerChunkName+" parameter in message");
      return false;
    }
    String channel = param.getString("channel");
    log.fine("down: channel="+channel);

    // Add our own header to the message
    msg = msg.copyFrame();
    TypedMap hdr = new TypedMap();
    hdr.putString("channel", channel);
    msg.put(headerChunkName, "passive", hdr);

    // Set our MIME type
    msg.setType(mimeType);

    // Create a parameter chunk for the routing service and define the whole
    // zone as destination for the message
    TypedMap rs = new TypedMap();
    rs.putString("destType", "zone");
    msg.put("rs", "param", rs);

    // Pass the message to the next lower handler
    return emit_down(msg);
  }
  /**
   * Called when a packet travels up the stack.
   */
  public boolean up(Message msg) // IMessageHandler
  {
    // Get our header
    TypedMap hdr = msg.getMap(headerChunkName, "passive");
    if (hdr==null)
    {
      log.warning("no "+headerChunkName+" header in message");
      return false;
    }
    String channel = hdr.getString("channel");
    log.fine("up: channel="+channel);
    // Reconstruct the address chunk
    TypedMap amap = msg.getOrCreateMap("address", "passive");
    amap.put("channel", channel);
    // Also put the session of this broker into the address chunk. This will
    // prevent that the message will be looped back to us
    amap.put("session", session);
    return emit_up(msg);
  }
  /**
   * Called when a new subscriber is added by a local service.
   * @param s  the subscriber object.
   */
  public void subscriberAdded(Subscriber s) // IBCLProvider.ISignal
  {
    log.fine("subscriberAdded: "+s);
  }
  /**
   * Called when a subscriber is removed by a local service.
   * @param s  the subscriber object.
   */
  public void subscriberRemoved(Subscriber s) // IBCLProvider.ISignal
  {
    log.fine("subscriberRemoved: "+s);
  }
  /**
   * Called when a new publisher is added by a local service.
   * @param p  the publisher object.
   */
  public void publisherAdded(Publisher p) // IBCLProvider.ISignal
  {
    log.fine("publisherAdded: "+p);
  }
  /**
   * Called when a publisher is removed by a local service.
   * @param p  the publisher object.
   */
  public void publisherRemoved(Publisher p) // IBCLProvider.ISignal
  {
    log.fine("publisherRemoved: "+p);
  }
  
  private static final String headerChunkName = "dumbtb";
  private static final String mimeType = "message/dumbtb";
  private Logger log = Logger.getLogger("dumbtb");
}
