package pt.sapo.labs.crawl.twitter.streaming.adapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import pt.sapo.labs.utils.JSONUtils;
import twitter4j.Status;
import twitter4j.json.DataObjectFactory;

public class FileOutputStatusAdapter extends BaseStatusAdapter {

	private static Logger logger = Logger.getLogger(FileOutputStatusAdapter.class);
	
	private String homeDir;
	private String topicName;

	public FileOutputStatusAdapter(String homeDir,String topicName) {
		super();
		this.homeDir = homeDir;
		this.topicName = topicName;
	}
	
	public FileOutputStatusAdapter(String homeDir) {
		this(homeDir,"twitterecho-stream");
	}
	
	public FileOutputStatusAdapter() {
		this(System.getProperty("java.io.tmpdir"),"twitterecho-stream");
	}

	@Override
	public void onStatus(Status status) {
		super.onStatus(status);
		
		String json = DataObjectFactory.getRawJSON(status); 

		SimpleDateFormat sdf_hour = new  SimpleDateFormat("HH");
		SimpleDateFormat sdf_day = new  SimpleDateFormat("yyyy/MM/dd");
		
		try {
			Date now = new Date();
			String suffix = sdf_hour.format(now);

			if(!this.homeDir.endsWith(File.separator)){
				this.homeDir+=File.separator;				
			}
			
			String dirName = this.homeDir + File.separator + this.topicName + File.separator + "json" +  File.separator + sdf_day.format(now);
			String fileName = dirName + File.separator + suffix;

			File file = new File(fileName);

			logger.info("writing to file : " + file);
			
			// save data on file
			FileUtils.writeStringToFile(file, json + "\n", true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
