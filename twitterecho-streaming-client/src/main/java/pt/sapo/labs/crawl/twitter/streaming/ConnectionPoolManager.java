package pt.sapo.labs.crawl.twitter.streaming;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ConnectionPoolManager {

	public static final String TWITTER_USERS_LIMIT_DEFAULT  = "5000";

	private List<String[]> staticAuthenticationPool = new ArrayList<String[]>();
	private Stack<String[]> authenticationPool = new Stack<String[]>();

	public ConnectionPoolManager(String[] accounts) {
		super();
		
		for (int i = 0; i < accounts.length; i++) {
			String [] twitterAccount = accounts[i].split(",");
			authenticationPool.add(twitterAccount);
		}
	}

	public void resetPool() {
		authenticationPool.clear();
		authenticationPool.addAll(staticAuthenticationPool);
	}

	public String[] getAvailableAuthentication() {
		// TODO Auto-generated method stub
		if(authenticationPool.size() > 0){
			return authenticationPool.pop();
		}
		return null;
	}
}
