import org.mundo.annotation.*;
import org.mundo.rt.ActiveMap;

@mcFilter
@mcSerialize
public class CtxItem
{	
	public ActiveMap object;
	public long timestamp;
	public long duration;
	public long TTL = -1; //TTL in milliseconds
	public float confidence = 1.0f;
	public String contextSource = "";
	public String type = "";
	public long endTimeStamp;
	public String name = "";
	public String javaClass= "";
}
