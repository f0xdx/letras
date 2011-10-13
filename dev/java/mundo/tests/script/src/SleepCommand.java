import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class SleepCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    NamedNodeMap attrMap=node.getAttributes();
    try
    {
      int ms=0;
      node=attrMap.getNamedItem("ms");
      if (node!=null)
        ms=Integer.parseInt(node.getNodeValue());
      else
        ms=Integer.parseInt(attrMap.getNamedItem("secs").getNodeValue())*1000;
      Thread.sleep(ms);
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
}

