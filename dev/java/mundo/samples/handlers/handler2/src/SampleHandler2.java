import org.mundo.rt.IMessageHandler;
import org.mundo.rt.Message;
import org.mundo.rt.Service;
import org.mundo.rt.Logger;
import org.mundo.net.ProtocolCoordinator;
import org.mundo.net.AbstractHandler;

/**
 * This is one of the simplest possible handlers.
 * 
 * @author erwin
 */
public class SampleHandler2 extends AbstractHandler
{
  public SampleHandler2()
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
    log.fine("down: " + msg.getBlob("all", "bin").size());
    // Set our MIME Type before sending the message
    msg.setType(mimeType);
    return emit_down(msg);
  }
  /**
   * Called when a packet travels up the stack.
   */
  public boolean up(Message msg)
  {
    log.fine("up: " + msg.getBlob("all", "bin").size());
    // Change the MIME Type back. In this example, we assume that the next
    // handler is always mc-bincoll. Note that this assumption cannot be
    // made in general!
    msg.setType("message/mc-bincoll");
    return emit_up(msg);
  }
  
  private static final String mimeType = "message/sample2";
  private Logger log = Logger.getLogger("sample");
}
