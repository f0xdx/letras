import java.util.HashMap;

public class ConfigBuilder
{
  public ConfigBuilder()
  {
  }
  public ConfigContainer container(String key, String name)
  {
    if (key==null)
      key = name;
    if (key==null)
      throw new IllegalArgumentException("key==null");
    ConfigContainer container = containers.get(key);
    if (container == null)
    {
      container = new ConfigContainer(key, name);
      containers.put(key, container);
      if (root==null)
        root = container;
      else
        currentContainer.add(container);
    }
    current = currentContainer = container;
    return container;
  }
  public ConfigContainer container(String name)
  {
    return container(name, name);
  }
  public ConfigModule module(String key, String name, String value)
  {
    if (key==null)
      key = name;
    if (key==null)
      throw new IllegalArgumentException("key==null");
    ConfigModule module = new ConfigModule(key, name, value);
    module.external = external;
    currentContainer.add(module);
    current = currentContainer = module;
    return module;
  }
  public ConfigModule module(String name, String value)
  {
    return module(name, name, value);
  }
  public ConfigBoolFeature feature(String key, String name, boolean b)
  {
    if (key==null)
      key = name;
    if (key==null)
      throw new IllegalArgumentException("key==null");
    ConfigBoolFeature feature = new ConfigBoolFeature(key, name, b);
    currentContainer.add(feature);
    currentFeature = feature;
    current = feature;
    currentOption = feature.getEnabledOption();
    return feature;
  }
  public ConfigBoolFeature feature(String name, boolean b)
  {
    return feature(name, name, b);
  }
  public ConfigIntFeature feature(String key, String name, int v)
  {
    if (key==null)
      key = name;
    if (key==null)
      throw new IllegalArgumentException("key==null");
    ConfigIntFeature feature = new ConfigIntFeature(key, name, v);
    currentContainer.add(feature);
    currentFeature = feature;
    current = feature;
    return feature;
  }
  public ConfigIntFeature feature(String name, int v)
  {
    return feature(name, name, v);
  }
  public ConfigFeature feature(String key, String name)
  {
    if (key==null)
      key = name;
    if (key==null)
      throw new IllegalArgumentException("key==null");
    ConfigFeature feature = new ConfigFeature(key, name);
    currentContainer.add(feature);
    currentFeature = feature;
    current = feature;
    currentOption = null;
    return feature;
  }
  public ConfigFeature feature(String name)
  {
    return feature(name, name);
  }
  public void option(String key, String name, boolean b)
  {
    if (key==null)
      key = name;
    if (key==null)
      throw new IllegalArgumentException("key==null");
    if (name==null)
      name = key;
    currentOption = currentFeature.addOption(key, name, b);
  }
  public void option(String name, boolean b)
  {
    option(name, name, b);
  }
  public void description(String text)
  {
    current.description = text;
  }
  public void define(String target, String key, String value)
  {
    currentOption.addDefine(new Define(target, key, value));
  }
  public void srcFile(String fn)
  {
    ((ConfigModule)current).fileSet.add(new FileSet.FileEntry(fn));
  }
  public void up()
  {
    current = currentContainer = current.parent;
  }
  ConfigContainer getRoot()
  {
    return root;
  }
  public void setExternal(boolean b)
  {
    external = b;
  }
  
  boolean external = false;
  ConfigContainer root = null;
  ConfigItem current;
  ConfigItem currentContainer;
  ConfigFeature currentFeature;
  ConfigOption currentOption;
  HashMap<String,ConfigContainer> containers = new HashMap<String,ConfigContainer>();
}
