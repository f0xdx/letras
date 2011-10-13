import java.util.ArrayList;
import java.io.PrintWriter;

public class NodeConfigItem
{
  public void writeXML(XMLWriter w)
  {
  }
  public void add(NodeConfigItem item)
  {
    item.parent = this;
    children.add(item);
  }
  public void attr(String key, String value)
  {
    if (attrs == null)
      attrs = new ArrayList<String[]>();
    attrs.add(new String[] { key, value });
  }

  String key;
  NodeConfigItem parent;
  ArrayList<NodeConfigItem> children = new ArrayList<NodeConfigItem>();
  ArrayList<String[]> attrs = null;
}
