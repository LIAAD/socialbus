package pt.sapo.labs.crawl.twitter.streaming.adapter;


import org.apache.log4j.Logger; 

import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusDeletionNotice;

public abstract class BaseStatusAdapter extends StatusAdapter {
	
	private static Logger logger = Logger.getLogger(BaseStatusAdapter.class);

	protected int statusCount = 0;

	/*@Override
	public void onStatus(Status status) {
		logger.info("######################################################################");
		logger.info("New status");
		logger.debug(JSONUtils.jsonStringFromObject(status));

		 ++statusCount;
		logger.info(statusCount);
	}*/

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		logger.info("deleted status " + statusDeletionNotice.getStatusId() + 
				" from user " + statusDeletionNotice.getUserId() );
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		
		String msg = "Got track limitation notice:" + numberOfLimitedStatuses;
		logger.warn(msg);
		
		//new Mailer().logMail(msg);
	}

	@Override
	public void onException(Exception e) {
		//new Mailer().logMail("Exception : " + e.getMessage());
		logger.error("Exception message : " + e.getMessage());
		logger.error("Exception localized message : " + e.getLocalizedMessage());
		
		logger.error("Stream error", e);		
	}

	@Override
	public void onScrubGeo(long lat, long lon) {
		// TODO Auto-generated method stub
		logger.debug("onScrubGeo : " + lat + ","+ lon);
	}    

}
