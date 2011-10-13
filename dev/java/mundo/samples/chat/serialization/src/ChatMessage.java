import org.mundo.annotation.*;

@mcSerialize
public class ChatMessage {
  public String text;
  
  public ChatMessage() {
  }
  public ChatMessage(String t) {
    text=t;
  }
}
