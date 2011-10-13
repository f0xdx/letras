import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ConfigFeature extends ConfigItem
{
  ConfigFeature(String k, String n)
  {
    key = k;
    name = n;
  }
  public ConfigOption addOption(String key, String name, boolean b)
  {
    ConfigOption co = new ConfigOption(key, name, b);
    options.add(co);
    return co;
  }
  public void selectKey(String key)
  {
    for (ConfigOption co : options)
      co.enabled = co.key.equals(key);
  }
  public void selectName(String name)
  {
    for (ConfigOption co : options)
      co.enabled = co.name.equals(name);
  }
  public ConfigOption getSelectedOption()
  {
    for (ConfigOption co : options)
      if (co.enabled)
        return co;
    return null;
  }
  @Override
  public String getText()
  {
    String value = "";
    for (ConfigOption co : options)
    {
      if (co.enabled)
        value = co.name;
    }
    return name+": "+value;
  }
  @Override
  public ArrayList<Define> getDefines()
  {
    for (ConfigOption co : options)
    {
      if (co.enabled)
        return co.defines;
    }
    return null;
  }
  @Override
  public JPanel getOptionsPanel(IConfigListener l)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel(name + " (Feature)"));
    ButtonGroup grp = new ButtonGroup();
    for (ConfigOption co : options)
    {
      JRadioButton btn = new JRadioButton(co.name);
      btn.setSelected(co.enabled);
      btn.addActionListener(new ButtonListener(l, co.name));
      grp.add(btn);
      panel.add(btn);
    }
    return panel;
  }

  class ButtonListener implements ActionListener
  {
    ButtonListener(IConfigListener l, String c)
    {
      listener = l;
      this.c = c;
    }
    public void actionPerformed(ActionEvent e)
    {
      selectName(c);
      listener.optionChanged();
    }
    private IConfigListener listener;
    private String c;
  }
  
  ArrayList<ConfigOption> options = new ArrayList<ConfigOption>();
}
