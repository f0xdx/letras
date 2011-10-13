import org.w3c.dom.Node;

abstract class Command
{
  public abstract void run(Context ctx, Node node) throws Exception;
}
  
