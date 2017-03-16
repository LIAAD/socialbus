package pt.sapo.labs;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
// import com.twitter.hbc.httpclient.ClientContext;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
//import com.twitter.hbc.twitter4j.v3.Twitter4jStatusClient;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.crawl.twitter.ApplicationManager;
import pt.sapo.labs.crawl.twitter.streaming.TokenManager;
import pt.sapo.labs.utils.TwitterOAuthInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SampleStreamClientStrategy implements StreamClientStrategy{

    private static Logger logger = LoggerFactory.getLogger(SampleStreamClientStrategy.class);

    private ApplicationManager applicationManager;
    private TokenManager tokenManager;

	public SampleStreamClientStrategy(ApplicationManager applicationManager,TokenManager tokenManager) {	
        this.applicationManager = applicationManager;
        this.tokenManager = tokenManager;
    }

    public void execute() {
        logger.info("starting sample stream");

        // Create an appropriately sized blocking queue
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

        TwitterOAuthInfo authenticationData = this.tokenManager.getAvailableAuthentication();

        if (authenticationData != null) {
            String oAuthToken = authenticationData.getToken();
            String oAuthSecretToken = authenticationData.getSecret();

            List<StatusAdapter> adapters = this.applicationManager.getAdapters();
            for (StatusAdapter adapter : adapters) {

                adapter.getConfiguration().setProperty("token", oAuthToken);
                adapter.getConfiguration().setProperty("token.secret", oAuthSecretToken);
            }

			String consumerKey = authenticationData.getConsumerKey();
            String consumerSecret = authenticationData.getConsumerSecret();

            // Define our endpoint: By default, delimited=length is set (we need this for our processor)
            // and stall warnings are on.
            StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();

			int retries = 10;

            String consumerKey_= "qo1X760cN7owZNXTE05tTzeWE";
            String consumerSecret_ = "kvRWa6bzyVEQKig8LRm8vfwz27yXAMCeSpAieSAygX8YRdYBJR";
            String token = "839802838633046017-OtuVGS3R7M60gJpXvduFMrcNGHrtEO5";
            String secret = "7pyyfFEwasGnmoxYSoZxFp7D1P2AT7UOJk3K1qW8JhlKW";

            Authentication auth = new OAuth1(consumerKey_, consumerSecret_, token, secret);

            // Create a new BasicClient. By default gzip is enabled.
            BasicClient client = new ClientBuilder()
//                    .name("sampleExampleClient")
                    .hosts(Constants.STREAM_HOST)
                    .endpoint(endpoint)
                    .authentication(auth)
                    .processor(new StringDelimitedProcessor(queue))
                    .build();

            // Create an executor service which will spawn threads to do the actual work of parsing the incoming messages and
            // calling the listeners on each message
            int numProcessingThreads = 4;
            ExecutorService service = Executors.newFixedThreadPool(numProcessingThreads);

            // Wrap our BasicClient with the twitter4j client
            Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
                    client, queue, adapters, service);

            // Establish a connection
            t4jClient.connect();
            for (int threads = 0; threads < numProcessingThreads; threads++) {
                // This must be called once per processing thread
                t4jClient.process();
            }

            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            client.stop();



        } else {
            logger.error("No twitter accounts available to open a new connection");
        }
    }
}