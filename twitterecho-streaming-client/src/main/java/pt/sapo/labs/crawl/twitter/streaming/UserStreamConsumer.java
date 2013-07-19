package pt.sapo.labs.crawl.twitter.streaming;

import java.util.Arrays;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;

public class UserStreamConsumer extends BaseStreamConsumer {

	protected long[] usersToMonitor;
	
	public UserStreamConsumer(long [] users) {
		this.usersToMonitor = Arrays.copyOf(users, users.length);
	}
	
	
	public void startConsuming() throws TwitterException {
		super.startConsuming();
		
		FilterQuery tQuery = new FilterQuery();
		tQuery.follow(usersToMonitor);
		
		twitterStream.filter(tQuery);
	}
}
