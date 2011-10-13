import org.mundo.rt.IMessageHandler;
import org.mundo.rt.Message;
import org.mundo.rt.Service;
import org.mundo.rt.Logger;
import org.mundo.net.AbstractHandler;

/**
 * This is one of the simplest possible handlers.
 * 
 * @author erwin
 */
public class SampleHandler1 extends AbstractHandler
{
  public SampleHandler1()
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
    return emit_down(msg);
  }
  /**
   * Called when a packet travels up the stack.
   * This method will not be called, because we did not set the MIMEType yet.
   */
  public boolean up(Message msg)
  {
    log.fine("up: " + msg.getBlob("all", "bin").size());
    return emit_up(msg);
  }
  
  private Logger log = Logger.getLogger("sample");
}
