package pt.sapo.labs.crawl.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FilterManager {
	
	private static Logger logger = LoggerFactory.getLogger(FilterManager.class);	

	public static int NUMBER_OF_USERS_PER_CONNECTION = 5000;
	public static int NUMBER_OF_KEYWORDS_PER_CONNECTION = 400;

    private String filterFilePath;

	public FilterManager(String filterConfigFilePath) {

		this.filterFilePath = filterConfigFilePath;
	}

	public String [] loadFilters(int max_filters){

		String [] filters = new String[max_filters];

		logger.debug("Loading file content at " + this.filterFilePath);
		
		if(this.filterFilePath == null) {
			logger.error("Please inform 'filter.file.path' at the filter config file");

			return null;
		}

		File file = new File(this.filterFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			int lineIndex = 0;
			while (dis.available() != 0) {
//				if(lineIndex > NUMBER_OF_KEYWORDS_PER_CONNECTION) break;

				// this statement reads the line from the file and print it to
				// the console
				
				String keyword = dis.readLine();
//				logger.debug(lineIndex +  " : "+ uid);
				filters[lineIndex++] = keyword ;
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();


		} catch (FileNotFoundException e) {
			logger.error("Unable to read filter file",e);
		} catch (IOException e) {
			logger.error("Unable to read filter file",e);
		}

		return filters;
	}
}
