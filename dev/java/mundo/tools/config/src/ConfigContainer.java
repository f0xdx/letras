import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConfigContainer extends ConfigItem
{
  public ConfigContainer(String k, String n)
  {
    key = k;
    name = n;
  }
  public ConfigContainer add(String key, String value)
  {
    if (list==null)
      list = new ArrayList<ConfigItem>();
    ConfigContainer ci = new ConfigContainer(key, value);
    list.add(ci);
    return ci;
  }
  @Override
  public int getIcon()
  {
    return ConfigCellRenderer.ICON_DASH;
  }
  @Override
  public JPanel getOptionsPanel(IConfigListener l)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel(name+" (Container)"));
    return panel;
  }
}