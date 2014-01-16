package pt.sapo.labs.crawl.twitter.streaming;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;

import java.util.Arrays;

public class UserStreamConsumer extends BaseStreamConsumer {

	protected String[] usersToMonitor;
	
	public UserStreamConsumer(String [] users) {
		
		this.usersToMonitor = Arrays.copyOf(users, users.length);
	}
	
	public void startConsuming() throws TwitterException {
		super.startConsuming();
		
		FilterQuery tQuery = new FilterQuery();
		
		long[] usersToMonitorL = convertStringToLongs(usersToMonitor);
		
		tQuery.follow(usersToMonitorL);
		
		twitterStream.filter(tQuery);
	}
	
	public static long[] convertStringToLongs(String[] input)
	{
	    if (input == null)
	    {
	        return null; // Or throw an exception - your choice
	    }
	    long[] output = new long[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	    	if(input[i] != null){
	    		output[i] = Long.parseLong(input[i]);	    		
	    	}
	    }
	    return output;
	}
}
