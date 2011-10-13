import org.mundo.util.DefaultApplication;
import org.mundo.service.ServiceManager;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ResultSet;

public class ContQuery1 extends DefaultApplication {
  public ContQuery1() {
  }
  @Override
  public void run() {
    try {
      ServiceInfoFilter filter = new ServiceInfoFilter();
      filter.filterInterface("IChat");
      ServiceManager.getInstance().contQuery(filter, getSession(), UPDATE_HANDLER);
      pause();
    } catch(Exception x) {
      x.printStackTrace();
    }
  }
  private final ResultSet.ISignal UPDATE_HANDLER = new ResultSet.SignalAdapter() {
    @Override
    public void inserted(ResultSet rs, int i, int n) {
      System.out.println("inserted: " + rs.getList().subList(i, i+n));
    }
    @Override
    public void removing(ResultSet rs, int i, int n) {
      System.out.println("removing: " + rs.getList().subList(i, i+n));
    }
  };
  public static void main(String args[]) {
    start(new ContQuery1());
  }
}
