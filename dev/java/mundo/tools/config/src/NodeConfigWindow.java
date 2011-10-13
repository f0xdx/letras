import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NodeConfigWindow extends JFrame implements IConfigListener
{
  private static final String CAPTION = "MundoCore: node.conf.xml";

  public NodeConfigWindow()
  {
    setTitle(CAPTION);
    setSize(640, 480);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        closeWindow();
      }
    });

    nodeConfigPanel = new NodeConfigPanel();
    nodeConfigPanel.setListener(this);
    setContentPane(nodeConfigPanel);
    
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("File");
    menuBar.add(menu);
    JMenuItem item;
//    item = new JMenuItem("Open...");
//    menu.add(item);
//    item = new JMenuItem("Save");
//    menu.add(item);
    saveItem = item = new JMenuItem("Save as...");
//    item.setEnabled(false);
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        save();
      }
    });
    menu.add(item);
    
    menu.add(new JSeparator());
    item = new JMenuItem("Close");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        closeWindow();
      }
    });
    menu.add(item);
    setJMenuBar(menuBar);
  }

  private void setChanged(boolean b)
  {
    changed = b;
    if (changed)
    {
      setTitle(CAPTION+" (changed)");
      saveItem.setEnabled(true);
    }
    else
    {
      setTitle(CAPTION);
      saveItem.setEnabled(false);
    }
  }
  
  public void optionChanged() // IConfigListener
  {
    setChanged(true);
  }

  void save()
  {
    setChanged(false);
    try
    {
      JFileChooser fc = new JFileChooser();
      String dir = new File(".").getCanonicalPath();
      fc.setSelectedFile(new File(dir+"/node.conf.xml"));
      if (fc.showSaveDialog(this) == fc.APPROVE_OPTION)
        nodeConfigPanel.writeXML(fc.getSelectedFile());
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  void closeWindow()
  {
    if (changed && JOptionPane.showConfirmDialog(null,
        "Save changes?", "MundoCore Configuration", JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION)
      save();
    dispose();
  }

  public static void main(String args[]) throws Exception
  {
    NodeConfigWindow wnd = new NodeConfigWindow();
    wnd.setVisible(true);
  }

  boolean changed = true;
  NodeConfigPanel nodeConfigPanel;
  JMenuItem saveItem;
}
