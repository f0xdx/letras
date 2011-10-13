import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class SubscribeCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel=attrMap.getNamedItem("channel").getNodeValue();
    String zone=attrMap.getNamedItem("zone").getNodeValue();
    System.out.println("subscribe "+zone+":"+channel);
    
    SubscriptionEntry se=(SubscriptionEntry)ctx.subscribers.get(channel);
    if (se==null)
    {
      se = new SubscriptionEntry(ctx, attrMap.getNamedItem("zone").getNodeValue(), channel);
      ctx.subscribers.put(channel, se);
    }
  }
}
