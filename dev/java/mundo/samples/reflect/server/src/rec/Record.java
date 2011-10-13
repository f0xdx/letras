package rec;

import org.mundo.annotation.mcSerialize;

@mcSerialize
public class Record
{
  public String  strField;
  public int     intField;

  public Record()
  {
  }
  public Record(String s, int i)
  {
	strField = s;
	intField = i;
  }
  @Override
  public String toString()
  {
	return "strField="+strField+",intField="+intField;
  }
}
