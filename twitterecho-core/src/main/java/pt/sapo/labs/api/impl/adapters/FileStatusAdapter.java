package pt.sapo.labs.api.impl.adapters;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileStatusAdapter extends AbstractStatusAdapter {

	private static Logger logger = LoggerFactory.getLogger(FileStatusAdapter.class);

    private String homeDir;
	private String topicName;
	private String clientName;

	public FileStatusAdapter() {
    }

    public void initialize(){
        logger.info("initializing file status adapter");

        this.homeDir = this.getConfiguration().getString("home.dir",
                System.getProperty("java.io.tmpdir"));

        this.topicName = this.getConfiguration().getString("topic.name",
                "sample");

		this.clientName = "localhost";

        if(this.homeDir != null){

           File homeDirSys = new File(this.homeDir);

           if(!homeDirSys.exists()){
               homeDirSys.mkdir();
           }

           if(homeDirSys.isDirectory()){

               if(homeDirSys.canWrite()){
                   this.setEnabled(true);
               }else{
                    logger.error("Check your permissions. Can't write at " + homeDirSys);
               }
           }else{
               logger.error("Check your home.dir configuration. It's not a directory" + homeDirSys);
           }
        }
    }

    @Override
    public void onStatus(String json) {

        if(!this.isEnabled()) {
            return;
        }

        SimpleDateFormat sdf_hour = new  SimpleDateFormat("HH");
        SimpleDateFormat sdf_day = new  SimpleDateFormat("yyyy/MM/dd");

        Date now = new Date();
        String suffix = sdf_hour.format(now);

        if(!this.homeDir.endsWith(File.separator)){
            this.homeDir+=File.separator;
        }

        String dirName = this.homeDir + File.separator + 
						 this.topicName + File.separator + 
					     this.clientName + File.separator + 
					     "json" +  File.separator 
					     + sdf_day.format(now);
		
        String fileName = dirName + File.separator + suffix;

        try {
            File file = new File(fileName);

            logger.info("writing to file : " + file);

            // save data on file
            FileUtils.writeStringToFile(file, json + "\n", true);

        } catch (IOException e) {
            logger.error("Fail to write file at " + dirName ,e);
        }
    }

    @Override
	public void onStatus(twitter4j.internal.org.json.JSONObject status) {
		super.onStatus(status);

        String jsonString = status.toString();
		
		// try{
// 			twitter4j.internal.org.json.JSONObject metadata = (twitter4j.internal.org.json.JSONObject) status.get("metadata");
// 			String statusTopicName = (String) metadata.get("topic");
// 			this.topicName = statusTopicName;
// 		
// 			String clientName = (String) metadata.get("client");
// 			this.clientName = clientName;	
// 			
// 		}catch( twitter4j.internal.org.json.JSONException e){
// 			logger.error("Fail to read metadata from status");
// 		}
		
		
        this.onStatus(jsonString);
	}

    @Override
    public void onStatus(JSONObject status) {
        super.onStatus(status);

        String jsonString = parseJsonString(status);
		
		JSONObject metadata = (JSONObject) status.get("metadata");
		
		String statusTopicName = (String) metadata.get("topic");
		this.topicName = statusTopicName;
		
		String clientName = (String) metadata.get("client");
		this.clientName = clientName;
		
        this.onStatus(jsonString);
    }	
}