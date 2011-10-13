import org.mundo.rt.Subscriber;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class UnsubscribeCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel=attrMap.getNamedItem("channel").getNodeValue();
    System.out.println("unsubscribe "+channel);

    Subscriber sub = ctx.subscribers.get(channel).sub;
    ctx.session.unsubscribe(sub);
    ctx.subscribers.remove(channel);
  }
}
