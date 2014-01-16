package pt.sapo.labs.twitterecho.broker;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketComponent;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/9/13
 * Time: 3:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRoute extends RouteBuilder {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ElasticSearchRoute.class);

    @Override
    public void configure() throws Exception {

        logger.info("setup camel elasticsearch route");

        from("direct:foo")
                .to("elasticsearch://local?operation=INDEX&indexName=twitter&indexType=tweet");
    }
}
