package types;

import org.mundo.annotation.*;
import org.mundo.rt.Service;
import org.mundo.rt.Mundo;
import org.mundo.rt.Signal;

@mcRemote
public class ParamTypesServer extends Service
{
  @mcMethod
  public void testVoid() {}
 
  @mcMethod
  public byte testByte(byte b) { return b; }
  @mcMethod
  public char testChar(char c) { return c; }
  @mcMethod
  public short testShort(short s) { return s; }
  @mcMethod
  public int testInt(int i) { return i; }
  @mcMethod
  public long testLong(long l) { return l; }
  @mcMethod
  public float testFloat(float f) { return f; }
  @mcMethod
  public double testDouble(double d) { return d; }
  @mcMethod
  public boolean testBoolean(boolean b) { return b; }
  @mcMethod
  public String testString(String s) { return s; }

  @mcMethod
  public byte[] testByteArray(byte[] a) { return a; }
  @mcMethod
  public char[] testCharArray(char[] a) { return a; }
  @mcMethod
  public short[] testShortArray(short[] a) { return a; }
  @mcMethod
  public int[] testIntArray(int[] a) { return a; }
  @mcMethod
  public long[] testLongArray(long[] a) { return a; }
  @mcMethod
  public float[] testFloatArray(float[] a) { return a; }
  @mcMethod
  public double[] testDoubleArray(double[] a) { return a; }
  @mcMethod
  public boolean[] testBooleanArray(boolean[] a) { return a; }
  @mcMethod
  public String[] testStringArray(String[] a) { return a; }
  
  @mcMethod
  public byte[][] testByteArray2(byte[][] a) { return a; }
  @mcMethod
  public char[][] testCharArray2(char[][] a) { return a; }
  @mcMethod
  public short[][] testShortArray2(short[][] a) { return a; }
  @mcMethod
  public int[][] testIntArray2(int[][] a) { return a; }
  @mcMethod
  public long[][] testLongArray2(long[][] a) { return a; }
  @mcMethod
  public float[][] testFloatArray2(float[][] a) { return a; }
  @mcMethod
  public double[][] testDoubleArray2(double[][] a) { return a; }
  @mcMethod
  public boolean[][] testBooleanArray2(boolean[][] a) { return a; }
  @mcMethod
  public String[][] testStringArray2(String[][] a) { return a; }

  @mcMethod
  public void testException() throws Exception
  {
    throw new TestException("Test exception");
  }

  @mcMethod
  public boolean testTimeout(int t)
  {
	try
	{
	  Thread.sleep(t);
	  return true;
	}
	catch(InterruptedException x)
	{
	}
	return false;
  }

  @mcMethod
  public int testOverload(int p, int q)
  {
    return q;
  }
  @mcMethod
  public int testOverload(int p, int q, int r)
  {
    return r;
  }
  @mcMethod
  public int testOverload(int p)
  {
    return p;
  }
  @mcMethod
  public int testOverload(long p)
  {
    return (int)p*10;
  }

  @mcMethod
  public java.util.Date testDate(java.util.Date d)
  {
    return d;
  }

  @mcMethod
  public void requestShutdown()
  {
    run=false;
  }

  @Override
  public void init()
  {
    super.init();
    Signal.connect(getSession().subscribe("lan", "rmctest"), this);
  }

  public static void main(String args[])
  {
    Mundo.init();
    ParamTypesServer svc=new ParamTypesServer();
    Mundo.registerService(svc);
    try
    {
      while (run)
      {
        Thread.sleep(100);
      }
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
    Mundo.shutdown();
  }
  static boolean run=true;
}
