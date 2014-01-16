package pt.sapo.labs.api.impl.adapters;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.ClassComponent;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/9/13
 * Time: 2:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class StompRoute extends RouteBuilder{
    @Override
    public void configure() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.

//        // setup Camel web-socket component on the port we have defined
//        WebsocketComponent wc = getContext().getComponent("websocket", WebsocketComponent.class);
//        wc.setPort(this.getWebSocketPort());
//        // we can serve static resources from the classpath: or file: system
//        wc.setStaticResources("classpath:.");
//
//        // setup Twitter component
//        RabbitMQComponent tc = getContext().getComponent("rabbitmq", RabbitMQComponent.class);
//
//        // poll messages for new tweets
//        fromF("rabbitmq://localhost/sample")
//                .to("log:tweet")
//                        // and push tweets to all web socket subscribers on camel-tweet
//                .to("websocket:camel-tweet?sendToAll=true");
    }
}
