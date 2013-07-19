package pt.sapo.labs.twitterecho.adapters;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import twitter4j.Status;
import twitter4j.json.DataObjectFactory;

public class FileOutputStatusAdapter extends BaseStatusAdapter {

	private static Logger logger = Logger.getLogger(FileOutputStatusAdapter.class);
	
	private String homeDir;

	public FileOutputStatusAdapter(String homeDir) {
		super();
		this.homeDir = homeDir;
	}
	

	@Override
	public void onStatus(JSONObject statusJson) {

		String topicName = readTopicName(statusJson);
		String clientHost = readClientHost(statusJson);
		
//		Status status = buildStatus(statusJson);
//		String rawJson = DataObjectFactory.getRawJSON(status);
		
		String rawJson = rawJson(statusJson);
		
		SimpleDateFormat sdf_hour = new  SimpleDateFormat("HH");
		SimpleDateFormat sdf_day = new  SimpleDateFormat("yyyy/MM/dd");
		
		try {
			Date now = new Date();
			String suffix = sdf_hour.format(now);

			if(!this.homeDir.endsWith(File.separator)){
				this.homeDir+=File.separator;				
			}
			
			String dirName = this.homeDir  + File.separator + 
							topicName + File.separator + 
							clientHost + File.separator + 
							"json" +  File.separator 
							+ sdf_day.format(now);
			
			String fileName = dirName + File.separator + suffix;

			File file = new File(fileName);

			logger.info("writing to file : " + file);
			
			// save data on file
			FileUtils.writeStringToFile(file, rawJson + "\n", true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
