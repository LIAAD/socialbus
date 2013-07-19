package pt.sapo.labs.twitterecho.broker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import pt.sapo.labs.twitterecho.adapters.BaseStatusAdapter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class ExchangeConsumer implements Runnable{
	
//	private static final String EXCHANGE_NAME = "twitterecho";
	private static Logger logger = Logger.getLogger(ExchangeConsumer.class);

	private String host;
	private int port;
	private String exchange;
	
	private List<BaseStatusAdapter> adapters;
	
	private boolean keepRunning = true;
	
//	
	private QueueingConsumer consumer;
    
	public void shutdown(){
		this.keepRunning = false; 
	}
	
    public ExchangeConsumer(String exchange, String host, int port) {
		super();
		this.exchange = exchange;
		this.host = host;
		this.port = port;
		
		adapters = new ArrayList<BaseStatusAdapter>();
	}
    
    public ExchangeConsumer(String exchange, String host) {
		this(exchange,host,5672);
	}
    
    public void addAdapter(BaseStatusAdapter adapter){
    	this.adapters.add(adapter);
    }

	public void setup(){
    	ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.host);
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
			e.printStackTrace();
		}
        
    }
	
	
	private void processMessage(String rawMessage){
		
		JSONObject jsonObj = (JSONObject) JSONValue.parse(rawMessage);
		
//		JSONObject metadata = (JSONObject) jsonObj.get("metadata");
//		System.out.println(" [x] Received metadata " + metadata);
//		System.out.println(" [x] Tweet id " + jsonObj.get("id"));
		
		for (BaseStatusAdapter adapter : adapters) {
			jsonObj = adapter.processMetadata(jsonObj);
			adapter.onStatus(jsonObj);
		}			
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (this.keepRunning) {
			
			try {
				
				QueueingConsumer.Delivery delivery;
				delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				
				processMessage(message);
				
			} catch (ShutdownSignalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConsumerCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            

        }
	}
}
