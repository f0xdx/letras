import org.mundo.rt.Mundo;
import org.mundo.service.ServiceManager;
import javax.swing.UIManager;

class inspect
{
  public static void main(String args[])
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
    Mundo.setNodeName("inspect");
    Mundo.init();
    new MainWindow().run();
  }
  
  static final String caption="MundoCore Inspect 0.9.5";
}
