

public class ConfigBoolFeature extends ConfigFeature
{
  ConfigBoolFeature(String k, String n, boolean b)
  {
    super(k, n);
    addOption("disabled", "disabled", !b);
    addOption("enabled", "enabled", b);
  }
  ConfigOption getEnabledOption()
  {
    return options.get(1);
  }
  boolean getEnabled()
  {
    return options.get(1).enabled;
  }
}
