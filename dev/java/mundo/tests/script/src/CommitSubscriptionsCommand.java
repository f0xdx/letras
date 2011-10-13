import java.util.Iterator;
import org.w3c.dom.Node;

class CommitSubscriptionsCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    for (Iterator<SubscriptionEntry> i=ctx.subscribers.values().iterator(); i.hasNext();)
      i.next().subscribe();
  }
}
