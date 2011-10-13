import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mundo.rt.TypedMap;
import static org.mundo.filter.IFilterConstants.*;
import org.mundo.filter.AttributeFilter;
import org.mundo.filter.IFilter;
import org.mundo.filter.ObjectAttributeFilter;
import org.mundo.filter.TypedMapFilter;
import java.util.Map;

public class SerTest extends TestCase
{
  PersonFilter pf;
  AddressFilter af;
  
  public SerTest()
  {
  }

  protected void setUp()
  {
    pf=new PersonFilter();
    pf.firstname="Erwin";
    pf._op_firstname=OP_EQUAL;

    af=new AddressFilter();
    pf.address=af;
    pf._op_address=OP_FILTER;
    af.zip=64289;
    af._op_zip=OP_EQUAL;
    af.street="schul";
    af._op_street=OP_CONTAINS;

//    printFilter("", pf);
  }

  public void testSer() throws Exception
  {
    String s1 = pf.toString();

    TypedMap p1 = (TypedMap)TypedMap.passivate(pf);
    PersonFilter pf2 = (PersonFilter)TypedMap.activate(p1);

    String s2 = pf2.toString();
    assertEquals(s1, s2);
  }

//  public void testThatFails()
//  {
    // check that build continues when test fails
//    assertEquals(true, false);
//  }
  
  /**
   * Creates a TestSuite with all tests defined here.
   */
  public static Test suite()
  {
    return new TestSuite(SerTest.class);
  }

  /**
   * Runs the TestSuite console-based.
   */
  public static void main(String args[])
  {
    junit.textui.TestRunner.run(suite());
  }
}
