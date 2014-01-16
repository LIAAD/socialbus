package pt.sapo.labs.twitterecho;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownInterceptor extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(ShutdownInterceptor.class);
	
	private IApp app;

	public ShutdownInterceptor(IApp app) {
		this.app = app;
	}

	public void run() {
		logger.debug("Calling the shutdown routine");
		app.shutDown();
	}
}
