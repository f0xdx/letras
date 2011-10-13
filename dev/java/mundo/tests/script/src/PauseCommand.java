import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.w3c.dom.Node;

class PauseCommand extends Command
{
  public void run(Context ctx, Node node)
  {
    try
    {
      node=node.getFirstChild();
      System.out.println(node!=null ? node.getNodeValue() : "hit enter to continue");
      new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
}

