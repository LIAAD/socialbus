package pt.sapo.labs.crawl.twitter.streaming;

import com.twitter.hbc.twitter4j.v3.handler.StatusStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public abstract class BaseStreamConsumer {

	private static Logger logger = LoggerFactory.getLogger(BaseStreamConsumer.class);
	
	public static String USER_STREAM = "user.stream";
	public static String KEYWORD_STREAM = "keyword.stream";
	
	protected String streamingType;
	
	protected TwitterStream twitterStream;
	
	protected StatusStreamHandler [] adapters;
	
	protected String oAuthConsumerToken;
	protected String oAuthConsumerSecret;
	protected String oAuthToken;
	protected String oAuthSecretToken;

	public String getoAuthConsumerToken() {
		return oAuthConsumerToken;
	}

	public void setoAuthConsumerToken(String oAuthConsumerToken) {
		this.oAuthConsumerToken = oAuthConsumerToken;
	}

	public String getoAuthConsumerSecret() {
		return oAuthConsumerSecret;
	}

	public void setoAuthConsumerSecret(String oAuthConsumerSecret) {
		this.oAuthConsumerSecret = oAuthConsumerSecret;
	}

	public void setOAuthToken(String oAuthToken){
		this.oAuthToken = oAuthToken;
	}

	public void setOAuthSecretToken(String oAuthSecretToken){
		this.oAuthSecretToken = oAuthSecretToken;
	}

	protected Configuration buildConfiguration(){

		logger.info("Configuring Twitter Streaming connection...");

		ConfigurationBuilder confBuilder = new ConfigurationBuilder();
		confBuilder.setOAuthConsumerKey(this.oAuthConsumerToken);
		confBuilder.setOAuthConsumerSecret(this.oAuthConsumerSecret);

		//enable raw json
		confBuilder.setJSONStoreEnabled(true);

		logger.debug("OAUTH_ACCESS_TOKEN : " + this.oAuthToken);
		logger.debug("OAUTH_ACCESS_TOKEN_SECRET : " + this.oAuthSecretToken);

		confBuilder.setOAuthAccessToken(this.oAuthToken);
		confBuilder.setOAuthAccessTokenSecret(this.oAuthSecretToken);

		return confBuilder.build();
	}


	protected void startConsuming() throws TwitterException {
		logger.info("Open stream connection");

		Configuration config = buildConfiguration();

		twitterStream = new TwitterStreamFactory(config).getInstance();
		
		for (StatusStreamHandler statusAdapter : adapters) {
			logger.debug("setting adapter " + statusAdapter.getClass().getName());
			
			twitterStream.addListener(statusAdapter);
		}
	}

	public void start(){
		logger.debug("Restarting stream connection...");

		try {
			this.shutdown();

			logger.debug("Starting stream connection...");
			this.startConsuming();

		} catch (TwitterException e) {
			logger.error("Fail to stream data", e);
			this.shutdown();

			logger.debug("Waiting. Try after " + e.getRetryAfter()  + " miliseconds");
			try {
				Thread.sleep(e.getRetryAfter());
			} catch (InterruptedException e1) {
				logger.error("Fail to wait", e);
			}
		}		
	}

	public void shutdown(){
		logger.debug("Shutting down stream connection...");

		if(twitterStream != null){

			try{
				//shutdown internal stream consuming thread
				twitterStream.cleanUp();
				twitterStream.shutdown();

			}catch(Exception e){
				logger.error("fail on try to shutdown the stream",e);
			}

			twitterStream = null;
		}

		//delegate = null;
	}

	public void setAdapters(StatusStreamHandler[] adapters) {
		// TODO Auto-generated method stub
		this.adapters = adapters;
	}
}
