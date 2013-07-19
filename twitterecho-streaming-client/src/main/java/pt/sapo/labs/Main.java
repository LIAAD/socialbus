package pt.sapo.labs;

import java.io.File;

import org.apache.log4j.Logger;

import twitter4j.TwitterException;

public class Main{

	private static Logger logger = Logger.getLogger(Main.class);
	
	/**
	 * Main entry of this application.
	 */
	public static void main(String[] args)throws TwitterException {    	
		logger.info("Loading application...");
		
		String configFilePath = System.getProperty("config");
		logger.info("Config file path: " + configFilePath);

		if (configFilePath == null) {
			logger.info("Please inform the config file path.");
			System.exit(1);
		}
		
		File configFile = new File(configFilePath);	
		
		if(!configFile.canRead()){
			logger.error("Unable to read file " +configFilePath + ". Please inform a valid config file path");
			System.exit(1);	
		}
		
		IApp app = new TwitterStreamClientApplication(configFile);
		ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor(app);
		Runtime.getRuntime().addShutdownHook(shutdownInterceptor);
		
		logger.info("Initializing application...");
		app.start();
	}
}