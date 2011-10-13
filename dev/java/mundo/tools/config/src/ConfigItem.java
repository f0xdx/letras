import java.util.ArrayList;
import javax.swing.JPanel;

public class ConfigItem
{
  public ConfigItem()
  {
  }
  public ConfigItem(String n)
  {
    name = n;
  }
  public void add(ConfigItem ci)
  {
    if (list==null)
      list = new ArrayList<ConfigItem>();
    list.add(ci);
    ci.parent = this;
  }
  public ConfigItem get(String key)
  {
    if (list==null)
      return null;
    for (ConfigItem ci : list)
      if (key.equals(ci.key))
        return ci;
    return null;
  }
  public int getIcon()
  {
    return ConfigCellRenderer.ICON_NONE;
  }
  public JPanel getOptionsPanel(IConfigListener l)
  {
    return null;
  }
  public String getText()
  {
    return name;
  }
  ArrayList<Define> getDefines()
  {
    return null;
  }
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<indent; i++)
      sb.append("  ");
    sb.append(name);
    return sb.toString();
  }
  public ConfigItem parent;
  public String key;
  public String name;
  public ArrayList<ConfigItem> list;
  public int indent;
  public String description;
}
