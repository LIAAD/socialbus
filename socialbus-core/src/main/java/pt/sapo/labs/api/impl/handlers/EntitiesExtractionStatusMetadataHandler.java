package pt.sapo.labs.api.impl.handlers;

import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.Extractor;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntitiesExtractionStatusMetadataHandler extends AbstractStatusMetadataHandler {

    private static Logger logger = LoggerFactory.getLogger(EntitiesExtractionStatusMetadataHandler.class);
    protected Extractor extractor;
    
    public EntitiesExtractionStatusMetadataHandler(){
    	extractor = new Extractor();
    }

    @Override
    protected boolean accept(JSONObject json) {
//        TODO: validar json
        return true;
    }

    @Override
    protected void doHandle(JSONObject json) {

        JSONObject original = (JSONObject) json.clone();

        logger.info(String.valueOf(json.get("id")));

        try{
            JSONObject metadata = readMetadata(json);

			String service = (String) metadata.get("service");
			
			// support only to twitter
			if(service.equals("twitter")){

	            String statusText = (String) json.get("text");

	            List<String> users = extractor.extractMentionedScreennames(statusText);
                List<String> hashtags = extractor.extractHashtags(statusText);
                List<String> urls = extractor.extractURLs(statusText);
	            
	            metadata.put("hashtags", hashtags);
	            metadata.put("mentions", users);
	            metadata.put("urls", urls);
	            
	            json.remove("metadata");
	            json.put("metadata", metadata);

	            
			} else
                // support to facebook
                if(service.startsWith("facebook")){

                    String preprossedText = "";

                    if(json.containsKey("message")){
                        preprossedText += (String) json.get("message");
                    }

                    if(json.containsKey("description")){
                        preprossedText += (String) json.get("description");
                    }

                    List<String> hashtags = extractor.extractHashtags(preprossedText);
                    List<String> urls = extractor.extractURLs(preprossedText);


                    metadata.put("hashtags", hashtags);
                    metadata.put("urls", urls);

                    json.remove("metadata");
                    json.put("metadata", metadata);
                }
            
            if(this.nextHandler != null){
                this.nextHandler.handleRequest(json);
            }	

        }catch (Exception e){

            logger.error("Fail to extract entities",e);
            if(this.nextHandler != null){
                this.nextHandler.handleRequest(original);
            }
        }
    }

    protected JSONObject readMetadata(JSONObject status){
        JSONObject metadata = (JSONObject) status.get("metadata");

        return metadata;
    }
}
