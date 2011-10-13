import java.util.Random;

import org.mundo.annotation.mcMethod;
import org.mundo.annotation.mcRemote;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.util.DefaultApplication;

@mcRemote
public class Server extends Service {
	@mcMethod
	public int foo() {
		int i = new Random().nextInt();
		System.out.println("return " + i);
		return i;
	}

	public void init() {
		Signal.connect(getSession().subscribe("lan", "foo-channel"), this);
	}
	
}
