package types;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.ExtProcess;

import org.mundo.annotation.*;
import org.mundo.rt.Signal;
import org.mundo.rt.Service;
import org.mundo.rt.Mundo;
import org.mundo.rt.TypedArray;
import org.mundo.rt.RMCException;

public class ParamTypesClient extends TestCase
{
  /**
   * Tests calls with base types.
   */
  public void testByte1() throws Exception
  {
    assertEquals("Byte.MIN_VALUE", stub.testByte(Byte.MIN_VALUE), Byte.MIN_VALUE);
    assertEquals("Byte.MAX_VALUE", stub.testByte(Byte.MAX_VALUE), Byte.MAX_VALUE);
  }

  public void testChar1a() throws Exception
  {
    assertEquals("Char.A", 'A', stub.testChar('A'));
  }
  public void testChar1b() throws Exception
  {
    assertEquals("Char.FFFD", 0xFFFD, stub.testChar((char)0xFFFD));
  }

  public void testShort1() throws Exception
  {
    assertEquals("Short.MIN_VALUE", stub.testShort(Short.MIN_VALUE), Short.MIN_VALUE);
    assertEquals("Short.MAX_VALUE", stub.testShort(Short.MAX_VALUE), Short.MAX_VALUE);
  }

  public void testInteger1() throws Exception
  {
    assertEquals("Integer.MIN_VALUE", stub.testInt(Integer.MIN_VALUE), Integer.MIN_VALUE);
    assertEquals("Integer.MAX_VALUE", stub.testInt(Integer.MAX_VALUE), Integer.MAX_VALUE);
  }

  public void testLong1() throws Exception
  {
    assertEquals("Long.MIN_VALUE", stub.testLong(Long.MIN_VALUE), Long.MIN_VALUE);
    assertEquals("Long.MAX_VALUE", stub.testLong(Long.MAX_VALUE), Long.MAX_VALUE);
  }

  public void testFloat1() throws Exception
  {
    assertEqualsD("Float.MIN_VALUE", stub.testFloat(Float.MIN_VALUE), Float.MIN_VALUE);
    assertEqualsD("Float.MAX_VALUE", stub.testFloat(Float.MAX_VALUE), Float.MAX_VALUE);
  }

  public void testDouble1() throws Exception
  {
    assertEqualsD("Double.MIN_VALUE", stub.testDouble(Double.MIN_VALUE), Double.MIN_VALUE);
    assertEqualsD("Double.MAX_VALUE", stub.testDouble(Double.MAX_VALUE), Double.MAX_VALUE);
  }

  public void testBoolean1() throws Exception
  {
    assertEquals("Boolean.false", stub.testBoolean(false), false);
    assertEquals("Boolean.true", stub.testBoolean(true), true);
  }

  public void testString1() throws Exception
  {
    assertEquals("String.foobar", stub.testString("foobar"), "foobar");
  }
  public void testString2() throws Exception
  {
    assertEquals("String.null", stub.testString(null), null);
  }

  /**
   * Tests calls with one-dimensional array.
   */
  public void testByteArray1() throws Exception
  {
    assertEquals("byte[].null", stub.testByteArray(null), null);
    byte[] a = { 1, 2, 3 };
    byte[] b = stub.testByteArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("byte[] identical", a==b);
    assertTrue("byte[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testCharArray1() throws Exception
  {
    assertEquals("char[].null", stub.testCharArray(null), null);
    char[] a = { 'A', 'B', 'C' };
    char[] b = stub.testCharArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("char[] identical", a==b);
    assertTrue("char[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testShortArray1() throws Exception
  {
    assertEquals("short[].null", stub.testShortArray(null), null);
    short[] a = { 1, 2, 3 };
    short[] b = stub.testShortArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("short[] identical", a==b);
    assertTrue("short[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testIntArray1() throws Exception
  {
    assertEquals("int[].null", stub.testIntArray(null), null);
    int[] a = { 1, 2, 3 };
    int[] b = stub.testIntArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("int[] identical", a==b);
    assertTrue("int[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testLongArray1() throws Exception
  {
    assertEquals("long[].null", stub.testLongArray(null), null);
    long[] a = { 1, 2, 3 };
    long[] b = stub.testLongArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("long[] identical", a==b);
    assertTrue("long[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testFloatArray1() throws Exception
  {
    assertEquals("float[].null", stub.testFloatArray(null), null);
    float[] a = { 1.2f, 3.4f, 5.6f };
    float[] b = stub.testFloatArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("float[] identical", a==b);
    assertTrue("float[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testDoubleArray1() throws Exception
  {
    assertEquals("double[].null", stub.testDoubleArray(null), null);
    double[] a = { 1.2, 3.4, 5.6 };
    double[] b = stub.testDoubleArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("double[] identical", a==b);
    assertTrue("double[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testBooleanArray1() throws Exception
  {
    assertEquals("boolean[].null", stub.testBooleanArray(null), null);
    boolean[] a = { true, false, true };
    boolean[] b = stub.testBooleanArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("boolean[] identical", a==b);
    assertTrue("boolean[] equal", TypedArray.arrayEquals(a, b));
  }

  public void testStringArray1() throws Exception
  {
    assertEquals("String[].null", stub.testStringArray(null), null);
    String[] a = { "a", "b", "c" };
    String[] b = stub.testStringArray(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("String[] identical", a==b);
    assertTrue("String[] equal", TypedArray.arrayEquals(a, b));
  }

  /**
   * Tests calls with two-dimensional array.
   */
/*
  public void testByteArray2() throws Exception
  {
    byte[][] a = new byte[2][2];
    a[0][0] = 1;
    a[0][1] = 2;
    a[1][0] = 3;
    a[1][1] = 4;
    byte[][] b = stub.testByteArray2(a);
    // If a==b, then a was just passed through the stub, without serialization/deserialization.
    assertFalse("byte[][] identical", a==b);
    assertFalse("byte[] identical", a[0]==b[0]);
    assertFalse("byte[] identical", a[1]==b[1]);
    assertTrue("byte equal", TypedArray.arrayEquals(a, b));
  }
*/
  /**
   * Test overloading
   */
/*   
  public void testOverload1() throws Exception
  {
    assertEquals(stub.testOverload(1), 1);
  }
  
  public void testOverload2() throws Exception
  {
    assertEquals(stub.testOverload(1, 2), 2);
  }
  
  public void testOverload3() throws Exception
  {
    assertEquals(stub.testOverload(1, 2, 3), 3);
  }

  public void testOverload4() throws Exception
  {
    assertEquals(stub.testOverload(1L), 10);
  }
*/
  /**
   * Test exception marshalling
   */
/*
  public void testException() throws Exception
  {
    Exception exc=null;
    String where="";
    try
    {
      stub.testException();
    }
    catch(Exception x)
    {
      exc=x;
      where=x.getStackTrace()[1].toString();
    }
    assertTrue("exception received", exc!=null);
    assertEquals("types.TestException", exc.getClass().getName());
  }
*/
  /**
   * Test timeout
   */
/*
  public void testTimeout() throws Exception
  {
	try
	{
	  System.out.println(stub.testTimeout(5000));
	}
	catch(RMCException x)
	{
	  assertEquals("Timeout waiting for RMC reply", x.getMessage());
	  // wait a bit before issuing the next test
	  Thread.sleep(2000);
	  return;
	}
	catch(Exception x)
	{
	  x.printStackTrace();
	  fail("Unexpected exception: "+x);
	}
	fail("RMCException did not occur");
  }
*/
  /**
   * Test handling of exception during ONEWAY call
   */
/*   
  public void testOnewayException() throws Exception
  {
    stub.testException(stub.ONEWAY);
  }
*/
  /**
   * Tests marshalling of standard object (Java serialization)
   */
/*
  public void testStdObject() throws Exception
  {
    java.util.Date d=new java.util.Date(123456789);
    java.util.Date d2=stub.testDate(d);
    assertEquals(d, d2);
  }
*/
  public void testZZZ() throws Exception
  {
    stub.requestShutdown(stub.ONEWAY);
  }

  /**
   * Asserts that two double values are equal.
   */
  private void assertEqualsD(String text, double a, double b)
  {
    assertTrue(text, a==b);
  }

  /**
   * Creates a TestSuite with all tests defined here.
   */
  public static Test suite()
  {
    return new TestSuite(ParamTypesClient.class);
  }

  /**
   * Runs the TestSuite console-based.
   */
  public static void main(String args[]) throws Exception
  {
    try
    {
	  ExtProcess proc = null;
//      proc = new ExtProcess("types.ParamTypesServer");
//      proc.start();

      Mundo.init();
      Service svc=new Service();
      Mundo.registerService(svc);
      stub=new DoParamTypesServer();
//      Signal.connectStrict(stub, svc.getSession().publish("lan", "rmctest"));
	  Signal.connect(stub, svc.getSession().publish("lan", "rmctest"));
      stub._setTimeout(3000);

      junit.textui.TestRunner.run(suite());
      Mundo.shutdown();

	  if (proc!=null)
        proc.join();
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
  
  private static DoParamTypesServer stub;
}
