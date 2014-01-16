package pt.sapo.labs;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileMessageAdapter {

	private static Logger logger = LoggerFactory.getLogger(FileMessageAdapter.class);

    private String source = "facebook";
    private String homeDir;
	private String topicName;
	private String clientName;

    private CompositeConfiguration config;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active;
    private String sourceType;

	public FileMessageAdapter(CompositeConfiguration config, String sourceType) {
        this.config = config;
        this.sourceType = sourceType;

        this.initialize();
    }

    public void initialize(){
        logger.info("initializing file status adapter");

        this.homeDir = this.config.getString("home.dir",
                System.getProperty("java.io.tmpdir"));

        this.topicName = this.config.getString("topic.name", "sample");

		this.clientName = "localhost";

        if(this.homeDir != null){

           File homeDirSys = new File(this.homeDir);

           if(!homeDirSys.exists()){
               homeDirSys.mkdir();
           }

           if(homeDirSys.isDirectory()){

               if(homeDirSys.canWrite()){
                   this.setActive(true);
               }else{
                    logger.error("Check your permissions. Can't write at " + homeDirSys);
               }
           }else{
               logger.error("Check your home.dir configuration. It's not a directory" + homeDirSys);
           }
        }
    }

    public void onMessage(String json) {

        if(!this.isActive()) {
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
                         this.clientName + File.separator +
                         this.topicName + File.separator +

                         this.source + File.separator +
                         this.sourceType + File.separator +

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
}