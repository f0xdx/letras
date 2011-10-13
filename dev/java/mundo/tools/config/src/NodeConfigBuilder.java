import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class NodeConfigBuilder
{
  public NodeConfigBuilder()
  {
  }
  void map(String key)
  {
    NodeConfigItem item = new NodeConfigMapItem(key);
    if (root == null)
      root = item;
    else
      current.add(item);
    current = item;
  }
  void map(String name, String acName)
  {
    map(name);
    attr("activeClass", acName);
  }
  void array(String key)
  {
    NodeConfigItem item = new NodeConfigArrayItem(key);
    current.add(item);
    current = item;
  }
  void up()
  {
    current = current.parent;
  }
  void attr(String key, String value)
  {
    current.attr(key, value);
  }
  void string(String key, String value)
  {
    current.add(new NodeConfigStringItem(key, value));
  }
  void integer(String key, int value)
  {
    current.add(new NodeConfigIntItem(key, value));
  }
  void bool(String key, boolean value)
  {
    current.add(new NodeConfigBoolItem(key, value));
  }
  void newInstance(String className, String name)
  {
    map("new-instance");
    string("classname", className);
    string("name", name);
  }
  
  NodeConfigItem root;
  NodeConfigItem current;
}
