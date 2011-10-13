import java.io.StringWriter;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mundo.rt.Blob;
import org.mundo.rt.Message;
import org.mundo.rt.Publisher;
import org.mundo.rt.TypedMap;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class ExecRemoteCommand extends Command
{
  public void run(Context ctx, Node node) throws Exception
  {
    NamedNodeMap attrMap=node.getAttributes();
    String channel, name, cmdline;
    try {
      name=attrMap.getNamedItem("name").getNodeValue();
    } catch(NullPointerException x) {
      throw new Exception("attribute name expected");
    }
    try {
      channel=attrMap.getNamedItem("node").getNodeValue();
    } catch(NullPointerException x) {
      throw new Exception("attribute node expected");
    }
    try {
      cmdline=ctx.expandVars(attrMap.getNamedItem("cmdline").getNodeValue());
    } catch(NullPointerException x) {
      throw new Exception("attribute cmdline expected");
    }

    String outChannelName="ts."+name+".out";
    new ExecRemoteContext(ctx, outChannelName);
    
    TypedMap map=new TypedMap();
    map.putString("request", "Run");
    map.putString("name", name);
    map.putString("cmdline", cmdline);
    map.putString("outChannel", outChannelName);
    Message msg=new Message(map);

    node=node.getFirstChild();
    while (node!=null)
    {
      if (node.getNodeType()==Node.ELEMENT_NODE)
      {
        StringWriter sw=new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(node), new StreamResult(sw));
        Blob blob = new Blob();
        blob.write(sw.toString());
        msg.put("stdin", "bin", blob);
        // FIXME: add a break here?
      }
      node=node.getNextSibling();
    }

    Publisher pub=(Publisher)ctx.publishers.get(channel);
    if (pub==null)
    {
      pub=ctx.session.publish("lan", channel);
      ctx.publishers.put(channel, pub);
    }
    pub.send(msg);
    ctx.execCnt++;
    ctx.totalCnt++;
  }
}
