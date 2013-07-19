package pt.sapo.labs.utils;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.cybozu.labs.langdetect.Language;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class LanguageDetector {
	
	private static Logger logger = Logger.getLogger(LanguageDetector.class);
	
	private static LanguageDetector myInstance = null;
	
	public synchronized static LanguageDetector getInstance() {  
		logger.debug("Loading Language Detector");
		
	      if (myInstance == null) {  
	    	  myInstance = new LanguageDetector();
	    	  String langprofilesDir = System.getProperty("langprofiles");
	    	  
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
