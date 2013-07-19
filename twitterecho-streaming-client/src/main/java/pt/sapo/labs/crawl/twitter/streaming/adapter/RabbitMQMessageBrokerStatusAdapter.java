package pt.sapo.labs.crawl.twitter.streaming.adapter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import twitter4j.Status;
import twitter4j.internal.org.json.JSONException;
import twitter4j.json.DataObjectFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class RabbitMQMessageBrokerStatusAdapter extends BaseStatusAdapter {

	protected String hostAddress;
	private final String topicName;

	private Connection connection = null;
	private Channel channel = null;

	public RabbitMQMessageBrokerStatusAdapter(String hostAddress, String topicName ) {
		// super(topic);
		this.hostAddress = hostAddress;
		this.topicName = topicName;

		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(this.hostAddress);

			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.exchangeDeclare(topicName, "topic");
			// channel.queueDeclare(topicName, "topic");
		}
		catch  (Exception e) {
			logger.error("Error declaring channel at RabbitMQ",e);
		}
		// finally {
		// 		      if (connection != null) {
		// 		        try {
		// 		          connection.close();
		// 		        }
		// 		        catch (Exception ignore) {}
		// 		      }
		// 		    }

	}

	private static Logger logger = Logger.getLogger(RabbitMQMessageBrokerStatusAdapter.class);

	@Override
	public void onStatus(Status status) {
		logger.info("Send message "+status.getId()+" to topic " + this.topicName + " at " + this.hostAddress);

		String rawStatus = DataObjectFactory.getRawJSON(status);
		String routingKey = "anonymous.info";

		JSONObject jsonObj = (JSONObject) JSONValue.parse(rawStatus);
		
		JSONObject metadata = buildMetadata();
		jsonObj.put("metadata", metadata);

		StringWriter out = new StringWriter();
		try {
			JSONValue.writeJSONString(jsonObj, out);
			String jsonText = out.toString();
			System.out.print(jsonText);
			
			this.channel.basicPublish("twitter", routingKey, null, jsonText.getBytes());
			
		} catch (IOException e) {
			logger.error("Error publishing message to server",e);
		}
	}

	private JSONObject buildMetadata() {

		JSONObject metadata = new JSONObject();

		try {
			String localAddress = java.net.InetAddress.getLocalHost().getHostAddress();
			metadata.put("client", localAddress);
			metadata.put("topic", this.topicName);

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}				

		return metadata;		
	}
}
