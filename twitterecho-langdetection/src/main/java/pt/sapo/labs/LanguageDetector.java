package pt.sapo.labs;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class LanguageDetector {
	
	private static Logger logger = LoggerFactory.getLogger(LanguageDetector.class);
	
	private static LanguageDetector myInstance = null;
	
	public synchronized static LanguageDetector getInstance() {  
		logger.debug("Loading Language Detector");
		
	      if (myInstance == null) {  
	    	  myInstance = new LanguageDetector();
//	    	  String langprofilesDir = System.getProperty("pt.sapo.labs.langprofiles");

              ClassLoader classLoader = LanguageDetector.class.getClassLoader();

              //String langprofilesDir = classLoader.getResource("pt/sapo/labs/langprofiles/").toString();

              String langprofilesDir = "pt/sapo/labs/langprofiles/";

              logger.info("langprofilesDir : " + langprofilesDir);

	    	  if(langprofilesDir == null){
	    		  logger.error("error to load language profiles");
	    	  }
	    	  
	    	  try {
				myInstance.init(langprofilesDir);
			} catch (LangDetectException e) {
				// TODO Auto-generated catch block
				logger.error("error to init Lang detector",e);
			}
	      }  
	      return myInstance;  
	   }  
	
	public void init(String profileDirectory) throws LangDetectException {
        DetectorFactory.loadProfile(profileDirectory);
        System.out.print("ok");
    }
	
    public String detect(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }
    
    public ArrayList<Language> detectLangs(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }
}
