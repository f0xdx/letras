import org.mundo.rt.Publisher;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class AdvertiseCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel=attrMap.getNamedItem("channel").getNodeValue();
    String zone=attrMap.getNamedItem("zone").getNodeValue();
//      System.out.println("TSS: advertise "+zone+":"+channel);

    Publisher pub = ctx.session.publish(zone, channel);
    ctx.publishers.put(channel, pub);      
  }
}
