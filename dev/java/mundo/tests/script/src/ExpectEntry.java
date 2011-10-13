import org.mundo.rt.Message;
import org.mundo.rt.TypedMap;

class ExpectEntry
{
  public ExpectEntry(TypedMap m, String n, String d)
  {
    map=m;
    name=n;
    received=false;
    if (d!=null)
      deps=d.split(",");
  }
  public boolean expects(Context ctx, Message msg)
  {
    if (deps!=null)
    {
      for (int i=0; i<deps.length; i++)
        if (!ctx.dependHash.containsKey(deps[i]))
          return false;
    }
    if (map.equals(msg.getMap()))
    {
      if (name!=null)
        ctx.dependHash.put(name, new Boolean(true));
      received=true;
      return true;
    }
    return false;
  }
  public String toString()
  {
    return request+map.toString();
  }
  String request;
  TypedMap map;
  String[] deps;
  String name;
  boolean received;
}
