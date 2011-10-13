import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.rt.Signal;

class ChatService extends Service implements IChat {
  private static final String CHANNEL_NAME = "chat_rmc";
  private static final String ZONE_NAME = "lan";
  DoIChat doChat;
	
  public ChatService() {
  }
  public void init() {
    try {
      // make this service visible for Service Discovery
      setServiceZone("lan");

      // connect channel to this object to receive chat messages
      Signal.connect(getSession().subscribe(ZONE_NAME, CHANNEL_NAME), this);

      // connect DoIChat stub to channel to send chat messages
      doChat = new DoIChat();
      Signal.connect(doChat, getSession().publish(ZONE_NAME, CHANNEL_NAME));
    }
    catch(Exception x) {
      x.printStackTrace();
    }
  }
  public void run() {
    try {
      BufferedReader r=new BufferedReader(new InputStreamReader(System.in));
      String ln;
      while ( (ln=r.readLine())!=null && !ln.equals(".") )
        doChat.chatMessage(ln, doChat.ONEWAY);
    }
    catch (Exception x) {
      x.printStackTrace();
    }
  }
  
  public void chatMessage(String msg) /*IChat*/ {
    System.out.println(msg);
  }
}

public class RMCChat {
  public static void main(String args[]) {
    Mundo.init();
    ChatService cs=new ChatService();
    Mundo.registerService(cs);
    cs.run();
    Mundo.shutdown();
  }
}
