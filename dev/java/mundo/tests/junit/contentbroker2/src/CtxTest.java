import junit.framework.*;
import org.mundo.rt.TypedContainer;
import org.mundo.rt.TypedMap;
import org.mundo.rt.ActiveMap;
import static org.mundo.filter.IFilterConstants.*;

import org.mundo.filter.AttributeFilter;
import org.mundo.filter.IFilter;
import org.mundo.filter.ObjectAttributeFilter;
import org.mundo.filter.TypedMapFilter;
import org.mundo.filter.ActiveMapFilter;

import java.util.Map;

public class CtxTest extends TestCase
{
  private CtxItem ci1;
  private CtxItem ci2;
  private CtxItem ci3;
    
  public CtxTest()
  {
  }

  protected void setUp()
  {
    ci1 = new CtxItem();
    ci1.timestamp = 1000;
    ci1.duration  = 1;
    ci1.name      = "Context Item 1";
    ci1.object    = null;
    
    ci2 = new CtxItem();
    ci2.timestamp = 2000;
    ci2.duration  = 2;
    ci2.name      = "Context Item 2";
    ci2.object    = new ActiveMap();
    ci2.object.putInt("int1", 42);
    
    ci3 = new CtxItem();
    ci3.object = new ActiveMap();
    ci3.object.put("innerCtx", ci2);
  }

  private boolean matches(IFilter f, Object o) throws Exception
  {
    TypedMapFilter filter = f._getFilter();
    TypedMap map = (TypedMap)TypedMap.passivate(o);
    return filter.matches(map);
  }

  /**
   * A simple test that compares one attribute of the object.
   */
  public void test01() throws Exception
  {
    CtxItemFilter f = new CtxItemFilter();
    f.timestamp     = 1000;
    f._op_timestamp = OP_GREATER;

    assertFalse("test01-1", matches(f, ci1));
    assertTrue("test01-2", matches(f, ci2));
  }
  
  /**
   * This test checks the nested map for equality.
   */
  public void test02() throws Exception
  {
    ActiveMap m = new ActiveMap();
    m.putInt("int1", 42);
    CtxItemFilter f = new CtxItemFilter();
    f.object     = m;
    f._op_object = OP_EQUAL;

    assertFalse("test02-1", matches(f, ci1));
    assertTrue("test02-2", matches(f, ci2));
  }

  /**
   * The ActiveMap is compared with an ActiveMapFilter.
   */
  public void test03() throws Exception
  {
    ActiveMapFilter mf = new ActiveMapFilter();
    mf.putInt("int1", OP_EQUAL, 42);
    CtxItemFilter f = new CtxItemFilter();
    f.object     = mf;
    f._op_object = OP_FILTER;
    
    // a bit more complex filter
    ActiveMapFilter mf2 = new ActiveMapFilter();
    mf2.putMapFilter("innerCtx", mf);
    CtxItemFilter f2 = new CtxItemFilter();
    f2.object     = mf2;
    f2._op_object = OP_FILTER;

    assertFalse("test03-1", matches(f, ci1));
    assertTrue("test03-2", matches(f, ci2));
    assertTrue("test03-2", matches(f2, ci3));
  }

  /**
   * Passivation and activation of ActiveMap objects.
   */
  public void test04() throws Exception
  {
    ActiveMap am1 = new ActiveMap();
    am1.putInt("int1", 42);
    am1.putString("str1", "foobar");
    TypedMap tm1 = (TypedMap)TypedContainer.passivate(am1);
    
    TypedMap tm2 = new TypedMap();
    tm2.setActiveClassName("org.mundo.rt.ActiveMap");
    tm2.putInt("int1", 42);
    tm2.putString("str1", "foobar");
    
    ActiveMap am2 = (ActiveMap)TypedContainer.activate(tm1);

    assertTrue("test04-1", am1.size()==2);
    assertTrue("test04-2", am2.size()==2);    
    assertEquals("test04-3", am1, am2);
    assertEquals("test04-4", tm1, tm2);
  }

  /**
   * Passivation and activation of ActiveMapFilter objects.
   */
  public void test05() throws Exception
  {
    ActiveMapFilter af1 = new ActiveMapFilter();
    af1.putInt("int1", ">", 42);
    af1.putString("str1", "==", "foobar");
    
    TypedMap tm1 = (TypedMap)TypedContainer.passivate(af1);
    
    ActiveMapFilter af2 = (ActiveMapFilter)TypedContainer.activate(tm1);

    assertTrue("test05-1", af1.size()==2);
    assertTrue("test05-2", af2.size()==2);
    assertEquals("test05-3", af1, af2);
  }
  
  /**
   * 
   * Test whether copy() results in identical copy of the object 
   */
  public void test06()
  {
    ActiveMap am1 = new ActiveMap();
    am1.putInt("int1", 42);
    am1.putString("str1", "foobar");

    TypedMap tm1 = new TypedMap();
    tm1.setActiveClassName("foobar");

    assertEquals("test06-1", am1, am1.copy());
    assertEquals("test06-2", tm1, tm1.copy());
    assertEquals("test06-3", tm1, tm1.clone());
  }

  /*
   * Use inheritance
   */
  public void test07() throws Exception
  {
    SubCtxItem s = new SubCtxItem();
    s.name = "test";
    ActiveMap am = new ActiveMap();
    am.putObject("object", s);

    TypedMap tm = (TypedMap)TypedContainer.passivate(am);

    ActiveMap am2 = (ActiveMap)TypedContainer.activate(tm);

    assertEquals("test07-1", am, am2);
  }

  /**
   * Creates a TestSuite with all tests defined here.
   */
  public static Test suite()
  {
    return new TestSuite(CtxTest.class);
  }

  /**
   * Runs the TestSuite console-based.
   */
  public static void main(String args[])
  {
    junit.textui.TestRunner.run(suite());
  }
}
