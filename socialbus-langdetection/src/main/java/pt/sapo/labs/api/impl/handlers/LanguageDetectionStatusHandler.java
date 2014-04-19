package pt.sapo.labs.api.impl.handlers;

import com.cybozu.labs.langdetect.LangDetectException;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import pt.sapo.labs.LanguageDetector;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class LanguageDetectionStatusHandler extends AbstractStatusMetadataHandler {

    private static Logger logger = LoggerFactory.getLogger(LanguageDetectionStatusHandler.class);

    private String preprocess(JSONObject json){
        String statusText = (String) json.get("text");
//        List<String> users = (List<String>) readMetadata(json).get("mentions");
//
//        for (String mention : users) {
//            statusText = statusText.replaceAll(mention, "");
//        }

        return statusText;
    }

    protected JSONObject readMetadata(JSONObject status){
        JSONObject metadata = (JSONObject) status.get("metadata");

        return metadata;
    }

    @Override
    protected boolean accept(JSONObject json) {
        return true;
    }

    @Override
    protected void doHandle(JSONObject json) {
        JSONObject original = (JSONObject) json.clone();

        logger.info(String.valueOf(json.get("id")));

        try {

            JSONObject metadata = readMetadata(json);

            String service = (String) metadata.get("service");

            String lang ="undefined";

			// support to twitter
			if(service.equals("twitter")){

                String preprossedText = preprocess(json);

	            lang = LanguageDetector.getInstance().detect(preprossedText);

	            metadata.put("language", lang);
	            json.remove("metadata");
	            json.put("metadata", metadata);

			}else
                // support to facebook
                if(service.startsWith("facebook")){

                String preprossedText = "";

                if(json.containsKey("message")){
                    preprossedText += (String) json.get("message");
                }

                if(json.containsKey("description")){
                    preprossedText += (String) json.get("description");
                }

                lang = LanguageDetector.getInstance().detect(preprossedText);


            }

            metadata.put("language", lang);
            json.remove("metadata");
            json.put("metadata", metadata);

            //handle request (move to correct folder)
            if(this.nextHandler != null){
                this.nextHandler.handleRequest(json);
            }

        } catch (LangDetectException e) {

            logger.error("failed to detect language",e);

//            continues to next handler with original json
            if(this.nextHandler != null){
                this.nextHandler.handleRequest(original);
            }
        }
    }
}
