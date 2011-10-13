import java.io.PrintWriter;

public class NodeConfigIntItem extends NodeConfigItem
{
  NodeConfigIntItem(String k, int v)
  {
    key = k;
    value = v;
  }
  @Override
  public void writeXML(XMLWriter w)
  {
    w.tag("<"+key+" xsi:type=\"xsd:int\">"+value+"</"+key+">");
  }
  int value;
}
