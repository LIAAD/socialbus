package pt.sapo.labs;

import com.twitter.hbc.BasicRateTracker;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.RateTracker;
import com.twitter.hbc.ReconnectionManager;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.v3.Twitter4jStatusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.crawl.twitter.ApplicationManager;
import pt.sapo.labs.crawl.twitter.FilterManager;
import pt.sapo.labs.crawl.twitter.streaming.TokenManager;
import pt.sapo.labs.utils.TwitterOAuthInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class KeywordFilterStreamClientStrategy implements StreamClientStrategy {

    private static Logger logger = LoggerFactory.getLogger(KeywordFilterStreamClientStrategy.class);

    private ApplicationManager applicationManager;
    private TokenManager tokenManager;

	public KeywordFilterStreamClientStrategy(ApplicationManager applicationManager,TokenManager tokenManager) {
        this.applicationManager = applicationManager;
        this.tokenManager = tokenManager;
    }

    public void execute() {
        logger.info("starting keyword filter stream");

        // Create an appropriately sized blocking queue
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

        // Define our endpoint: By default, delimited=length is set (we need this for our processor)
        // and stall warnings are on.
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        String[] trackingKeywords = applicationManager.getFilterManager().
                loadFilters(FilterManager.NUMBER_OF_KEYWORDS_PER_CONNECTION);

        List<String> trackingKeywordsL = new ArrayList<String>();

        for (String keyword : trackingKeywords) {
            trackingKeywordsL.add(keyword);
        }


        trackingKeywordsL.removeAll(Collections.singleton(null));

        endpoint.trackTerms(trackingKeywordsL);

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

            Authentication auth = new OAuth1(consumerKey, consumerSecret, oAuthToken, oAuthSecretToken);

			int retries = 10;
            // Create a new BasicClient. By default gzip is enabled.
            BasicClient client = new ClientBuilder()
                    .hosts(Constants.STREAM_HOST)
                    .endpoint(endpoint)
                    .authentication(auth)
                    .processor(new StringDelimitedProcessor(queue))
                    // .context(this.context)
                    .retries(retries)
                    .build();

            // Create an executor service which will spawn threads to do the actual work of parsing the incoming messages and
            // calling the listeners on each message
            int numProcessingThreads = 4;
            ExecutorService service = Executors.newFixedThreadPool(numProcessingThreads);

            Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
                    client, queue, adapters, service);


            this.applicationManager.getActiveTwitterStreamClients().add(t4jClient);
            // Establish a connection
            t4jClient.connect();
            for (int threads = 0; threads < numProcessingThreads; threads++) {
                // This must be called once per processing thread
                t4jClient.process();
            }

        } else {
            logger.error("No twitter accounts available to open a new connection");
        }
    }
}