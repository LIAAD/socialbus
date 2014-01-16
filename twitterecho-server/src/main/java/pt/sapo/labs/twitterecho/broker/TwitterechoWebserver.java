package pt.sapo.labs.twitterecho.broker;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.Main;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.twitterecho.ApplicationManager;

import java.net.UnknownHostException;
import java.net.InetAddress;
/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/9/13
 * Time: 4:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterechoWebserver implements Runnable{

    private ApplicationManager applicationManager;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TwitterechoWebserver.class);


    public TwitterechoWebserver(ApplicationManager applicationManager){
        this.applicationManager = applicationManager;
    }

    public Main getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(Main camelContext) {
        this.camelContext = camelContext;
    }

    // create a new Camel Main so we can easily start Camel
    private Main camelContext;

    public void run(){
        logger.info("starting web server");

//        if(this.camelContext == null){
//            this.camelContext  = new Main();
//        }


        // enable hangup support which mean we detect when the JVM terminates, and stop Camel graceful
        this.camelContext.enableHangupSupport();
        ElasticSearchRoute elasticSearchRoute = new ElasticSearchRoute();

        WebConsoleRoute webConsoleRoute  = new WebConsoleRoute();
        webConsoleRoute  .setExchangeName("twitterecho-1");
        webConsoleRoute  .setHttpPort(applicationManager.getConfig().getInt("http.console.port",9090));
		
		String localAddress = "localhost";
		
        try {
            localAddress = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("can't find local host address. Use localhost",e);
        }
		
        webConsoleRoute.setHost(localAddress);

        // add our routes to Camel
        this.camelContext.addRouteBuilder(webConsoleRoute);
//        this.camelContext.addRouteBuilder(elasticSearchRoute);

        // and run, which keeps blocking until we terminate the JVM (or stop CamelContext)
        try {
            this.camelContext.run();



// send to a specific queue
//            template.sendBody("rabb:MyQueue", "<hello>world!</hello>");









        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
