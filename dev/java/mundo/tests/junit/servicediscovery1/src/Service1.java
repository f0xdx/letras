import org.mundo.rt.Service;

public class Service1 extends Service implements Interface1, Interface3
{
  public Service1()
  {
    setServiceInstanceName("Service1");
    setServiceZone("lan");
  }
}
