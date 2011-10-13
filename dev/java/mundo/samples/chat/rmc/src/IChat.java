import org.mundo.annotation.mcRemote;

@mcRemote
public interface IChat {
  public void chatMessage(String msg);
}
