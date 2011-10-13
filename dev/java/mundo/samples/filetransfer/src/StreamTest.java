import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.mundo.rt.Channel;
import org.mundo.rt.Mundo;
import org.mundo.rt.Service;
import org.mundo.stream.MundoInputStream;
import org.mundo.stream.MundoOutputStream;

public class StreamTest {

	public static void main(String[] args) {
		Mundo.init();
		
		final Service svc1 = new Service();
		Mundo.registerService(svc1);

		final Service svc2 = new Service();
		Mundo.registerService(svc2);

		final Service svc3 = new Service();
		Mundo.registerService(svc3);

		
		Thread t1 = new Thread() {
			public void run() {
				MundoInputStream is = new MundoInputStream(new Channel("lan","blob-stream"), svc1.getSession());
				byte[] buffer = new byte[1024];
				int bytesRead;
				try {
					OutputStream os = new FileOutputStream("copy_remote.jpg");
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread t2 = new Thread() {
			public void run() {
				try {
					InputStream is = new URL(null, "mundocore://blob-stream", new MundoURLStreamHandler(svc3.getSession())).openConnection().getInputStream();
					byte[] buffer = new byte[1024];
					int bytesRead;
					OutputStream os = new FileOutputStream("copy_remote_url.jpg");
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Thread t3 = new Thread() {
			public void run() {
				String filename = "c:/Desert.jpg";
				FileInputStream in = null;
		        byte[] buffer = new byte[1024];
		        MundoOutputStream os = new MundoOutputStream(new Channel("lan","blob-stream"), svc2.getSession());
	        	try {
					in = new FileInputStream(filename);
		            int bytesRead = 0;
		            OutputStream local_os = new FileOutputStream("copy_local.jpg");
		            while ((bytesRead = in.read(buffer)) != -1) {
		            	local_os.write(buffer);
		            	os.write(buffer);
		            	System.out.println("[" + bytesRead + "]");
		            	try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		            }
	            	os.close();
	            	System.out.println("done");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		
		t1.start();
		t2.start();
		t3.start();
		
		try {
			t3.join();
			
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Mundo.shutdown();
	}
	
	
}
