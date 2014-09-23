package pt.sapo.labs.crawl.twitter;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.sapo.labs.StreamFilterType;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.crawl.twitter.streaming.TokenManager;
import pt.sapo.labs.utils.TwitterOAuthInfo;
import au.com.bytecode.opencsv.CSVReader;

import com.twitter.hbc.twitter4j.Twitter4jStatusClient;

public class ApplicationManager {

	private static Logger logger = LoggerFactory.getLogger(ApplicationManager.class);	
	
	public static int NUMBER_OF_USERS_PER_CONNECTION = 5000;


	private List<TwitterOAuthInfo> accounts;
	


	public ApplicationManager(CompositeConfiguration config) {

        this.activeTwitterStreamClients = new ArrayList<Twitter4jStatusClient>();
		this.config = config;
        this.config.setListDelimiter(';');

        if((this.config.containsKey("filter.type")) &&
            (
                this.config.getString("filter.type").equalsIgnoreCase(StreamFilterType.KEYWORDS.toString())
                || this.config.getString("filter.type").equalsIgnoreCase(StreamFilterType.USERS.toString())
            )
           ){
            this.filterManager = new FilterManager(this.config.getString("filter.file"));
        }else{
            logger.info("No stream filter defined. Will use SampleStream instead.");
        }
	}

	public List<TwitterOAuthInfo> getTwitterAccounts(){
        this.config.setListDelimiter(';');

//        List<Object> twitterAccounts = (List<Object>) this.config.getList("twitter.tokens");

        List<TwitterOAuthInfo> twitterAccounts  = new ArrayList<TwitterOAuthInfo>();
        try {
            CSVReader reader = new CSVReader(new FileReader((String) config.getString("oauth.file.path")),',');

            List<String[]> lines = reader.readAll();

            for (String[] line : lines){
                TwitterOAuthInfo twitterOAuthInfo = new TwitterOAuthInfo();

                if(line.length >= 4){

                    twitterOAuthInfo.setToken(line[0]);
                    twitterOAuthInfo.setSecret(line[1]);

                    twitterOAuthInfo.setConsumerKey(line[2]);
                    twitterOAuthInfo.setConsumerSecret(line[3]);

                    if(line.length == 5){
                        twitterOAuthInfo.setScreenName(line[4]);
                    }

                    if(twitterOAuthInfo.isValid()){
                        twitterAccounts.add(twitterOAuthInfo);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read oauth.file",e);
        }

        logger.info("Loaded " + twitterAccounts.size() + " accounts");

        return twitterAccounts;
	}

	public int getMaxSupportedUsers() {
		// TODO Auto-generated method stub
		if(accounts == null) 
			accounts = getTwitterAccounts();
		
		logger.debug("accounts.length : " +accounts.size());
		return accounts.size() * NUMBER_OF_USERS_PER_CONNECTION;
	}

    public void setAdapters(List<StatusAdapter> adapters) {
        this.adapters = adapters;
    }

    public  List<StatusAdapter> getAdapters() {
        return adapters;
    }

    public List<Twitter4jStatusClient> getActiveTwitterStreamClients() {
        return this.activeTwitterStreamClients;
    }

    public Configuration getConfig() {
        return config;
    }

    public FilterManager getFilterManager() {
        return filterManager;
    }

    private TokenManager tokenManager;
    private List<Twitter4jStatusClient> activeTwitterStreamClients;
    private List<StatusAdapter> adapters;
    private CompositeConfiguration config;
    private FilterManager filterManager;



}

