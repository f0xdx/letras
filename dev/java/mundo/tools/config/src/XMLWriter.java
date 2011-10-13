import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class XMLWriter
{
  public XMLWriter(File f) throws IOException
  {
    w = new PrintWriter(f);
    w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
  }
  public void close()
  {
    w.close();
  }
  public void tag(String s)
  {
    writeIndent();
    w.println(s);
  }
  public void openTag(String s)
  {
    writeIndent();
    w.print(s);
    indent++;
   }
  public void closeTag(String s)
  {
    indent--;
    writeIndent();
    w.println(s);
  }
  public void print(String s)
  {
    w.print(s);
  }
  public void println(String s)
  {
    w.println(s);
  }

  public void write(NodeConfigItem item)
  {
    item.writeXML(this);
  }

  private void writeIndent()
  {
    for (int i=0; i<indent; i++)
      w.print("  ");
  }
  
  private PrintWriter w;
  private int indent = 0;
}
