package pt.sapo.labs.twitterecho;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import pt.sapo.labs.twitterecho.adapters.ConsoleStatusAdapter;
import pt.sapo.labs.twitterecho.adapters.FileOutputStatusAdapter;
import pt.sapo.labs.twitterecho.adapters.MongoStatusAdapter;
import pt.sapo.labs.twitterecho.adapters.SolrStatusAdapter;
import pt.sapo.labs.twitterecho.broker.ExchangeConsumer;
import pt.sapo.labs.utils.PropertiesUtils;

public class TwitterechoServerApplication implements IApp {

	private static Logger logger = Logger.getLogger(TwitterechoServerApplication.class);

	public static Properties props;

	private File configFile;
	
	private ExchangeConsumer consumer;
	private Thread consumerThread;
	
	public TwitterechoServerApplication(File configFile) {
		super();
		this.configFile = configFile;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

		logger.info("Loading configuration...");
		props = PropertiesUtils.load(configFile);
		
		String host = props.getProperty("rabbitmq.host");
//		String port = props.getProperty("rabbitmq.port");
		
		consumer = new ExchangeConsumer("twitter",host);
		consumer.setup();
		
		consumer.addAdapter(new ConsoleStatusAdapter());
		declareOptionalAdapters(consumer);
		
		Thread t = new Thread(consumer);
		t.start();
	}
	
	protected void declareOptionalAdapters(ExchangeConsumer consumer) {
		
//		optionals
		
		String homeDir = props.getProperty("home.dir");
		String mongoHost = props.getProperty("mongo.host");
		String solrAddress = props.getProperty("solr.address");
		
		if(homeDir != null){
			logger.info("Configuring File adapter");
			logger.info("Home Dir : " + homeDir);
			consumer.addAdapter(new FileOutputStatusAdapter(homeDir));
		}
		
		if(solrAddress != null){
			logger.info("Configuring Apache Solr adapter");
			logger.info("Solr Address : " + solrAddress);
			consumer.addAdapter(new SolrStatusAdapter(solrAddress));
		}
		
		if(mongoHost != null){
			logger.info("Configuring Mongo adapter");
			int mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
			String mongoDatabase = props.getProperty("mongo.database");
			String mongoCollection = props.getProperty("mongo.collection");
			
			logger.info("Mongo Host: " + mongoHost);

			MongoStatusAdapter mongoAdapter =  new MongoStatusAdapter(mongoHost, mongoPort, mongoDatabase, mongoCollection);
			consumer.addAdapter(mongoAdapter);
		}
	}

	public void shutDown() {
		// TODO Auto-generated method stub
		logger.info("Shutting down server");
		
		consumer.shutdown();		
		consumerThread.interrupt();
	}
}
