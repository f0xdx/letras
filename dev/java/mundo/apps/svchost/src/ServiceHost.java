import org.mundo.util.DefaultApplication;
import org.mundo.rt.Mundo;
import org.mundo.rt.Logger;
import org.mundo.rt.LogEntry;

public class ServiceHost extends DefaultApplication
{
  public static void main(String args[])
  {
    Mundo.setNodeName("svchost");
    start(null);
  }
}
