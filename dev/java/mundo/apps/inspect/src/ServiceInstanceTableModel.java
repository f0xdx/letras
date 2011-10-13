import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.mundo.service.ServiceInstance;
import org.mundo.service.ServiceManager;
import org.mundo.service.DoIServiceManager;

class ServiceInstanceTableModel extends AbstractTableModel
{
  ServiceInstanceTableModel(List<ServiceInstance> s, DoIServiceManager mgr)
  {
    services=s;
    serviceManager=mgr;
  }
  public int getRowCount()
  {
    return services.size();
  }
  public int getColumnCount()
  {
    return 3;
  }
  public String getColumnName(int column)
  {
    switch (column)
    {
      case 0:
        return "Name";
      case 1:
        return "ClassName";
      case 2:
        return "PluginName";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    ServiceInstance si=services.get(row);
    switch (column)
    {
      case 0:
        return si.instanceName;
      case 1:
        return si.className;
      case 2:
        if (si.pluginName==null)
          return "built-in";
        return si.pluginName;
    }
    return null;
  }
  ServiceInstance getEntry(int row)
  {
    return services.get(row);
  }
  private List<ServiceInstance> services;
  DoIServiceManager serviceManager;
}
