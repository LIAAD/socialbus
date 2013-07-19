package pt.sapo.labs.crawl.twitter.streaming.adapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import twitter4j.Status;

public class CSVStatusAdapter extends BaseStatusAdapter {

private static Logger logger = Logger.getLogger(CSVStatusAdapter.class);
	
	private String homeDir;

	public CSVStatusAdapter(String homeDir) {
		super();
		this.homeDir = homeDir;
	}
	
	public CSVStatusAdapter() {
		this(System.getProperty("java.io.tmpdir"));
	}

	@Override
	public void onStatus(Status status) {
		super.onStatus(status);
		
		StringBuffer sBuffer = new StringBuffer();
		
		sBuffer.append(status.getUser().getScreenName());
		sBuffer.append("\t");
		sBuffer.append(status.getCreatedAt());
		sBuffer.append("\t");
		sBuffer.append(status.getId());
		sBuffer.append("\t");
		sBuffer.append(status.getText().replaceAll("(\\t)", ""));
		
		String line = sBuffer.toString();
		line = line.replaceAll("(\\r|\\n)", "");
		
		SimpleDateFormat sdf_hour = new  SimpleDateFormat("HH");
		SimpleDateFormat sdf_day = new  SimpleDateFormat("yyyy_MM_dd");
		
		try {
			Date now = new Date();
			String suffix = sdf_hour.format(now);

			if(!this.homeDir.endsWith(File.separator)){
				this.homeDir+=File.separator;				
			}
			
			String dirName = this.homeDir + File.separator + "csv" +  File.separator + sdf_day.format(now);
			String fileName = dirName + File.separator + suffix;

			File file = new File(fileName);

			logger.info("writing to csv file : " + file);
			
			// save data on file
			FileUtils.writeStringToFile(file, line + "\n", true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
