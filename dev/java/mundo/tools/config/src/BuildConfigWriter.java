import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BuildConfigWriter
{
  public void write(String fn, ConfigItem root) throws IOException
  {
    w = new PrintWriter(new FileWriter(fn));
    write(root);
    w.close();
  }
  private void write(ConfigItem ci)
  {
    if (ci instanceof ConfigContainer)
      writeBeginContainer((ConfigContainer)ci);
    else if (ci instanceof ConfigModule)
      writeBeginModule((ConfigModule)ci);
    else if (ci instanceof ConfigFeature)
      writeBeginFeature((ConfigFeature)ci);
    
    if (ci.list != null)
      for (ConfigItem si : ci.list)
        write(si);

    if (ci instanceof ConfigContainer)
      writeEndContainer();
    else if (ci instanceof ConfigModule)
      writeEndModule();
    else if (ci instanceof ConfigFeature)
      writeEndFeature();
  }
  private void writeBeginContainer(ConfigContainer c)
  {
    w.println("<container key=\""+c.key+"\">");
  }
  private void writeEndContainer()
  {
    w.println("</container>");
  }
  private void writeBeginModule(ConfigModule m)
  {
   w.println("<module key=\""+m.key+"\" value=\""+m.getStateAsString()+"\">");
  }
  private void writeEndModule()
  {
    w.println("</module>");
  }
  private void writeBeginFeature(ConfigFeature f)
  {
    w.print("<feature key=\""+f.key+"\" value=\"");
    ConfigOption co = f.getSelectedOption();
    if (co!=null)
      w.print(co.key);
    else
      w.print("null");
    w.println("\"/>");
  }
  private void writeEndFeature()
  {
  }
  private PrintWriter w;
}
