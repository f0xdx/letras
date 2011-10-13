import org.mundo.util.DefaultApplication;
import org.mundo.service.ServiceManager;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ResultSet;

public class Query1 extends DefaultApplication {
  public Query1() {
  }
  @Override
  public void run() {
    try {
      ServiceInfoFilter filter = new ServiceInfoFilter();
      filter.filterInterface("IChat");
      ResultSet rs = ServiceManager.getInstance().query(filter, null);
      Thread.sleep(1000);
      System.out.println(rs);
    } catch(Exception x) {
      x.printStackTrace();
    }
  }
  public static void main(String args[]) {
    start(new Query1());
  }
}
