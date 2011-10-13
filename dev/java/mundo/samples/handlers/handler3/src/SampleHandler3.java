import org.mundo.rt.IMessageHandler;
import org.mundo.rt.Message;
import org.mundo.rt.Service;
import org.mundo.rt.Logger;
import org.mundo.rt.TypedMap;
import org.mundo.net.ProtocolCoordinator;
import org.mundo.net.AbstractHandler;

/**
 * Example handler 3
 */
public class SampleHandler3 extends AbstractHandler
{
  public SampleHandler3()
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
  
  private static final String mimeType = "message/sample3";
  private Logger log = Logger.getLogger("sample");
}
