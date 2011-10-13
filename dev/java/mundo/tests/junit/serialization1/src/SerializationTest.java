import junit.framework.TestCase;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;
import org.mundo.service.ResultSet;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.TypedContainer;
import org.mundo.rt.TypedMap;

public class SerializationTest extends TestCase
{
  public void testAAA()
  {
	Mundo.init();
  }
  public void testSerialization1() throws Exception
  {
	ClassB b = new ClassB();
	b.a = 123;
	b.b = 456;
	TypedMap m1 = (TypedMap)TypedContainer.passivate(b);
	
	TypedMap m2 = new TypedMap();
	m2.setActiveClassName("ClassB");
	m2.putInt("a", 123);
	m2.putInt("b", 456);
	assertEquals(m2, m1);
  }
  public void testZZZ()
  {
    Mundo.shutdown();
  }
  public static void main(String args[]) throws Exception
  {
	Mundo.init();
	SerializationTest t = new SerializationTest();
	t.testSerialization1();
	Mundo.shutdown();
  }
  private static Service service;
}
