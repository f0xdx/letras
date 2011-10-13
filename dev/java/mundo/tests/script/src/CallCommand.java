import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.Publisher;
import org.mundo.rt.Signal;
import org.mundo.rt.TypedMap;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class CallCommand extends Command
{
  public void run(Context ctx, Node node) throws Exception
  {
    NamedNodeMap attrMap = node.getAttributes();
    String channel = attrMap.getNamedItem("channel").getNodeValue();

    // read parameters
    TypedMap m = new TypedMap();
    try {
      m.putString("ptypes", attrMap.getNamedItem("ptypes").getNodeValue());
    } catch(Exception x) {
      throw new NullPointerException("ptypes expected");
    }
    try {
      m.putString("rtype", attrMap.getNamedItem("rtype").getNodeValue());
    } catch(Exception x) {
      throw new NullPointerException("rtype expected");
    }

    // FIXME: stubs should also be cached
    String ifName;
    try {
      ifName = attrMap.getNamedItem("interface").getNodeValue();
    } catch(Exception x) {
      throw new NullPointerException("interface expected");
    }
    String mtdName;
    try {
      mtdName = attrMap.getNamedItem("method").getNodeValue();
    } catch(Exception x) {
      throw new NullPointerException("method expected");
    }
    int timeout = 10000;
    try {
      timeout = Integer.parseInt(attrMap.getNamedItem("timeout").getNodeValue());
    } catch(Exception x) {
    }

    // get or create publisher
    String zone;
    try
    {
      zone = attrMap.getNamedItem("zone").getNodeValue();
    }
    catch(NullPointerException x)
    {
      zone = "lan";
    }
    Publisher pub = (Publisher)ctx.publishers.get(channel);
    if (pub==null)
    {
      pub = ctx.session.publish(zone, channel);
      ctx.publishers.put(channel, pub);
    }
    DoObject stub = new DoObject();
    stub._setTimeout(timeout);
    Signal.connect(stub, pub);

    try
    {
      AsyncCall call = new AsyncCall(stub, ifName, mtdName, m);
      call.invoke();
      call.waitForReply();
    }
    catch(Exception x)
    {
      throw new Exception("call failed; "+ifName+"."+mtdName+", "+x.toString());
    }
  }
}
