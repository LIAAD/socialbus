package pt.sapo.labs.twitterecho;

import com.twitter.hbc.httpclient.ClientContext;
import org.apache.camel.main.Main;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.api.impl.adapters.MessageBrokerStatusAdapter;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.api.services.StatusMetadataHandler;
import pt.sapo.labs.core.ApplicationServiceLoader;
import pt.sapo.labs.twitterecho.broker.MessageBrokerConsumer;
import pt.sapo.labs.twitterecho.broker.SocialechoWebserver;
import pt.sapo.labs.twitterecho.metadata.StatusMetadataProcessorClient;
import pt.sapo.labs.utils.AppUtils;

import java.util.List;

public class TwitterechoServerApplication implements IApp {

	private MessageBrokerConsumer consumer;
	private Thread consumerThread;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TwitterechoServerApplication.class);

    private ApplicationManager applicationManager;

    private CompositeConfiguration config;

    public TwitterechoServerApplication(CompositeConfiguration config) {
        this.config = config;
    }

    protected ClientContext getContext(){

        ClientContext context = AppUtils.convertConfigurationToClientContext(applicationManager.getConfig());

        return context;
    }

    public ApplicationManager getApplicationManager(){
        return this.applicationManager;
    }

	@Override
	public void start() {

        applicationManager = new ApplicationManager(config);

        loadAdapters();
        loadMetadataHandlers();

        String clusterName = config.getString("cluster.name","socialecho-1");
		String host = config.getString("rabbitmq.host","localhost");
		int port = config.getInt("rabbitmq.port",5672);


        org.apache.camel.main.Main camelContext = new Main();
        // enable hangup support which mean we detect when the JVM terminates, and stop Camel graceful
        camelContext.enableHangupSupport();

        SocialechoWebserver socialechoCamelRoutes = new SocialechoWebserver(applicationManager);
        socialechoCamelRoutes.setCamelContext(camelContext);

        Thread tcamel = new Thread(socialechoCamelRoutes);
        tcamel.start();

        StatusMetadataProcessorClient metadataProcessor =
                new StatusMetadataProcessorClient(applicationManager.getMetadataHandlers());

        consumer = new MessageBrokerConsumer(applicationManager,metadataProcessor,clusterName,host,port);
        consumer.setCamelContext(camelContext);
        consumer.setup();

        Thread t = new Thread(consumer);
		t.start();



//        try{
//            ProducerTemplate template = null;
//// send to default endpoint
//            while(template == null){
//                logger.info("sending message try get context");
//                if(twitterechoWebserver.getCamelContext() == null){
//                    continue;
//                }
//                template = twitterechoWebserver.getCamelContext().getCamelTemplate();
//                while(true){
//                    logger.info("sending message");
//                    template.sendBody("direct:foo","<hello>world!</hello>");
//                }
//            }
//        }   catch(Exception e){
//             e.printStackTrace();
//        }

	}

    protected void loadAdapters() {

        List<StatusAdapter> adapters = ApplicationServiceLoader.loadAdapters(StatusAdapter.class);
        this.getApplicationManager().setAdapters(adapters);

        for (StatusAdapter adapter : adapters){
//         TODO rever this.config.getConfiguration(0);

            //MessageBrokerStatusAdapter is ignored at server.
            if(!(adapter instanceof MessageBrokerStatusAdapter)){
                logger.debug("loaded StatusAdapter  : " + adapter.getClass());
                Configuration conf = this.config;
                adapter.setConfiguration(conf);
                adapter.initialize();
            }
        }

        applicationManager.setAdapters(adapters);
    }

    protected void loadMetadataHandlers() {

        List<StatusMetadataHandler> handlers = ApplicationServiceLoader.loadAdapters(StatusMetadataHandler.class);
        this.getApplicationManager().setMetadataHandlers(handlers);

        for (StatusMetadataHandler handler : handlers){
//            TODO rever this.config.getConfiguration(0);

            logger.debug("loaded StatusMetadataHandler : " + handler.getClass());
            Configuration conf = this.config;
            handler.setConfiguration(conf);
        }

        applicationManager.setMetadataHandlers(handlers);
    }
	
//	protected void declareOptionalAdapters(MessageBrokerConsumer consumer) {
//
//		String homeDir = props.getProperty("home.dir");
//		String mongoHost = props.getProperty("mongo.host");
//		String solrAddress = props.getProperty("solr.address");
//
//		if(homeDir != null){
//			logger.info("Configuring File adapter");
//			logger.info("Home Dir : " + homeDir);
//			consumer.addAdapter(new FileOutputStatusAdapter(homeDir));
//		}
//
//		if(solrAddress != null){
//			logger.info("Configuring Apache Solr adapter");
//			logger.info("Solr Address : " + solrAddress);
//			consumer.addAdapter(new SolrStatusAdapter(solrAddress));
//		}
//
//		if(mongoHost != null){
//			logger.info("Configuring Mongo adapter");
//			int mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
//			String mongoDatabase = props.getProperty("mongo.database");
//			String mongoCollection = props.getProperty("mongo.collection");
//
//			logger.info("Mongo Host: " + mongoHost);
//
//			MongoStatusAdapter mongoAdapter =  new MongoStatusAdapter(mongoHost, mongoPort, mongoDatabase, mongoCollection);
//			consumer.addAdapter(mongoAdapter);
//		}
//	}

	public void shutDown() {
		// TODO Auto-generated method stub
		logger.info("Shutting down server");
		
		consumer.shutdown();		
		consumerThread.interrupt();
	}
}
