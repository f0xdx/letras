import org.mundo.annotation.*;

@mcFilter
@mcSerialize
public class Person
{
  public String   firstname;
  public String   lastname;
  public Address  address;
  
  public String toString(){
	  return "Person: " + firstname + " " + lastname + ", " + address;	 
  }
}

