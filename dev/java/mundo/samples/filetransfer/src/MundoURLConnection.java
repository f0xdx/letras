import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.mundo.filter.IFilterConstants;
import org.mundo.rt.Channel;
import org.mundo.rt.Session;
import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;
import org.mundo.service.ResultSet.ISignal;
import org.mundo.stream.MundoInputStream;

public class MundoURLConnection extends URLConnection {
	private Session session;
	private DoIFileServer doFileServer;
	public boolean waiting = false;

	protected MundoURLConnection(URL url, Session session) {
		super(url);
		this.session = session;
	}
	
	private void foundFileServerHost(DoIFileServer doFileServer) {
		this.doFileServer = doFileServer;
	}

	@Override
	public MundoInputStream getInputStream() throws IOException {
		String server = this.getURL().getHost();
		String file = this.getURL().getFile();
		ServiceInfoFilter sif = new ServiceInfoFilter();
		sif.instanceName = server;
		sif._op_instanceName = IFilterConstants.OP_EQUAL;
		ResultSet rs = null;
		try {
			rs = ServiceManager.getInstance().contQuery(sif, session,
					new ISignal() {
						@Override
						public void inserted(ResultSet r, int arg1, int arg2) {
							if (doFileServer == null) {
								ServiceInfo si = (ServiceInfo) r.getList().get(0);
								foundFileServerHost(new DoIFileServer(si.doService));
								synchronized (MundoURLConnection.this) {
									MundoURLConnection.this.notify();
								}
							}
						}

						@Override
						public void propChanged(ResultSet arg0, int arg1) {
							// ignore
						}

						@Override
						public void propChanging(ResultSet arg0, int arg1) {
							// ignore
						}

						@Override
						public void removed(ResultSet arg0, int arg1, int arg2) {
							// ignore
						}

						@Override
						public void removing(ResultSet arg0, int arg1, int arg2) {
							// ignore
						}
					});
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		long deadline = System.currentTimeMillis() + 10000;

		while (doFileServer == null) {
			try {
				long d = deadline - System.currentTimeMillis();
				if (d < 1)
					throw new IOException("Host " + server + " not found.");
				synchronized (this) {
					wait(d);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		rs.release();
		String channelName = UUID.randomUUID().toString();
		doFileServer.sendFile(file, channelName);
		return new MundoInputStream(new Channel("lan", channelName), session);
	}

	@Override
	public void connect() throws IOException {
	}
	
}
