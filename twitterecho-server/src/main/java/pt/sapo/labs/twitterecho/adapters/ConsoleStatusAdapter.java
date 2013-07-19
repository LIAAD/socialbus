package pt.sapo.labs.twitterecho.adapters;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import twitter4j.Status;

public class ConsoleStatusAdapter extends BaseStatusAdapter{

	private static Logger logger = Logger.getLogger(ConsoleStatusAdapter.class);

	@Override
	public void onStatus(JSONObject statusJson) {
		logger.info("New status (" + statusCount + ") ------------------------------------------------------------------------------------");
		++statusCount;

		String topicName = readTopicName(statusJson);
		String clientHost = readClientHost(statusJson);

		Status status = buildStatus(statusJson);

		logger.info("client :" + clientHost);
		logger.info("topic :" + topicName);
		logger.info("@" + status.getUser().getScreenName() + " : " + status.getText());
		
	}
}
