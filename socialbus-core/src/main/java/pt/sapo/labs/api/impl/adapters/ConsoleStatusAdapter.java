package pt.sapo.labs.api.impl.adapters;

//import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
//import twitter4j.internal.org.json.JSONObject;
import twitter4j.json.DataObjectFactory;

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
    	
    	try{
			logger.debug("@" + json.getJSONObject("user").getString("screen_name") + " - " + json.getString("text"));
			
		}catch(JSONException ex){
			ex.printStackTrace();
		}
    	
    	String jsonString = parseJsonString(json);
    	System.out.println(jsonString);
//        Status status;
//		try {
//			status = parseJsonStatus(json);
//			
//			printStatus(status);
//			
//		} catch (TwitterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	private void printStatus(Status status) {
		if(status !=null){
			++statusCount;
			
			logger.info("New status (" + statusCount + ") for topic[" + this.topicName+ "]" +
					" -----------------------------------------------------------------------");
			
			User user = status.getUser();
			logger.info("@" + user.getScreenName() + " : " + status.getText());
		}
	}
	
	private void printStatusJson(String status) {
		logger.info(status);
	}

//	public void onStatus(org.json.simple.JSONObject jsonObject) {
//		// TODO Auto-generated method stub
//		
//	}
    
	public void onStatus(Status status) {
		// TODO Auto-generated method stub
    	
    	
//    	String json = parseJsonString(status);
    	logger.info("teste");
    	
//		printStatus(status);
//    	DataObjectFactory.getRawJSON(status);
//		printStatusJson(getRawJson(status));
		
		logger.info("------------------------------------------------------------");
	}

}
