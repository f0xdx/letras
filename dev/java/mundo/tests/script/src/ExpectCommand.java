import java.io.IOException;

import org.mundo.rt.TypedMap;
import org.mundo.xml.XMLDeserializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class ExpectCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel=attrMap.getNamedItem("channel").getNodeValue();
    String request=attrMap.getNamedItem("request").getNodeValue();
    TypedMap map=null;
    try
    {
      map=new XMLDeserializer().deserializeMap(node);
    }
    catch(IOException x)
    {
    }
    String zone;
    try
    {
      zone=attrMap.getNamedItem("zone").getNodeValue();
    }
    catch(NullPointerException x)
    {
      zone="lan";
    }
    SubscriptionEntry se=(SubscriptionEntry)ctx.subscribers.get(channel);
    if (se==null)
    {
      se = new SubscriptionEntry(ctx, zone, channel);
      ctx.subscribers.put(channel, se);
    }
    String name=null;
    node=attrMap.getNamedItem("name");
    if (node!=null)
      name=node.getNodeValue();
    String deps=null;
    node=attrMap.getNamedItem("depends");
    if (node!=null)
      deps=node.getNodeValue();
    map.put("request", request);
    se.add(map, name, deps);
  }
}
