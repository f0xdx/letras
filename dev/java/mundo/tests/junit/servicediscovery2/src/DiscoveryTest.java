import java.util.List;
import java.util.HashSet;
import junit.framework.TestCase;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;
import org.mundo.service.ResultSet;
import org.mundo.service.DoIServiceManager;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;

public class DiscoveryTest extends TestCase
{
  public void testAAA()
  {
    Mundo.init();
    Mundo.registerService(new Service1());
    Mundo.registerService(service = new Service());
  }
  // test if some local runtime services can be discovered
  public void testLocalDiscovery() throws Exception
  {
    ServiceInfoFilter filter = new ServiceInfoFilter();
    filter.filterZone("rt");
    ResultSet rs = ServiceManager.getInstance().query(filter, service.getSession());
    Thread.sleep(1000);
    assertTrue("services>8", rs.getList().size()>8);
  }
  // test if discovered local services can be accessed
  public void testLocalAccessiblity() throws Exception
  {
    ServiceInfoFilter filter = new ServiceInfoFilter();
    filter.filterZone("rt");
    ResultSet rs = ServiceManager.getInstance().query(filter, service.getSession());
    Thread.sleep(1000);
    assertTrue("services>8", rs.getList().size()>8);
    for (ServiceInfo si : rs)
    {
      Object obj = si.doService._getLocalObject();
      assertTrue("localObject!=null", obj!=null);
      assertTrue("localObject instanceof Service", obj instanceof Service);
    }
  }
  // test if methods on the DoObject are callable
  public void testLocalCall() throws Exception
  {
    ServiceInfoFilter filter = new ServiceInfoFilter();
    filter.filterZone("rt");
    filter.filterInterface("org.mundo.service.IServiceManager");
    ResultSet rs = ServiceManager.getInstance().query(filter, service.getSession());
    Thread.sleep(1000);
    assertEquals("services==1", 1, rs.getList().size());
    for (ServiceInfo si : rs)
    {
      DoIServiceManager svcman = new DoIServiceManager(si.doService);
      List list = svcman.getServiceInstances();
      assertTrue("list!=null", list!=null);
      assertTrue("list.size()>8", list.size()>8);
    }
  }
  // test if parent and interfaces information is correct
  public void testServiceInfo() throws Exception
  {
    ServiceInfoFilter filter = new ServiceInfoFilter();
    filter.filterZone("rt");
    ResultSet rs = ServiceManager.getInstance().query(filter, service.getSession());
    Thread.sleep(1000);
    assertTrue("services>8", rs.getList().size()>8);
    for (ServiceInfo si : rs)
    {
      HashSet<String> superClasses = new HashSet<String>();
      HashSet<String> ifcs = new HashSet<String>();
      getClassInfo(si.doService._getLocalObject().getClass(), superClasses, ifcs);
      assertEquals("superClasses", superClasses, si.superClasses.keySet());
      assertEquals("interfaces", ifcs, si.interfaces.keySet());
    }
  }  
  private void getAllInterfaces(Class ifc, HashSet<String> allIfcs)
  {
    for (Class i : ifc.getInterfaces())
    {
      allIfcs.add(i.getName());
      getAllInterfaces(i, allIfcs);
    }
  }
  private void getClassInfo(Class cls, HashSet<String> parents, HashSet<String> ifcs)
  {
    Class c = cls;
    getAllInterfaces(c, ifcs);
    c = c.getSuperclass();
    while (c != null)
    {
      parents.add(c.getName());
      getAllInterfaces(c, ifcs);
      c = c.getSuperclass();
    }
  }
  private HashSet<String> toSet(TypedMap m)
  {
    return new HashSet<String>(m.keySet());
  }
  // test if services from all zones are found
  public void testAllZones() throws Exception
  {
    ServiceInfoFilter filter = new ServiceInfoFilter();
    ResultSet rs = ServiceManager.getInstance().query(filter, service.getSession());
    Thread.sleep(1000);
    int nRt=0, nLan=0;
    for (ServiceInfo si : rs)
    {
    	if ("rt".equals(si.zone))
    	  nRt++;
    	else if ("lan".equals(si.zone))
    	{
    		assertEquals("instanceName='Service1'", "Service1", si.instanceName);
    	  nLan++;
    	}
    }
    assertTrue("nRt>8", nRt>8);
    assertEquals("nLan==1", 1, nLan);
  }

  public void testZZZ()
  {
    Mundo.shutdown();
  }

  private static Service service;
}
