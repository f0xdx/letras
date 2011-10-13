
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;
import org.mundo.util.DefaultApplication;



public class URLClient extends DefaultApplication {
	private DoIFileServer server;
	
	public void init() {
		ServiceInfoFilter sif = new ServiceInfoFilter();
		sif.filterInterface("IFileServer");
		ResultSet rs = null;
		try {
			rs = ServiceManager.getInstance().query(sif, getSession());
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (rs == null || rs.getList().size() == 0 ) {
			System.err.println("No file server found. Did you start one?");
			System.exit(0);
		}

		//store handle to the file server
		server = new DoIFileServer(((ServiceInfo) rs.getList().get(0)).doService);
	}
	
	public void run() {
		try {
			System.out.println("issue requests with \"mundocore://servername/filename.txt\"");
			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			String ln;
			while ((ln = r.readLine()) != null && !ln.equals(".")) {
				URL url = new URL(null, ln, new MundoURLStreamHandler(getSession()));
				InputStream is = url.openConnection().getInputStream();
				byte[] buffer = new byte[1024];
				int bytesRead;
				try {
					OutputStream os = new FileOutputStream("client_data" + "/" + url.getFile());
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
						System.out.print("-");
					}
					is.close();
					os.close();
					System.out.println("\ndone");
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		DefaultApplication.start(new URLClient());
	}
	
	
	
}
