import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ConfigModule extends ConfigItem
{
  ConfigModule(String k, String n, int s)
  {
    key = k;
    name = n;
    state = s;
  }
  ConfigModule(String k, String n, String s)
  {
    key = k;
    name = n;
    setState(s);
  }
  @Override
  public int getIcon()
  {
    if (state == ENABLED)
      return ConfigCellRenderer.ICON_BULLET;
    else if (state == MODULE)
      return ConfigCellRenderer.ICON_MODULE;
    return ConfigCellRenderer.ICON_DOT;
  }
  @Override
  public JPanel getOptionsPanel(IConfigListener l)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel(name+" (Module)"));
    ButtonGroup grp = new ButtonGroup();
    JRadioButton btn = new JRadioButton("disabled");
    if (state == DISABLED)
      btn.setSelected(true);
    btn.addActionListener(new ButtonListener(l, DISABLED));
    grp.add(btn);
    panel.add(btn);
    btn = new JRadioButton("built-in");
    if (state == ENABLED)
      btn.setSelected(true);
    btn.addActionListener(new ButtonListener(l, ENABLED));
    grp.add(btn);
    panel.add(btn);
    btn = new JRadioButton("module");
    if (!external)
      btn.setEnabled(false);
    if (state == MODULE)
      btn.setSelected(true);
    btn.addActionListener(new ButtonListener(l, MODULE));
    grp.add(btn);
    panel.add(btn);
    return panel;
  }
  public void setState(String s)
  {
    if ("enabled".equals(s))
      state = ENABLED;
    else if ("module".equals(s))
      state = MODULE;
    else
      state = DISABLED;
  }
  public String getStateAsString()
  {
    if (state == ENABLED)
      return "enabled";
    else if (state == MODULE)
      return "module";
    return "disabled";
  }
  @Override
  public ArrayList<Define> getDefines()
  {
    if (state == DISABLED && fileSet.size() > 0)
    {
      ArrayList<Define> defs = new ArrayList<Define>();
      for (FileSet.Entry e : fileSet.entries)
        defs.add(new Define("src-exclude", e.name, null));
      return defs;
    }
    return null;
  }
  
  class ButtonListener implements ActionListener
  {
    ButtonListener(IConfigListener l, int c)
    {
      listener = l;
      this.c = c;
    }
    public void actionPerformed(ActionEvent e)
    {
      state = c;
      listener.optionChanged();
    }
    private IConfigListener listener;
    private int c;
  }
  
  public boolean external = false;
  public FileSet fileSet = new FileSet();
  public int state;
  public static final int DISABLED = 0;
  public static final int ENABLED = 1;
  public static final int MODULE = 2;
}
