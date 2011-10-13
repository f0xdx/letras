import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.mundo.rt.Channel;
import org.mundo.stream.MundoOutputStream;
import org.mundo.util.DefaultApplication;


public class FileServer extends DefaultApplication implements IFileServer {

	private File dataFolder;
	private String serverName;
	
	public FileServer(String serverName, File dataFolder) {
		this.dataFolder = dataFolder;
		this.serverName = serverName;
	}
	
	public void init() {
		System.out.println("starting MundoCore fileserver [" + serverName + "]. Using data folder " + dataFolder);
		setServiceZone("lan");
		setServiceInstanceName(serverName);
	}
	
	@Override
	public void sendFile(final String filename, final String channel) {
		new Thread() {
			public void run() {
				FileInputStream in = null;
		        byte[] buffer = new byte[128];
		        MundoOutputStream os = new MundoOutputStream(new Channel("lan",channel), getSession());
	        	try {
					in = new FileInputStream(dataFolder + "/" + filename);
					System.out.println("transmitting " + dataFolder + filename + " over channel " + channel);
		            int bytesRead;
		            while ((bytesRead = in.read(buffer)) != -1) {
		            	os.write(buffer);
		            }
	            	os.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			DefaultApplication.start(new FileServer("MundoCoreServer", new File("data")));
		} else {
			DefaultApplication.start(new FileServer(args[0], new File(args[1])));
		}
	}

		
	
	
	
	

}
