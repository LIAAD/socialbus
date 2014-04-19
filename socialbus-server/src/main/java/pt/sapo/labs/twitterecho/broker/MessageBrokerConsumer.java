package pt.sapo.labs.twitterecho.broker;

import com.rabbitmq.client.*;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.Main;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.twitterecho.ApplicationManager;
import pt.sapo.labs.twitterecho.metadata.StatusMetadataProcessorClient;

import java.io.IOException;
import java.util.Date;

public class MessageBrokerConsumer implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(MessageBrokerConsumer.class);

	private String host;
	private int port;
	private String exchange;
	
    private boolean keepRunning = true;
	private QueueingConsumer consumer;

    private ApplicationManager applicationManager;
    private StatusMetadataProcessorClient metadataProcessor;

    private SocialechoServerSessionStats socialechoServerSessionStats;

    public MessageBrokerConsumer(ApplicationManager applicationManager, StatusMetadataProcessorClient metadataProcessor,String exchange, String host, int port) {
		super();
        this.applicationManager = applicationManager;
        this.metadataProcessor = metadataProcessor;

		this.exchange = exchange;
		this.host = host;
		this.port = port;
	}
    
    public void shutdown(){
        this.keepRunning = false;
    }

    public void setup(){

    	ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.host);
        factory.setPort(this.port);
//        factory.setPassword("guest");
//        factory.setUsername("guest");

        socialechoServerSessionStats = SocialechoServerSessionStats.getInstance();
        socialechoServerSessionStats.setRunningSince(new Date());
        socialechoServerSessionStats.setApplicationManager(this.applicationManager);

        Connection connection;
		try {
			connection = factory.newConnection();

			Channel channel = connection.createChannel();

            channel.exchangeDeclare(this.exchange, "fanout");
	        
	        String queueName = channel.queueDeclare().getQueue();
	        channel.queueBind(queueName, this.exchange, "");

	        logger.info(" [*] Waiting for messages.");

	        consumer = new QueueingConsumer(channel);
	        channel.basicConsume(queueName, true, consumer);
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Failed to open RabbitMQ connection",e);
		}
    }
	
	
	private void processMessage(String rawMessage){
		
		JSONObject jsonObj = (JSONObject) JSONValue.parse(rawMessage);

//        Process status json
        metadataProcessor.statusReceived(jsonObj);
        socialechoServerSessionStats.increaseProcessedMessagesCount();

        logger.info("extracted metadata : " + jsonObj.get("metadata").toString());

        String topic = (String)((JSONObject) jsonObj.get("metadata")).get("topic");
        String client = (String)((JSONObject) jsonObj.get("metadata")).get("client");
        String service = (String)((JSONObject) jsonObj.get("metadata")).get("service");

        socialechoServerSessionStats.addClient(client);
        socialechoServerSessionStats.addTopic(topic);
        socialechoServerSessionStats.addService(service);
        socialechoServerSessionStats.setLastProcessedMessageAt(new Date());

		for (StatusAdapter adapter : applicationManager.getAdapters()) {
            adapter.onStatus(jsonObj);
        }

        try {

            if(this.camelProducerTemplate == null){
                this.camelProducerTemplate = camelContext.getCamelTemplate();
                logger.info(String.valueOf(this.camelProducerTemplate));

            }

            this.camelProducerTemplate.sendBody("direct:foo",jsonObj);

            logger.info("camelProducerTemplate.sendBody");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
	}

    public ProducerTemplate getCamelProducerTemplate() {
        return camelProducerTemplate;
    }

    public void setCamelProducerTemplate(ProducerTemplate camelProducerTemplate) {
        this.camelProducerTemplate = camelProducerTemplate;
    }

    private ProducerTemplate camelProducerTemplate;

    public void setCamelContext(Main camelContext) {
        this.camelContext = camelContext;
    }

    // create a new Camel Main so we can easily start Camel
    private org.apache.camel.main.Main camelContext;

	@Override
	public void run() {

		while (this.keepRunning) {
			
			try {
				
				QueueingConsumer.Delivery delivery;
				delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				
				processMessage(message);
				
			} catch (ShutdownSignalException e) {

				logger.error(e.getMessage(),e);
			} catch (ConsumerCancelledException e) {

                logger.error(e.getMessage(), e);
			} catch (InterruptedException e) {

                logger.error(e.getMessage(), e);
			}
            

        }
	}
}
