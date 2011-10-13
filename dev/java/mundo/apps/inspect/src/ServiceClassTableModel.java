import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.mundo.service.ServiceClass;
import org.mundo.service.ServiceManager;
import org.mundo.service.DoIServiceManager;

class ServiceClassTableModel extends AbstractTableModel
{
  ServiceClassTableModel(List<ServiceClass> s, DoIServiceManager mgr)
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
    return 2;
  }
  public String getColumnName(int column)
  {
    switch (column)
    {
      case 0:
        return "ClassName";
      case 1:
        return "PluginName";
    }
    return "<null>";
  }
  public Object getValueAt(int row, int column)
  {
    ServiceClass sc=services.get(row);
    switch (column)
    {
      case 0:
        return sc.className;
      case 1:
        if (sc.pluginName==null)
          return "built-in";
        return sc.pluginName;
    }
    return null;
  }
  ServiceClass getEntry(int row)
  {
    return services.get(row);
  }
  private List<ServiceClass> services;
  DoIServiceManager serviceManager;
}
