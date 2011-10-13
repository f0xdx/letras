import junit.framework.TestCase;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;
import org.mundo.service.ResultSet;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;

public class DiscoveryTest extends TestCase
{
  public void testAAA()
  {
    Mundo.init();
    Mundo.registerService(new Service1());
    Mundo.registerService(new Service2());
    Mundo.registerService(service = new Service());
  }
  public void testDiscover1() throws Exception
  {
    ServiceInfoFilter filter = new ServiceInfoFilter();
    filter.filterInterface("Interface3");
    ResultSet rs = ServiceManager.getInstance().query(filter, service.getSession());
    Thread.sleep(1000);
    String s="";
    for (ServiceInfo si : rs)
      s += si.instanceName+",";
    assertEquals("Service1,Service2,", s);
  }
  public void testZZZ()
  {
    Mundo.shutdown();
  }
  private static Service service;
}
