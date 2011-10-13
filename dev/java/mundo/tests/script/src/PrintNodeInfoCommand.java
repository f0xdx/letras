import org.mundo.rt.Mundo;
import org.mundo.rt.TypedMap;
import org.mundo.service.DebugService;
import org.mundo.xml.XMLFormatter;
import org.mundo.xml.XMLSerializer;
import org.w3c.dom.Node;

class PrintNodeInfoCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    try
    {
      DebugService ds=(DebugService)Mundo.getServiceByType(DebugService.class);
      System.out.println(XMLFormatter.format(new XMLSerializer().serializeMap("NodeInfo", (TypedMap)TypedMap.passivate(ds.getNodeInfo()))));
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
}

