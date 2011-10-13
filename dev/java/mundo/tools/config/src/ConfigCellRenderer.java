import java.awt.Component;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ConfigCellRenderer extends JLabel implements ListCellRenderer
{
  public Component getListCellRendererComponent(
      JList list,
      Object value,            // value to display
      int index,               // cell index
      boolean isSelected,      // is the cell selected
      boolean cellHasFocus)    // the list and the cell have the focus
  {
    if (value instanceof ConfigItem)
      ci = (ConfigItem)value;
    else
      ci = null;
    this.list = list;
    selected = isSelected;
    return this;
  }
  
  @Override
  public void paintComponent(Graphics g)
  {
    if (ci==null)
      return;
    int x = ci.indent*16;
    g.setFont(list.getFont());
    String text = ci.getText();
    if (text==null)
      text = "(null)";
    int w = g.getFontMetrics().stringWidth(text)+8;
    int h = g.getFontMetrics().getHeight()+2;
    g.setColor(list.getForeground());
    
    int i = ci.getIcon();
    switch (i)
    {
    case ICON_DOT:
      g.fillRect(x+4, h/2-1, 2, 2);
      break;
    case ICON_DASH:
      g.fillRect(x+2, h/2-1, 6, 2);
      break;
    case ICON_BULLET:
      g.fillOval(x, h/2-5, 10, 10);
      break;
    case ICON_MODULE:
      g.drawOval(x, h/2-5, 10, 10);
      g.drawString("M", x, 13);
    }
    
    if (selected)
    {
      g.setColor(list.getSelectionBackground());
      g.fillRect(x+12, 0, w, h);
      g.setColor(list.getSelectionForeground());
    }
    else
    {
      g.setColor(list.getForeground());
    }
    g.drawString(text, x+16, 13);
  }
  
  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(300, 20);
  }
/*  
    String s = value.toString();
    setText(s);
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    setEnabled(list.isEnabled());
    setFont(list.getFont());
    setOpaque(true);
    return this;
  }
*/
  public static final int ICON_NONE   = 0;
  public static final int ICON_DOT    = 1;
  public static final int ICON_BULLET = 2;
  public static final int ICON_DASH   = 3;
  public static final int ICON_MODULE = 4;
  
  JList list;
  ConfigItem ci;
  boolean selected;
}
