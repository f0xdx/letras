import org.mundo.annotation.mcRemote;


@mcRemote
public interface IFileServer {
	public void sendFile(String filename, String channel);
}
