package pt.sapo.labs;

import org.apache.log4j.Logger;

public class ShutdownInterceptor extends Thread {
	
	private static Logger logger = Logger.getLogger(ShutdownInterceptor.class);
	
	private IApp app;

	public ShutdownInterceptor(IApp app) {
		this.app = app;
	}

	public void run() {
		logger.debug("Calling the shutdown routine");
		app.shutDown();
	}
}
