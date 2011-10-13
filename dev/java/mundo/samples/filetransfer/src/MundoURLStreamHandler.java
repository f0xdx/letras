

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;
import org.mundo.rt.Session;

public class MundoURLStreamHandler extends URLStreamHandler {
	
	private Session session;
	
	public MundoURLStreamHandler(Session session) {
		super();
		this.session = session;
	}

	@Override
	protected MundoURLConnection openConnection(URL url) throws IOException {
		return new MundoURLConnection(url, session);
	}

}
