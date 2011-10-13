import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;

public class Resolver
{
  public Resolver()
  {
  }
  public synchronized String getHostName(String ipaddr)
  {
    String s=map.get(ipaddr);
    if (s!=null)
      return s;
    if (pending.contains(ipaddr))
      return null;
    pending.add(ipaddr);
    new ResolvThread(ipaddr).start();
    return null;
  }
  synchronized void result(String ipaddr, String hostname)
  {
    map.put(ipaddr, hostname);
    pending.remove(ipaddr);
  }
  synchronized void failed(String ipaddr)
  {
    pending.remove(ipaddr);
  }

  class ResolvThread extends Thread
  {
    ResolvThread(String a)
    {
      ipaddr=a;
    }
    public void run()
    {
      try
      {
        result(ipaddr, InetAddress.getByName(ipaddr).getHostName());
      }
      catch(Exception x)
      {
        x.printStackTrace();
        failed(ipaddr);
      }
    }
    private String ipaddr;
  }

  HashMap<String,String> map=new HashMap<String,String>();
  HashSet<String> pending=new HashSet<String>();
}
