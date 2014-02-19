package pt.sapo.labs.adapters;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.configuration.CompositeConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/4/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageBrokerStatusAdapter {

    private static Logger logger = LoggerFactory.getLogger(MessageBrokerStatusAdapter.class);

        protected String host;
        protected int port;
        protected String clusterName;
        protected String routingKey = "anonymous.info";

        private String topicName;

        private Connection connection;
        private Channel channel;


        private String source = "facebook";

        private CompositeConfiguration config;

        private boolean active;
        private String sourceType;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public MessageBrokerStatusAdapter(CompositeConfiguration config, String sourceType) {
            this.config = config;
            this.sourceType = sourceType;

            this.initialize();
        }

        public void initialize(){
            logger.info("initializing message broker adapter");

            this.host = this.config.getString("rabbitmq.host", "localhost");
            this.port = this.config.getInt("rabbitmq.port", 5672);
            this.clusterName = this.config.getString("cluster.name", "socialecho-1");

            this.topicName = this.config.getString("topic.name", "sample");

            setupChannel();
        }

        private void setupChannel(){
            logger.info("setup channel");

            if(!this.config.containsKey("rabbitmq.host")){
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


                this.setActive(true);
            }
            catch  (Exception e) {
                logger.error("Error declaring channel",e);

                this.setActive(false);
            }
        }

        public void onMessage(String json) {

            if(!this.isActive()) {
                return;
            }

//            Status status = parseJsonStatus(twitterJson);

            logger.info("Sending message "+json +
                    ", topic:" + this.topicName+
                    ", broker:" + this.clusterName +
                    ", host:" + this.host);

            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            JSONObject metadata = buildMetadata();
            jsonObj.put("metadata", metadata);

//            unique id
            jsonObj.put("_id", jsonObj.get("object_id"));

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
            String localAddress = InetAddress.getLocalHost().getHostAddress();
            metadata.put("client", localAddress);
            metadata.put("topic", this.topicName);
            metadata.put("service", "facebook_" + this.sourceType);

        } catch (UnknownHostException e) {

            logger.warn("can't find local host address",e);
        }

        return metadata;
    }

    protected String parseJsonString(Object statusJson){
        StringWriter out = new StringWriter();
        try {
            JSONValue.writeJSONString(statusJson, out);

            String jsonText = out.toString();

            return jsonText;

        } catch (IOException e) {
            logger.error("fail to convert JSONObject to String",e);
        }

        return null;
    }
}
