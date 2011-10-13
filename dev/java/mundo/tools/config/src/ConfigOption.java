import java.util.ArrayList;

public class ConfigOption extends ConfigItem
{
  public ConfigOption(String k, String n, boolean b)
  {
    key = k;
    name = n;
    enabled = b;
  }
  public void addDefine(Define def)
  {
    if (defines==null)
      defines = new ArrayList<Define>();
    defines.add(def);
  }

  ArrayList<Define> defines;
  boolean enabled;
}
