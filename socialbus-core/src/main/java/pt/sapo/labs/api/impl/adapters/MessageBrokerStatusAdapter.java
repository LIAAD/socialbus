package pt.sapo.labs.api.impl.adapters;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.internal.org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/4/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageBrokerStatusAdapter extends AbstractStatusAdapter{

    private static Logger logger = LoggerFactory.getLogger(MessageBrokerStatusAdapter.class);

        protected String host;
        protected int port;
        protected String clusterName;
        protected String routingKey = "anonymous.info";

        private String topicName;

        private Connection connection;
        private Channel channel;

        public MessageBrokerStatusAdapter() {
        }

        public void initialize(){
            logger.info("initializing message broker adapter");

            this.host = this.getConfiguration().getString("rabbitmq.host","localhost");
            this.port = this.getConfiguration().getInt("rabbitmq.port", 5672);
            this.clusterName = this.getConfiguration().getString("cluster.name", "socialbus-1");

            this.topicName = this.getConfiguration().getString("topic.name","sample");

            setupChannel();
        }

        private void setupChannel(){
            logger.info("setup channel");

            if(!this.getConfiguration().containsKey("rabbitmq.host")){
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
                logger.error("Error declaring channel",e);
            }
        }

        @Override
        public void onStatus(twitter4j.internal.org.json.JSONObject twitterJson) {

            if(!this.isEnabled()) {
                return;
            }

//            Status status = parseJsonStatus(twitterJson);

            try {
                logger.info("Sending message "+twitterJson.get("id").toString()+
                        ", topic:" + this.topicName+
                        ", broker:" + this.clusterName +
                        ", host:" + this.host);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String rawStatus = parseJsonString(twitterJson);

            JSONObject jsonObj = (JSONObject) JSONValue.parse(rawStatus);

            JSONObject metadata = buildMetadata();
            jsonObj.put("metadata", metadata);

//            unique id
            jsonObj.put("_id", jsonObj.get("id"));

            String jsonString = parseJsonString(jsonObj);

            try {
//                this.channel.basicPublish(this.clusterName, this.routingKey, null, jsonString.getBytes());

                this.channel.basicPublish(this.clusterName, this.routingKey, null, jsonString.getBytes());

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
            metadata.put("service", "twitter");

        } catch (UnknownHostException e) {

            logger.warn("can't find local host address",e);
        }

        return metadata;
    }

}
