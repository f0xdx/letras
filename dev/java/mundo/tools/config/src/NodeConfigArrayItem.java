import java.io.PrintWriter;

public class NodeConfigArrayItem extends NodeConfigItem
{
  public NodeConfigArrayItem(String k)
  {
    key = k;
  }
  @Override
  public void writeXML(XMLWriter w)
  {
    w.openTag("<"+key+" xsi:type=\"array\"");
    if (attrs != null)
      for (String[] a : attrs)
        w.print(" " + a[0] + "=\"" + a[1] + "\"");
    w.println(">");
    for (NodeConfigItem item : children)
      item.writeXML(w);
    w.closeTag("</"+key+">");
  }
}
