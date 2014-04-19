package pt.sapo.labs.api.impl.handlers;

import com.twitter.common.text.extractor.EmoticonExtractor;
import com.twitter.common.text.extractor.HashtagExtractor;
import com.twitter.common.text.extractor.URLExtractor;
import com.twitter.common.text.extractor.UserNameExtractor;
import com.twitter.common.text.token.TokenStream;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntitiesExtractionStatusMetadataHandler extends AbstractStatusMetadataHandler {

    private static Logger logger = LoggerFactory.getLogger(EntitiesExtractionStatusMetadataHandler.class);

    public EntitiesExtractionStatusMetadataHandler(){}

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
	            TokenStream hashtagsStream = new HashtagExtractor();
	            TokenStream usersStream = new UserNameExtractor();
	            TokenStream urlsStream = new URLExtractor();
	            TokenStream emoticonsStream = new EmoticonExtractor();

	            String statusText = (String) json.get("text");

	            hashtagsStream.reset(statusText);
	            usersStream.reset(statusText);
	            urlsStream.reset(statusText);
	            emoticonsStream.reset(statusText);

	            List<String> users = usersStream.toStringList();
                List<String> hashtags = hashtagsStream.toStringList();
                List<String> urls = urlsStream.toStringList();
	            List<String> emoticons = emoticonsStream.toStringList();

	            metadata.put("hashtags", hashtags);
	            metadata.put("mentions", users);
	            metadata.put("urls", urls);
	            metadata.put("emoticons", emoticons);

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

                    TokenStream hashtagsStream = new HashtagExtractor();
                    TokenStream urlsStream = new URLExtractor();

                    hashtagsStream.reset(preprossedText );
                    urlsStream.reset(preprossedText );

                    List<String> hashtags = hashtagsStream.toStringList();
                    List<String> urls = urlsStream.toStringList();


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
