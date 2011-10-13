import org.mundo.annotation.*;

@mcFilter
@mcSerialize
public class Address
{
  public String  street;
  public int     zip;
  public String  city;
  
  public String toString(){
	  return "Address: " + street + ", " + zip + " " + city;	 
  }
}

