import org.w3c.dom.Node;

class PrintCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    node=node.getFirstChild();
    System.out.println(node.getNodeValue());
  }
}

