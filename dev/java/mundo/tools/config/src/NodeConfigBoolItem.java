import java.io.PrintWriter;

public class NodeConfigBoolItem extends NodeConfigItem
{
  NodeConfigBoolItem(String k, boolean v)
  {
    key = k;
    value = v;
  }
  @Override
  public void writeXML(XMLWriter w)
  {
    w.tag("<"+key+" xsi:type=\"xsd:boolean\">"+value+"</"+key+">");
  }
  boolean value;
}
