package pt.sapo.labs.api.impl.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.User;
//import twitter4j.internal.org.json.JSONObject;
import twitter4j.JSONObject;

public class ConsoleStatusAdapter extends AbstractStatusAdapter {

	private static Logger logger = LoggerFactory.getLogger(ConsoleStatusAdapter.class);

    private String topicName;
    public ConsoleStatusAdapter(){
    }

    public void initialize(){
        logger.info("initializing file status adapter");

        this.topicName = this.getConfiguration().getString("topic.name",
                "sample");

        this.setEnabled(true);
    }

    @Override
	public void onStatus(JSONObject json) {

        Status status = parseJsonStatus(json);

        if(status !=null){
            ++statusCount;

            logger.info("New status (" + statusCount + ") for topic[" + this.topicName+ "]" +
                    " -----------------------------------------------------------------------");

            User user = status.getUser();
            logger.info("@" + user.getScreenName() + " : " + status.getText());
        }

        logger.trace(json.toString());
	}



}
