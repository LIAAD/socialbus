package pt.sapo.labs.crawl.twitter.streaming;

import pt.sapo.labs.utils.TwitterOAuthInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TokenManager {

	public static final String TWITTER_USERS_LIMIT_DEFAULT  = "5000";

	private List<TwitterOAuthInfo> staticAuthenticationPool = new ArrayList<TwitterOAuthInfo>();
	private Stack<TwitterOAuthInfo> authenticationPool = new Stack<TwitterOAuthInfo>();

	public TokenManager(List<TwitterOAuthInfo> accounts) {
		super();

        for (int i = 0; i < accounts.size(); i++) {
//            String token =  (String) accounts.get(i);
//			String [] twitterAccount = token.split("\\,", -1);

            authenticationPool.add(accounts.get(i));
		}
	}

	public void resetPool() {
		authenticationPool.clear();
		authenticationPool.addAll(staticAuthenticationPool);
	}

	public TwitterOAuthInfo getAvailableAuthentication() {
		// TODO Auto-generated method stub
		if(authenticationPool.size() > 0){
			return authenticationPool.pop();
		}
		return null;
	}
}
