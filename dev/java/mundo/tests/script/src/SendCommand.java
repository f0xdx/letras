import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.TypedMap;
import org.mundo.xml.XMLDeserializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class SendCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel=attrMap.getNamedItem("channel").getNodeValue();
    String request=attrMap.getNamedItem("request").getNodeValue();
    ctx.log.info("send to "+channel);
    String zone;
    try
    {
      zone=attrMap.getNamedItem("zone").getNodeValue();
    }
    catch(NullPointerException x)
    {
      zone="lan";
    }
    TypedMap map;
    try
    {
      map=new XMLDeserializer().deserializeMap(node);
    }
    catch(Exception x)
    {
      map=new TypedMap();
    }
    map.put("request", request);
    Message msg=new Message(map);

    Publisher pub=(Publisher)ctx.publishers.get(channel);
    if (pub==null)
    {
      pub = ctx.session.publish(zone, channel);
      ctx.publishers.put(channel, pub);
    }
    pub.send(msg);
  }
}
