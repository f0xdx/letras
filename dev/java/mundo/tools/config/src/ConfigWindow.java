import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class ConfigWindow implements IConfigListener
{
  private static final String CAPTION = "MundoCore: Library Configuration";
  
  public ConfigWindow(ConfigItem ci)
  {
    buildConfigPanel = new BuildConfigPanel(ci);
    try {
      buildConfigPanel.loadConfig(Configure.LIB_CONFIG_XML);
      File f = new File(Configure.LIB_BUILDFILE);
      if (!f.exists())
        changed = true;
    } catch(Exception x) {
      changed = true;
    }
    
    nodeConfigPanel = new NodeConfigPanel();
  }
  
  public void setVisible(boolean b)
  {
	mainWindow.setVisible(b);
  }
  
  /**
   * Encapsulates all GUI related stuff. If this method is not called, configuration is generated in headless mode.
   */
  public void initGui()
  {
	mainWindow = new JFrame();
	  
	mainWindow.setSize(640, 480);
	mainWindow.addWindowListener(new WindowAdapter() {
	  public void windowClosing(WindowEvent ev) {
	    shutdown();
	  }
	});

	JTabbedPane tabbedPane = new JTabbedPane();
	    
	buildConfigPanel.initGui();
	nodeConfigPanel.initGui();
	    
	buildConfigPanel.setListener(this);
	tabbedPane.addTab("Build Configuration", buildConfigPanel);
	nodeConfigPanel.setListener(this);
	tabbedPane.addTab("Node Default Configuration", nodeConfigPanel);
	mainWindow.setContentPane(tabbedPane);
	    
	JMenuBar menuBar = new JMenuBar();
	JMenu menu = new JMenu("File");
	menuBar.add(menu);

	JMenuItem item;
	JMenu profileMenu = new JMenu("Load Profile");
	menu.add(profileMenu);
	try
	{
	  File dir = new File(Configure.PROFILES_DIR);
	      String[] files = dir.list();
	      if (files != null)
	      {
	        for (String fn : files)
	        {
	          if (fn.endsWith(".xml"))
	          {
	            item = new JMenuItem(fn.substring(0, fn.length()-4));
	            profileMenu.add(item);
	            item.addActionListener(new LoadProfileAction(Configure.PROFILES_DIR+"/"+fn));
	          }
	        }
	      }
	    }
	    catch(Exception x)
	    {
	      x.printStackTrace();
	    }
	    
	    item = new JMenuItem("Save");
	    item.setEnabled(changed);
	    saveItem = item;
	    item.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ev) {
	        save();
	      }
	    });
	    menu.add(item);
	    menu.add(new JSeparator());

	    item = new JMenuItem("Edit node.conf");
	    menu.add(item);
	    item.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ev) {
	        new NodeConfigWindow().setVisible(true);
	      }
	    });
	    menu.add(new JSeparator());
	    
	    item = new JMenuItem("Exit");
	    item.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ev) {
	        shutdown();
	      }
	    });
	    menu.add(item);

	    mainWindow.setJMenuBar(menuBar);
	    updateTitle();
  }
  

  private void updateTitle()
  {
    if (changed)
    	mainWindow.setTitle(CAPTION+" (changed)");
    else
    	mainWindow.setTitle(CAPTION);
  }
  
  private void setChanged(boolean b)
  {
	//we are in headless mode  
	if (saveItem == null)
      return;
	
    changed = b;
    if (changed)
      saveItem.setEnabled(true);
    else
      saveItem.setEnabled(false);
    updateTitle();
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
      buildConfigPanel.writeBuildFile();
      nodeConfigPanel.writeJava(new File(Configure.DEFAULT_CONF_JAVA));
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  void shutdown()
  {
    if (changed && JOptionPane.showConfirmDialog(null,
        "Save changes?", "MundoCore Configuration", JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION)
      save();
    mainWindow.dispose();
  }

  class LoadProfileAction implements ActionListener
  {
    LoadProfileAction(String fn)
    {
      this.fn = fn;
    }
    public void actionPerformed(ActionEvent ev)
    {
      try {
        buildConfigPanel.loadConfig(fn);
      } catch(Exception x) {
        x.printStackTrace();
      }
    }
    private String fn;
  }
  
  boolean changed = false;
  BuildConfigPanel buildConfigPanel;
  NodeConfigPanel nodeConfigPanel;
  JMenuItem saveItem;
  JFrame mainWindow;

}
