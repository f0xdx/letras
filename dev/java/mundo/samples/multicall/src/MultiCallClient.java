import org.mundo.rt.AsyncCall;
import org.mundo.rt.DoObject;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;
import org.mundo.rt.TypedMap;
import org.mundo.rt.AsyncCall.IResultListener;

/** 
 * This example shows how to use an AsyncCall for broadcasting a request to all services 
 * bound to a channel and receiving more than one reply.
 */
public class MultiCallClient extends Service implements IResultListener {

	@Override
	public void resultReceived(AsyncCall c) {
		System.out.println("received: " + c.getMap());
		System.out.println("total: " + c.getNumberOfResults());
		for (Object o: c.getAllResults()) {
			TypedMap m = (TypedMap) o;
			System.out.println(m);
		}
	}
	
	public static void main(String[] args) {
		Mundo.init();
		MultiCallClient mc = new MultiCallClient();
		Mundo.registerService(mc);

		Server sv1 = new Server();
		Server sv2 = new Server();
		Mundo.registerService(sv1);
		Mundo.registerService(sv2);

		DoServer doServer = new DoServer();
		Signal.connect(doServer, mc.getSession().publish("lan", "foo-channel"));
		AsyncCall call = doServer.foo(DoServer.CREATEONLY);
		call.setResultListener(mc);

		/* remove the call from the session after two replies have been received */
		call.setExpectedReplies(2);
		
		/* optionally change the deadline for calls on the doServer stub */
		doServer._setTimeout(1000);
		
		call.invoke();

        /* wait for two replies */
		call.waitForReply(2);

		Mundo.unregisterService(sv2);
		Mundo.unregisterService(sv1);
		Mundo.unregisterService(mc);


		Mundo.shutdown();
	}
	
}
