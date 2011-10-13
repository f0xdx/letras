import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Vector;

import javax.swing.UIManager;

public class Configure
{
  // Description file holding information about the core library modules
  public static final String MODULES_LIB = "config/mcbuild.xml";
  // Directory with optional components
  public static final String MODULES_DIR = "com";
  public static final String PROFILES_DIR = "config/profiles";
  
  // Stores the library configuration made by the user
  public static final String LIB_CONFIG_XML = "config/mcconfig.xml";
  public static final String LIB_BUILDFILE = "build-library.xml";

  public static final String DEFAULT_CONF_JAVA = "config/DefaultConfig.java";
  
  public static void main(String args[]) throws Exception
  {
    if (args.length>1 && args[0].equals("--generate-buildfile"))
    {
      try
      {
        ConfigReader r = new ConfigReader();
        r.read(MODULES_LIB, false);
        r.scanDir(MODULES_DIR, true);
        
        BuildConfigReader bcr = new BuildConfigReader();
        bcr.read(r.getRoot(), args[1]);
        
        BuildFileWriter bfw = new BuildFileWriter();
        bfw.write(Configure.LIB_BUILDFILE, r.getRoot());
      }
      catch(Exception x)
      {
        x.printStackTrace();
      }
      return;
    }
    
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }

    if (args.length>0 && (args[0].equals("-lib-nogui") || (GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadless() && args[0].equals("-lib"))))
    {
      ConfigReader r = new ConfigReader();
      r.read(MODULES_LIB, false);
      r.scanDir(MODULES_DIR, true);
      ConfigWindow cw = new ConfigWindow(r.getRoot());
      cw.save();
      return;
    }
    
    if (args.length>0 && args[0].equals("-lib"))
    {
      ConfigReader r = new ConfigReader();
      r.read(MODULES_LIB, false);
      r.scanDir(MODULES_DIR, true);
      ConfigWindow cw = new ConfigWindow(r.getRoot());
      cw.initGui();
      cw.setVisible(true);
      return;
    }
    
    new NodeConfigWindow().setVisible(true);
  }
}
