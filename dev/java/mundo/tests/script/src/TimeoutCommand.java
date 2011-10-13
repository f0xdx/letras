import org.w3c.dom.Node;

class TimeoutCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    try
    {
      ctx.timeout=Integer.parseInt(node.getAttributes().getNamedItem("secs").getNodeValue())*10;
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
}

