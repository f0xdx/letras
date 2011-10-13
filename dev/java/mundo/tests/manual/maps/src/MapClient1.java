import org.mundo.rt.Blob;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.TypedMap;
import org.mundo.rt.TypedArray;
import org.mundo.rt.Publisher;
import org.mundo.rt.Subscriber;
import org.mundo.util.DefaultApplication;

class MapClient1 extends DefaultApplication implements IReceiver
{
  @Override
  public void init()
  {
    getSession().subscribe("lan", "maptest1.reply", this);
    pub = getSession().publish("lan", "maptest1.request");
  }
  public synchronized void received(Message msg, MessageContext ctx)
  {
    reply = msg;
    notify();
  }
  public void initMap1(TypedMap map)
  {
    map.putByte("Byte.MIN_VALUE", Byte.MIN_VALUE);
    map.putByte("Byte.MAX_VALUE", Byte.MAX_VALUE);
    map.putChar("Char.A", 'A');
    map.putChar("Char.FFFD", (char)0xFFFD);
    map.putShort("Short.MIN_VALUE", Short.MIN_VALUE);
    map.putShort("Short.MAX_VALUE", Short.MAX_VALUE);
    map.putInt("Integer.MIN_VALUE", Integer.MIN_VALUE);
    map.putInt("Integer.MAX_VALUE", Integer.MAX_VALUE);
    map.putLong("Long.MIN_VALUE", Long.MIN_VALUE);
    map.putLong("Long.MAX_VALUE", Long.MAX_VALUE);
    map.putFloat("Float.MIN_VALUE", Float.MIN_VALUE);
    map.putFloat("Float.MAX_VALUE", Float.MAX_VALUE);
    map.putDouble("Double.MIN_VALUE", Double.MIN_VALUE);
    map.putDouble("Double.MAX_VALUE", Double.MAX_VALUE);
    map.putBoolean("Boolean.false", false);
    map.putBoolean("Boolean.true", true);
    map.putString("String.foobar", "foobar");
    map.putString("String.null", null);    
  }
  public void initArray1(TypedArray a)
  {
    a.addByte(Byte.MIN_VALUE);
    a.addByte(Byte.MAX_VALUE);
    a.addChar('A');
    a.addChar((char)0xFFFD);
    a.addShort(Short.MIN_VALUE);
    a.addShort(Short.MAX_VALUE);
    a.addInt(Integer.MIN_VALUE);
    a.addInt(Integer.MAX_VALUE);
    a.addLong(Long.MIN_VALUE);
    a.addLong(Long.MAX_VALUE);
    a.addFloat(Float.MIN_VALUE);
    a.addFloat(Float.MAX_VALUE);
    a.addDouble(Double.MIN_VALUE);
    a.addDouble(Double.MAX_VALUE);
    a.addBoolean(false);
    a.addBoolean(true);
    a.addString("foobar");
    a.addString(null);
  }
  public void initMap2(TypedMap map)
  {
    initMap1(map);
    TypedMap subMap = new TypedMap();
    initMap1(subMap);
    map.put("map", subMap);
    TypedArray array = new TypedArray();
    initArray1(array);
    map.put("array", array);
  }
  @Override
  public void run()
  {
    try
    {
      TypedMap map = new TypedMap();
      initMap2(map);
      pub.send(new Message(map));
      synchronized(this)
      {
        while (reply==null)
          wait();
      }
      TypedMap replyMap = reply.getMap();
      if (map.equals(replyMap))
        System.out.println("SUCCESS!");
      else
      {
        System.out.println("ERROR!");
        System.out.println(map);
        System.out.println(replyMap);
      }
    }
    catch(InterruptedException x)
    {
      x.printStackTrace();
    }
  }
  public static void main(String args[])
  {
    start(new MapClient1());
  }
  private Publisher pub;
  private Message reply = null;
}
