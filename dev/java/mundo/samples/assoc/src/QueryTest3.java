import java.util.List;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.service.DoIME;
import org.mundo.service.IUS;
import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;
import org.mundo.service.DoIUS;

public class QueryTest3
{
    public static void main(String[] args) throws Exception {
        Mundo.init();

        Service svc = new Service();
        svc.setServiceZone("lan");
        Mundo.registerService(svc);

        ServiceInfoFilter filter = new ServiceInfoFilter();
        filter.filterInterface("org.mundo.service.IUS");
        ResultSet rs = ServiceManager.getInstance().query(filter, null);
        Thread.sleep(1000);
        System.out.println(rs);
        printServiceName(rs);
        System.out.println("===");

        rs = ServiceManager.getInstance().query(filter, svc.getSession());
        Thread.sleep(1000);
        System.out.println(rs);
        printServiceName(rs);

        Mundo.shutdown();
    }
    public static void printServiceName(ResultSet rs)
    {
        try
        {
	      ServiceInfo si = (ServiceInfo)rs.getList().get(0);
	      DoIUS doUS = new DoIUS(si.doService);
	      System.out.println(doUS.getName());
        }
	    catch(Exception x)
	    {
		  System.out.println(x);
		}
    }
}
