import java.io.FileInputStream;

import org.mundo.rt.Blob;
import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.TypedMap;
import org.mundo.xml.XMLDeserializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class SendMultipartCommand extends Command
{
  public void run(Context ctx, Node node) throws Exception
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel, zone;
    try {
      channel=attrMap.getNamedItem("channel").getNodeValue();
    } catch(NullPointerException x) {
      throw new Exception("channel expected");
    }
    try {
      zone=attrMap.getNamedItem("zone").getNodeValue();
    } catch(NullPointerException x) {
      zone="lan";
    }

    node=node.getFirstChild();
    while (node!=null && node.getNodeType()!=Node.ELEMENT_NODE)
      node=node.getNextSibling();
    if (node==null || !node.getNodeName().equals("message"))
      throw new Exception("<message> expected, not "+node.getNodeName());
    
    attrMap=node.getAttributes();
    String request=attrMap.getNamedItem("request").getNodeValue();

    TypedMap map=new XMLDeserializer().deserializeMap(node);
    map.put("request", request);
    Message msg=new Message(map);

    node=node.getNextSibling();
    while (node!=null)
    {
      if (node.getNodeType()==Node.ELEMENT_NODE)
      {
        Blob blob;
        attrMap=node.getAttributes();
        Node fnn=attrMap.getNamedItem("source");
        if (fnn!=null)
        {
          blob=new Blob();
          byte[] buffer=new byte[4096];
          FileInputStream fis=new FileInputStream(fnn.getNodeValue());
          int n;
          while ((n=fis.read(buffer))>0)
            blob.write(buffer, 0, n);
          fis.close();
        }
        else
        {
          blob = new Blob();
          blob.write(node.getFirstChild().getNodeValue());
        }
        msg.put(node.getNodeName(), "bin", blob);
      }
      node=node.getNextSibling();
    }

    Publisher pub=(Publisher)ctx.publishers.get(channel);
    if (pub==null)
    {
      pub = ctx.session.publish(zone, channel);
      ctx.publishers.put(channel, pub);
    }
    pub.send(msg);
  }
}
