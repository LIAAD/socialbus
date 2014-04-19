package pt.sapo.labs.twitterecho.broker;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/9/13
 * Time: 3:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class WebConsoleRoute extends RouteBuilder {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(WebConsoleRoute.class);

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    private String exchangeName;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private String host;

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    private int httpPort;

    @Override
    public void configure() throws Exception {
        logger.info("setup camel webserver route");

        // setup Camel web-socket component on the httpPort we have defined
        WebsocketComponent wc = getContext().getComponent("websocket", WebsocketComponent.class);
        wc.setPort(this.httpPort);
        // we can serve static resources from the classpath: or file: system
        wc.setStaticResources("classpath:.");

		String hostname = InetAddress.getLocalHost().getHostName();
		
		String localAddress = "localhost";
		
        try {
            localAddress = java.net.InetAddress.getLocalHost().getHostAddress();
            logger.debug("localAddress : " + localAddress);
        } catch (UnknownHostException e) {
            logger.warn("can't find local host address. Use localhost",e);
        }

//        JettyHttpComponent httpComponent = getContext().getComponent("websocket", JettyHttpComponent.class);

        from("jetty:http://"+localAddress+":9191/api/sysinfo").process(new SysInfoHttpService());
        from("direct:foo").to("websocket:twitterecho?sendToAll=true");

        //                .to("log:tweet")
    }
}
