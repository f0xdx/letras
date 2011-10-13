import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigIntFeature extends ConfigFeature
{
  ConfigIntFeature(String k, String n, int v)
  {
    super(k, n);
    value = v;
  }
  @Override
  public JPanel getOptionsPanel(IConfigListener l)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel(name + " (Feature)"));
    field = new JTextField(Integer.toString(value));
    field.addActionListener(new TextFieldListener(l));
    panel.add(field);
    return panel;
  }
  @Override
  public String getText()
  {
    return name+": "+value;
  }
  
  class TextFieldListener implements ActionListener
  {
    TextFieldListener(IConfigListener l)
    {
      listener = l;
    }
    public void actionPerformed(ActionEvent e)
    {
      try
      {
        value = Integer.parseInt(field.getText());
      }
      catch(Exception x) {}
      listener.optionChanged();
    }
    private IConfigListener listener;
  }

  JTextField field;
  int value;
}
