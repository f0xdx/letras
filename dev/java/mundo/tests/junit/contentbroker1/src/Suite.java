import junit.framework.Test;
import junit.framework.TestSuite;

public class Suite
{
  /**
   * Creates a TestSuite with all tests defined here.
   */
  public static Test suite()
  {
    return new TestSuite(FilterTest.class, SerTest.class);
  }

  /**
   * Runs the TestSuite console-based.
   */
  public static void main(String args[])
  {
    junit.textui.TestRunner.run(suite());
  }
}
