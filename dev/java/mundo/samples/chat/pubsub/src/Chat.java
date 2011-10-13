import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;
import org.mundo.rt.TypedMap;

class ChatService extends Service implements IReceiver {
  private Publisher publisher;

  ChatService() {
  }
  public void init() {
    publisher = getSession().publish("lan", "chattest");
    getSession().subscribe("lan", "chattest", this);
  }
  public void run() {
    try {
      BufferedReader r=new BufferedReader(new InputStreamReader(System.in));
      String ln;
      while ( (ln=r.readLine())!=null && !ln.equals(".") ) {
        TypedMap map=new TypedMap();
        map.putString("ln", ln);
        publisher.send(new Message(map));
      }
    }
    catch (Exception x) {
      x.printStackTrace();
    }
  }
  public void received(Message msg, MessageContext ctx) {
    System.out.println(msg.getMap().getString("ln"));
  }
}

public class Chat {
  public static void main(String args[]) throws Exception {
    Mundo.init();
    ChatService cs = new ChatService();
    Mundo.registerService(cs);
    cs.run();
    Mundo.shutdown();
  }
}
