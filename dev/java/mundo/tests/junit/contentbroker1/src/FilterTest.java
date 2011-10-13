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

public class FilterTest extends TestCase
{
  PersonFilter pf;
  AddressFilter af;
  
  public FilterTest()
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

  public void testFilter() throws Exception
  {
    Person p=new Person();
    p.firstname="Erwin";
    Address a=new Address();
    p.address=a;
    a.zip=64289;
    a.street="Hochschulstr.";

    TypedMap map=(TypedMap)TypedMap.passivate(p);
//    System.out.println(map);
    TypedMapFilter filter=pf._getFilter();
//    System.out.println(filter);
    
    assertTrue(filter.matches(map));

    a.zip=64283;
    assertFalse(filter.matches(TypedMap.passivate(p)));
  }
  
  static void printFilter(String prefix, IFilter filter) throws Exception
  {
    for (Map.Entry<String,AttributeFilter> e : filter._getFilter().entrySet())
    {
      AttributeFilter af=e.getValue();
      System.out.println("key      : "+prefix+e.getKey());
      System.out.println("operator : "+AttributeFilter.opToString(af.getOp()));
      if (af instanceof ObjectAttributeFilter)
      {
        System.out.println();
        printFilter(prefix+e.getKey()+".", ((ObjectAttributeFilter)af).getFilter());
      }
      else
      {
        System.out.println("value    : "+af.getValue());
        System.out.println();
      }
    }
  }

  /**
   * Creates a TestSuite with all tests defined here.
   */
  public static Test suite()
  {
    return new TestSuite(FilterTest.class);
  }

  /**
   * Runs the TestSuite console-based.
   */
  public static void main(String args[])
  {
    junit.textui.TestRunner.run(suite());
  }
}
