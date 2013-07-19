package pt.sapo.labs.crawl.twitter;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ApplicationManager {

	private static Logger logger = Logger.getLogger(ApplicationManager.class);	
	
	public static int NUMBER_OF_USERS_PER_CONNECTION = 5000;
	
	private Properties properties;
	private String[] accounts;
	
	public ApplicationManager(Properties props) {
		this.properties = props;
	}
	
	public String getProperty(String name){
		if(properties == null) return null;
		
		return (String) properties.get(name);
	}
	
	public String getHomeDir(){
		return (String) properties.get("home.dir");
	}
	
	public String getMonitoredKeywords(){
		return (String) properties.get("tracking.keywords");
	}
	
	public String getMonitoredUsersFilePath(){
		return (String) properties.get("tracking.users.file");
	}
	
	public String getServerLookupEndPoint(){
		return (String) properties.get("server.lookup.end.point");
	}
	
	public String getOAuthConsumerKey(){
		return (String) properties.get("application.consumer.key");
	}
	
	public String getOAuthConsumerSecret(){
		return (String) properties.get("application.consumer.secret");
	}
	
	public String[] getTwitterAccounts(){
		String twitterAccounts = (String) properties.get("twitter.tokens");
		logger.debug("twitterAccounts : " +twitterAccounts);
		String[] accounts = twitterAccounts.split(";");
		
		return accounts;
	}

	public int getMaxSupportedUsers() {
		// TODO Auto-generated method stub
		if(accounts == null) 
			accounts = getTwitterAccounts();
		
		logger.debug("accounts.length : " +accounts.length);
		return accounts.length * NUMBER_OF_USERS_PER_CONNECTION;
	}
	
	public long [] loadStreamingUsers(int maxSupportedStreamingUsers) throws IOException{
		logger.debug("Loading user ids");
		
		long [] userIds = new long[maxSupportedStreamingUsers];
		
		String filePathUsersId = this.getMonitoredUsersFilePath();
		logger.debug(" maxSupportedStreamingUsers : " + maxSupportedStreamingUsers);
		logger.debug(" monitored users file path : " + filePathUsersId);
		if(filePathUsersId == null) {
			logger.error("Please inform 'file.users' in the config file");
			
			return null;
		}
		
		File file = new File(filePathUsersId);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			int lineIndex = 0;
			while (dis.available() != 0) {
				if(lineIndex > maxSupportedStreamingUsers) break;

				// this statement reads the line from the file and print it to
				// the console.
				Long uid = Long.parseLong(dis.readLine());
//				logger.debug(lineIndex +  " : "+ uid);
				userIds[lineIndex++] = uid ;
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();


		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

		return userIds;
	}
}
