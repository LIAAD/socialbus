package pt.sapo.labs.crawl.twitter.oauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

class TwitterOAuthHelper {

	private static Logger logger = Logger.getLogger(TwitterOAuthHelper.class);

	static final String CONSUMER_KEY = "GJGALwtkuTDljV3eBDAYaQ";
	static final String CONSUMER_SECRET = "lGENO2QQVasMzalcpp6A3fAciNO1u0wA2ZihMpibyY0";
	
	public static void main(String a[]) throws TwitterException, java.io.IOException{
		// The factory instance is re-useable and thread safe.
		Twitter twitter = new TwitterFactory().getInstance();
		
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
		  logger.info("Open the following URL and grant access to your account:");
		  logger.info(requestToken.getAuthorizationURL());
		  logger.info("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
		  String pin = br.readLine();
		  try{
			 if(pin.length() > 0){
			   accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			 }else{
			   accessToken = twitter.getOAuthAccessToken();
			 }
		  } catch (TwitterException te) {
			if(401 == te.getStatusCode()){
			  logger.error("Unable to get the access token.");
			}else{
				logger.error("Erro",te);
			}
		  }
		}
		
		//persist to the accessToken for future reference.
		storeAccessToken(accessToken);
	  }
	  private static void storeAccessToken(AccessToken accessToken){
		logger.info("Token: "  + accessToken.getToken());
		logger.info ("SecretToken: "  + accessToken.getTokenSecret());
	  }	
}