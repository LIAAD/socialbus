package pt.sapo.labs;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.client.protocol.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.crawl.twitter.ApplicationManager;
import pt.sapo.labs.crawl.twitter.streaming.TokenManager;
import pt.sapo.labs.utils.TwitterOAuthInfo;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;

public class SampleStreamClientStrategy implements StreamClientStrategy{

    private static Logger logger = LoggerFactory.getLogger(SampleStreamClientStrategy.class);

    private ApplicationManager applicationManager;
    private TokenManager tokenManager;
    private ClientContext context;

    public SampleStreamClientStrategy(ApplicationManager applicationManager,TokenManager tokenManager,ClientContext context) {
        this.applicationManager = applicationManager;
        this.tokenManager = tokenManager;
        this.context = context;

    }

    public void execute() {
        logger.info("starting sample stream");

        // Create an appropriately sized blocking queue
//        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

        TwitterOAuthInfo authenticationData = this.tokenManager.getAvailableAuthentication();

        if (authenticationData != null) {
            String oAuthToken = authenticationData.getToken();
            String oAuthSecretToken = authenticationData.getSecret();

            List<StatusAdapter> adapters = this.applicationManager.getAdapters();
            for (StatusAdapter adapter : adapters) {

                adapter.getConfiguration().setProperty("token", oAuthToken);
                adapter.getConfiguration().setProperty("token.secret", oAuthSecretToken);
            }

            //            String consumerKey = this.applicationManager.getConfig().getString("application.consumer.key");
//            String consumerSecret = this.applicationManager.getConfig().getString("application.consumer.secret");

            String consumerKey = authenticationData.getConsumerKey();
            String consumerSecret = authenticationData.getConsumerSecret();

            Authentication auth = new OAuth1(consumerKey, consumerSecret, oAuthToken, oAuthSecretToken);
//            Authentication auth = new OAuth1("19595038-ktoF42PZrAEU1pFoHYKVktsgQuQ6kEWAQ77wTFQAw", 
//            		"pejWmJ57tmqKeDNgOzK8F5nR7UDKq0mFvFEKiaAywU", "GJGALwtkuTDljV3eBDAYaQ", "lGENO2QQVasMzalcpp6A3fAciNO1u0wA2ZihMpibyY0");
            
            // Authentication auth = new BasicAuth(username, password);

            // Define our endpoint: By default, delimited=length is set (we need this for our processor)
            // and stall warnings are on.
            StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();

//            int retries = Integer.parseInt((String)this.context.getProperty("connection.retries","10"));
            int retries = 10;

            System.out.println("--------------------- teste");
            // Create a new BasicClient. By default gzip is enabled.
            BasicClient client = new ClientBuilder()
//                    .name("sampleExampleClient")
                    .hosts(Constants.STREAM_HOST)
                    .endpoint(endpoint)
                    .authentication(auth)
                    .processor(new StringDelimitedProcessor(msgQueue))
                    .retries(retries)
//                    .context(this.context)
                    .build();

            // Create an executor service which will spawn threads to do the actual work of parsing the incoming messages and
            // calling the listeners on each message
            int numProcessingThreads = 4;
            ExecutorService service = Executors.newFixedThreadPool(numProcessingThreads);

            // Wrap our BasicClient with the twitter4j client
//            List listeners = Arrays.asList(adapters);

            Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
                    client, msgQueue, adapters, service);

            t4jClient.setJSONStoreEnabled(true);
            
            this.applicationManager.getActiveTwitterStreamClients().add(t4jClient);
            // Establish a connection
            t4jClient.connect();
            for (int threads = 0; threads < numProcessingThreads; threads++) {
                // This must be called once per processing thread
                t4jClient.process();

//                System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());


//                if (client.isDone()) {
//                    logger.error("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
//                    break;
//                }
            }

        } else {
            logger.error("No twitter accounts available to open a new connection");
        }
    }
}