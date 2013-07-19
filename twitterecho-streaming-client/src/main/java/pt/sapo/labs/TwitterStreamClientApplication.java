package pt.sapo.labs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import pt.sapo.labs.crawl.twitter.ApplicationManager;
import pt.sapo.labs.crawl.twitter.streaming.BaseStreamConsumer;
import pt.sapo.labs.crawl.twitter.streaming.ConnectionPoolManager;
import pt.sapo.labs.crawl.twitter.streaming.KeywordStreamConsumer;
import pt.sapo.labs.crawl.twitter.streaming.UserStreamConsumer;
import pt.sapo.labs.crawl.twitter.streaming.adapter.CSVStatusAdapter;
import pt.sapo.labs.crawl.twitter.streaming.adapter.ConsoleStatusAdapter;
import pt.sapo.labs.crawl.twitter.streaming.adapter.FileOutputStatusAdapter;
import pt.sapo.labs.utils.CollectionUtils;
import pt.sapo.labs.utils.PropertiesUtils;
import twitter4j.StatusAdapter;
import pt.sapo.labs.crawl.twitter.streaming.adapter.RabbitMQMessageBrokerStatusAdapter;

public class TwitterStreamClientApplication implements IApp {

	private static Logger logger = Logger.getLogger(TwitterStreamClientApplication.class);

	public static ApplicationManager appManager;
	private static ConnectionPoolManager connectionPool;
	private static List<BaseStreamConsumer> consumers;

	private File configFile;

	public TwitterStreamClientApplication(File configFile) {
		super();
		this.configFile = configFile;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

		logger.info("Loading configuration...");
		Properties props = PropertiesUtils.load(configFile);
		appManager = new ApplicationManager(props);

		logger.info("Loading twitter accounts");
		String [] twitterAccounts = appManager.getTwitterAccounts();
		logger.debug("twitter accounts : " + twitterAccounts );

		logger.info("Loading connection pool");
		connectionPool = new ConnectionPoolManager(twitterAccounts);

		consumers = new ArrayList<BaseStreamConsumer>();

		
		StatusAdapter[] adapters = createAdapters();
		
		if(appManager.getMonitoredKeywords() != null){
			startKeywordStream(adapters);
		}else{
			startUserStream(adapters);
		}
	}

	protected StatusAdapter[] createAdapters() {
		String brokerAddress = appManager.getProperty("rabbitmq.host");
		String homeDir = appManager.getProperty("home.dir");
		String topicName = appManager.getProperty("topic.name");
		
		logger.info("Topic name : " + topicName);
		logger.info("Broker Address : " + brokerAddress);		
		logger.info("Home Dir : " + homeDir);
		
		List<StatusAdapter> adaptersList = new ArrayList<StatusAdapter>();
		adaptersList.add(new ConsoleStatusAdapter());
		
		if(brokerAddress != null){
			adaptersList.add(new RabbitMQMessageBrokerStatusAdapter(brokerAddress,topicName));
		}
		
		if(homeDir != null){
			adaptersList.add(new FileOutputStatusAdapter(homeDir,topicName));
		}
		
		return adaptersList.toArray(new StatusAdapter[adaptersList.size()]);
	}

	private void startKeywordStream(StatusAdapter[] adapters) {
		// TODO Auto-generated method stub
		String trackingKeywords = appManager.getMonitoredKeywords();
		
		logger.info("Tracking words : " + trackingKeywords);
		
		String [] authenticationData = connectionPool.getAvailableAuthentication();
		
		if(authenticationData!=null){
			String oAuthToken = authenticationData[0];
			String oAuthSecretToken = authenticationData[1];

			BaseStreamConsumer stream = new KeywordStreamConsumer(trackingKeywords.split(","));
			stream.setAdapters(adapters);
			
			consumers.add(stream);

			stream.setoAuthConsumerToken(appManager.getOAuthConsumerKey());
			stream.setoAuthConsumerSecret(appManager.getOAuthConsumerSecret());
			stream.setOAuthToken(oAuthToken);
			stream.setOAuthSecretToken(oAuthSecretToken);

			stream.start();	
		}else{
			logger.error("No twitter accounts available to open a new connection");
		}
	}
	
	
	private void startUserStream(StatusAdapter [] adapter) {
		// TODO Auto-generated method stub
		int maxSupportedStreamingUsers =  appManager.getMaxSupportedUsers();
		
		logger.info("Max Supported Streaming Users : " + maxSupportedStreamingUsers);
		
		try {
			long[] streamingUsers = appManager.loadStreamingUsers(maxSupportedStreamingUsers);
			startStreams(streamingUsers, adapter);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Fail to load file containing user ids",e);
			
			System.exit(1);
		}
	}

	private void startStreams(long[] streamingUsers, StatusAdapter [] adapters) {
		List<long[]> distributedUserIdsList = CollectionUtils.distributeUsersId(streamingUsers);

		for (long[] userIdsBlock : distributedUserIdsList) {
			String [] authenticationData = connectionPool.getAvailableAuthentication();

			if(authenticationData!=null){
				String oAuthToken = authenticationData[0];
				String oAuthSecretToken = authenticationData[1];

				BaseStreamConsumer stream = new UserStreamConsumer(userIdsBlock);
				stream.setAdapters(adapters);
				
				consumers.add(stream);

				stream.setoAuthConsumerToken(appManager.getOAuthConsumerKey());
				stream.setoAuthConsumerSecret(appManager.getOAuthConsumerSecret());
				stream.setOAuthToken(oAuthToken);
				stream.setOAuthSecretToken(oAuthSecretToken);

				stream.start();	
			}else{
				logger.error("No twitter accounts available to open a new connection");
			}
		}
	}

	@Override
	public void shutDown() {
		// TODO Auto-generated method stub
		logger.info("Shutting down open streams");

		for (BaseStreamConsumer consumer : consumers) {
			consumer.shutdown();
		}
	}
}
