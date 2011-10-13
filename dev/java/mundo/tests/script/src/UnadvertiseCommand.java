import org.mundo.rt.Publisher;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class UnadvertiseCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel=attrMap.getNamedItem("channel").getNodeValue();
    System.out.println("unadvertise "+channel);
    
    Publisher pub=(Publisher)ctx.publishers.get(channel);
    ctx.session.unpublish(pub);
    ctx.publishers.remove(channel);
  }
}
