import org.mundo.rt.IMessageHandler;
import org.mundo.rt.Message;
import org.mundo.rt.Service;
import org.mundo.rt.Logger;
import org.mundo.rt.TypedMap;
import org.mundo.net.ProtocolCoordinator;
import org.mundo.net.AbstractHandler;

/**
 * This handler demonstrates how to duplicate a packet.
 */
public class SampleHandler5 extends AbstractHandler
{
  public SampleHandler5()
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
   * Called when a packet travels down the stack.
   */
  public boolean down(Message msg)
  {
    try
    {
      log.fine("down: " + msg.getBlob("all", "bin").size());

      // It is often necessary to keep connection state. Such state
      // should be associated with the ID of the remote node.
      log.finest("sending to node: "+getLink(msg).remoteId);

      // Add our own header to the message
      TypedMap hdr = new TypedMap();
      // Store the current MIME type of the message in our header
      hdr.putString("type", msg.getType());
      putHeader(msg, "sample", hdr);

      // Set our MIME type
      msg.setType(mimeType);
      return emit_down(msg);
    }
    catch(Exception x)
    {
      log.exception(x);
    }
    // Tell the caller that we dropped the packet
    return false;
  }
  /**
   * Called when a packet travels up the stack.
   */
  public boolean up(Message msg)
  {
    try
    {
      log.fine("up: " + msg.getBlob("all", "bin").size());

      // It is often necessary to keep connection state. Such state
      // should be associated with the ID of the remote node.
      log.finest("receiving from node: "+getLink(msg).remoteId);

      // Get our header
      TypedMap hdr = getHeader(msg, "sample");
      // Restore the previous MIME type
      msg.setType(hdr.getString("type"));
      
      return emit_up(msg);
    }
    catch(Exception x)
    {
      log.exception(x);
    }
    // Tell the caller that we dropped the packet
    return false;
  }
  
  private static final String mimeType = "message/sample5";
  private Logger log = Logger.getLogger("sample");
}
