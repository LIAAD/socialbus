package pt.sapo.labs.crawl.twitter.streaming.adapter;

import org.apache.log4j.Logger;

import pt.sapo.labs.utils.JSONUtils;
import twitter4j.Status;

public class ConsoleStatusAdapter extends BaseStatusAdapter{

	private static Logger logger = Logger.getLogger(ConsoleStatusAdapter.class);

	@Override
	public void onStatus(Status status) {
		logger.info("New status (" + statusCount + ") ------------------------------------------------------------------------------------");
		++statusCount;
		logger.info("@" + status.getUser().getScreenName() + " : " + status.getText());
		
		logger.trace(JSONUtils.jsonStringFromObject(status));
	}
}
