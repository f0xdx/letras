import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public abstract class AbstractConfigPanel extends JPanel
{
  protected void setDescription(String text)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><body>\n");
    if (text!=null)
      sb.append(text);
    else
      sb.append("No description available for this item.");

    int i = listBox.getSelectedIndex();
    if (i>=0)
    {
      ConfigItem ci = items.get(i);
      ArrayList<Define> defs = ci.getDefines();
      if (defs!=null)
      {
        sb.append("<h2>Effects</h2>\n");
        sb.append("<ul>\n");
        for (Define def : defs)
        {
          sb.append("<li>Define <code>"+def.target+"</code>: <code>"+def.key+"</code>");
          if (def.value!=null)
            sb.append("=<code>"+def.value+"</code>");
          sb.append("</li>\n");
        }
        sb.append("</ul>\n");
      }
    }
    sb.append("</body></html>\n");
    
    JEditorPane jEditorPane = new JEditorPane();
    jEditorPane.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(jEditorPane);
    
    HTMLEditorKit kit = new HTMLEditorKit();
    jEditorPane.setEditorKit(kit);
    
    StyleSheet styleSheet = kit.getStyleSheet();
    styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
    styleSheet.addRule("h1 {color: blue;}");
//    styleSheet.addRule("h2 {color: #ff0000;}");
    styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");

    Document doc = kit.createDefaultDocument();
    jEditorPane.setDocument(doc);
    jEditorPane.setText(sb.toString());

    rightPane.setBottomComponent(scrollPane);
  }
   
  public void setListener(IConfigListener l)
  {
    listener = l;
  }
  
  final ListSelectionListener SELECTION_LISTENER = new ListSelectionListener()
  {
    public void valueChanged(ListSelectionEvent ev)
    {
      int i = listBox.getSelectedIndex();
      ConfigItem ci = items.get(i);
      setDescription(ci.description);
      rightPane.setTopComponent(ci.getOptionsPanel(OPTION_LISTENER));
    }
  };
  
  final IConfigListener OPTION_LISTENER = new IConfigListener()
  {
    public void optionChanged()
    {
      listBox.repaint();
      int i = listBox.getSelectedIndex();
      ConfigItem ci = items.get(i);
      setDescription(ci.description);
      if (listener!=null)
        listener.optionChanged();
    }
  };
  
  protected void addItem(int indent, ConfigItem ci)
  {
    ci.indent = indent;
    items.add(ci);
    if (ci.list!=null)
    {
      for (ConfigItem child : ci.list)
        addItem(indent+1, child);
    }
  }

  private IConfigListener listener;
  protected Vector<ConfigItem> items;
  protected JList listBox;
  protected JSplitPane rightPane;
}
