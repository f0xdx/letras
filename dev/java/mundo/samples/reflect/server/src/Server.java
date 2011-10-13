import org.mundo.rt.Signal;
import org.mundo.util.DefaultApplication;
import rec.Record;

public class Server extends DefaultApplication implements IServer
{
  Server()
  {
  }
  @Override
  public void init()
  {
	super.init();
    Signal.connect(session.subscribe("lan", "samples.reflect"), this);
  }
  public Record getRecord() // IServer
  {
	return new Record("foo", 42);
  }
  public void printRecord(Record r) // IServer
  {
    System.out.println(r);
  }
  public static void main(String args[])
  {
	start(new Server());
  }
}
