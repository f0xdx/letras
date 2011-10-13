import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Mundo;
import org.mundo.rt.Publisher;
import org.mundo.rt.Service;

class ChatService extends Service implements IReceiver {
  private Publisher publisher;

  ChatService() {
  }
  public void init() {
    publisher = getSession().publish("lan", "serchattest");
    getSession().subscribe("lan", "serchattest", this);
  }
  public void run() {
    try {
      BufferedReader r=new BufferedReader(new InputStreamReader(System.in));
      String ln;
      while ( (ln=r.readLine())!=null && !ln.equals(".") )
        publisher.send(Message.fromObject(new ChatMessage(ln)));
    }
    catch(Exception x) {
      x.printStackTrace();
    }
  }
  public void received(Message msg, MessageContext ctx) {
    try {
      System.out.println(((ChatMessage)msg.getObject()).text);
    }
    catch(Exception x) {
      x.printStackTrace();
    }
  }
}

class SerChat {
  public static void main(String args[]) throws Exception {
    Mundo.init();
    ChatService cs = new ChatService();
    Mundo.registerService(cs);
    cs.run();
    Mundo.shutdown();
  }
}
