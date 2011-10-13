import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Subscriber;
import org.mundo.rt.TypedMap;

class SubscriptionEntry implements IReceiver
{
  public SubscriptionEntry(Context c, String z, String ch)
  {
    ctx=c;
    zone=z;
    channel=ch;
    sub=null;
  }
  public void subscribe()
  {
    if (sub==null)
      sub = ctx.session.subscribe(zone, channel, this);
  }
  public void received(Message msg, MessageContext mctx)
  {
    Iterator iter=exp.iterator();
    while (iter.hasNext())
    {
      ExpectEntry ee=(ExpectEntry)iter.next();
      if (ee.expects(ctx, msg))
      {
        String ln = msg.getMap().getString("ln");
        if (ln!=null)
          ctx.log.info("rcvd expected message: "+ln);
        else
          ctx.log.info("rcvd null message");
        iter.remove();
        ctx.expectCnt--;
        return;
      }
    }
    ctx.log.warning("unexpected message: "+msg);
  }
  public void add(TypedMap map, String name, String depends)
  {
    ctx.expectCnt++;
    exp.add(new ExpectEntry(map, name, depends));
  }
  /**
   * Lists all expected messages we did not receive so far.
   */
  public void printMissing(PrintStream os)
  {
    Iterator iter=exp.iterator();
    while (iter.hasNext())
    {
      ExpectEntry ee=(ExpectEntry)iter.next();
      if (!ee.received)
        os.println("missing: "+ee);
    }
  }
  private Context ctx;
  private String zone;
  private String channel;
  public Subscriber sub;
  public ArrayList exp=new ArrayList();
}
