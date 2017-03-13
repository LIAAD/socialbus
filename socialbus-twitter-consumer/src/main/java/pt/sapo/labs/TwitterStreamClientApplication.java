package pt.sapo.labs;

import com.twitter.hbc.twitter4j.v3.Twitter4jStatusClient;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.crawl.twitter.ApplicationManager;
import pt.sapo.labs.crawl.twitter.streaming.TokenManager;
import pt.sapo.labs.utils.AppUtils;
import pt.sapo.labs.utils.TwitterOAuthInfo;

import java.util.List;

public class TwitterStreamClientApplication implements IApp {

	private static Logger logger = LoggerFactory.getLogger(TwitterStreamClientApplication.class);

	private ApplicationManager applicationManager;
    private TokenManager tokenManager;

	private CompositeConfiguration config;

    public TwitterStreamClientApplication(CompositeConfiguration config) {
		this.config = config;
    }

    @Override
	public void start() {

        applicationManager = new ApplicationManager(config);
        logger.info("Loading twitter accounts");
        List<TwitterOAuthInfo> twitterAccounts = applicationManager.getTwitterAccounts();

        logger.debug("twitter accounts : " + twitterAccounts );

        logger.info("Loading connection pool ...");
        tokenManager = new TokenManager(twitterAccounts);

        loadAdapters();

		String filterType = this.config.getString("filter.type", StreamFilterType.NONE.toString());

        // Three contexts following different strategies
		StreamStrategyContext streamStrategyContext = new StreamStrategyContext(this.applicationManager,
                                                                        this.tokenManager);
        streamStrategyContext.execute(filterType);
	}

	protected void loadAdapters() {

        List<StatusAdapter> adapters = ApplicationServiceLoader.loadAdapters(StatusAdapter.class);
        this.getApplicationManager().setAdapters(adapters);

        for (StatusAdapter adapter : adapters){
//            TODO rever this.config.getConfiguration(0);
            Configuration conf = this.config;
            adapter.setConfiguration(conf);
            adapter.initialize();
        }
	}

    public ApplicationManager getApplicationManager(){
        return this.applicationManager;
    }

    @Override
	public void shutDown() {
		logger.info("Shutting down open streams");

        for (Twitter4jStatusClient consumer : this.applicationManager.getActiveTwitterStreamClients()) {
            consumer.stop();
        }
    }



}
