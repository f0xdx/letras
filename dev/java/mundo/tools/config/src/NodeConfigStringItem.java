import java.io.PrintWriter;

public class NodeConfigStringItem extends NodeConfigItem
{
  NodeConfigStringItem(String k, String v)
  {
    key = k;
    value = v;
  }
  @Override
  public void writeXML(XMLWriter w)
  {
    w.tag("<"+key+">"+value+"</"+key+">");
  }
  String value;
}
