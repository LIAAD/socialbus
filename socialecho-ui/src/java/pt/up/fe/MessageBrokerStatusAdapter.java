package pt.up.fe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import pt.up.fe.Scope;
import java.io.IOException;
import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.Map;
import java.net.UnknownHostException;
import twitter4j.json.DataObjectFactory;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.io.IOException;
import java.io.StringWriter;

// import org.slf4j.LoggerFactory;
import twitter4j.internal.org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;

public class MessageBrokerStatusAdapter implements StatusListener {

		protected Scope scope;

		protected Map<String, Object> configuration = new HashMap<String, Object>();

        protected String host;
        protected String port;
        protected String clusterName;
        protected String routingKey = "anonymous.info";
        protected String topicName;
		protected Boolean active;
		protected boolean enabled;

        private Connection connection;
        private Channel channel;

		private Map<String, Object> getConfiguration(){
			return this.configuration;
		}

		public boolean isEnabled() {
		        return enabled;
		    }

		    public void setEnabled(boolean enabled) {
		        this.enabled = enabled;
		    }

	    public MessageBrokerStatusAdapter(Scope scope,Map configuration){
	        this.scope = scope;
			this.configuration = configuration;
		
			initialize();
	    }

        public void initialize(){
            System.out.println("initializing message broker adapter");

            this.host = (String) this.getConfiguration().get("messagebroker.host");
            this.port = (String) this.getConfiguration().get("messagebroker.port");
            this.clusterName = (String) this.getConfiguration().get("messagebroker.clusterid");
			this.active = (Boolean) this.getConfiguration().get("messagebroker.active");
            this.topicName = this.scope.getName();

            setupChannel();
        }

        private void setupChannel(){
            System.out.println("setup channel");

            if(!this.getConfiguration().containsKey("messagebroker.host")
			|| !this.active){
                return;
            }

            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(this.host);
//                factory.setPort(this.port);

                connection = factory.newConnection();
                channel = connection.createChannel();

                //channel.exchangeDeclare(this.topicName, "topic");
                channel.exchangeDeclare(this.clusterName, "fanout");

//                channel.exchangeDeclarePassive(this.clusterName);
//                channel.queueDeclare(topicName, "topic");

                this.setEnabled(true);
            }
            catch  (Exception e) {
                System.out.println("Error declaring channel");
				e.printStackTrace();
            }
        }

        @Override
        public void onStatus(Status status) {

            if(!this.isEnabled()) {
                return;
            }

            // try {
                System.out.println("Sending message "+status.getId() +
                        ", topic:" + this.topicName+
                        ", broker:" + this.clusterName +
                        ", host:" + this.host);
            // } catch (JSONException e) {
                // e.printStackTrace();
            // }

            String jsonString = DataObjectFactory.getRawJSON(status);

            JSONObject jsonObj = (JSONObject) JSONValue.parse(jsonString);

            JSONObject metadata = buildMetadata();
            jsonObj.put("metadata", metadata);

//            unique id
            jsonObj.put("_id", jsonObj.get("id"));

            jsonString = parseJsonString(jsonObj);

            try {
//                this.channel.basicPublish(this.clusterName, this.routingKey, null, jsonString.getBytes());

                this.channel.basicPublish(this.clusterName, this.routingKey, null, jsonString.getBytes());

            } catch (IOException e) {
                System.out.println("Error publishing message to server");
				e.printStackTrace();
            }
        }
		
		protected String parseJsonString(Object statusJson){
		        StringWriter out = new StringWriter();
		        try {
		            JSONValue.writeJSONString(statusJson, out);

		            String jsonText = out.toString();

		            return jsonText;

		        } catch (IOException e) {
		            // logger.error("fail to convert JSONObject to String",e);
					 e.printStackTrace();
		        }

		        return null;
		    }

    private JSONObject buildMetadata() {

        JSONObject metadata = new JSONObject();

        try {
            String localAddress = java.net.InetAddress.getLocalHost().getHostAddress();
            metadata.put("client", localAddress);
            metadata.put("topic", this.topicName);
            metadata.put("service", "twitter");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return metadata;
    }
	
    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("deletion notice: " + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("on Track Limitation Notice: " + i);
    }

    @Override
    public void onScrubGeo(long l, long l2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onException(Exception e) {
        //To change body of implemented methods use File | Settings | File Templates.
        e.printStackTrace();
    }

}
