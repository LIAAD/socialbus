package pt.sapo.labs.crawl.twitter.streaming;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;

import java.util.Arrays;

public class KeywordStreamConsumer extends BaseStreamConsumer {

	protected String[] wordsToMonitor;
	
	public KeywordStreamConsumer(String [] trackingWords) {
		this.wordsToMonitor = Arrays.copyOf(trackingWords, trackingWords.length);;
	}
	
	public void startConsuming() throws TwitterException {
		super.startConsuming();
		
		FilterQuery tQuery = new FilterQuery();
		tQuery.track(wordsToMonitor);
		
		twitterStream.filter(tQuery);
	}
}
