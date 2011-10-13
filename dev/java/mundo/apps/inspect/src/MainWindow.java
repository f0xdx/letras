import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.net.routing.IRoutingService;

public class MainWindow extends InspectWindow
{
  MainWindow()
  {
    service=new Service();
    service.setServiceInstanceName("Inspect");
    Mundo.registerService(service);
    Signal.connect("rt", IRoutingService.IConn.class, this);

    setSize(800, 500);
    setTitle(inspect.caption);
    createMenu();
    createUI();
    createMainPane();
  }
  void createUI()
  {
    createTree();
  }
  void shutdown()
  {
    super.shutdown();
    Mundo.shutdown();
  }
  Service getService()
  {
    return service;
  }
  
  private Service service;
}
