import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.Signal;
import org.mundo.rt.TypedMap;
import org.mundo.reflect.GenericClientStub;
import org.mundo.reflect.MInterface;
import org.mundo.reflect.MMethod;
import org.mundo.reflect.MClass;
import org.mundo.util.DefaultApplication;

public class Client extends DefaultApplication
{
  Client()
  {
  }
  @Override
  public void init()
  {
    super.init();
  }
  @Override
  public void run()
  {
    GenericClientStub doServer = new GenericClientStub();
    Signal.connect(doServer, session.publish("lan", "samples.reflect"));

    MInterface ifc = doServer.queryInterface("IServer");
    MMethod mtd = ifc.getMethod("getRecord");
    doServer.setIgnoreClientErrors(true);
    System.out.println("calling: "+ifc.getName()+"."+mtd.getName());
    System.out.println();
    AsyncCall call = doServer.invoke(mtd, new Object[] {}, DoObject.SYNC);  
    TypedMap reply = call.getReturnValueAsMap();
    System.out.print("reply: ");
    System.out.println(reply);
    System.out.println();

    MClass mclass = doServer.queryClass(reply.getActiveClassName());
    System.out.println("reply type information:");
    System.out.println(mclass);
    System.out.println();
    
    TypedMap param = new TypedMap();
    param.put("p0", reply);
    doServer.invoke(ifc.getMethod("printRecord"), param, DoObject.SYNC.add(DoObject.PASSIVE_PARAMS));
  }
  public static void main(String args[])
  {
    start(new Client());
  }
}
