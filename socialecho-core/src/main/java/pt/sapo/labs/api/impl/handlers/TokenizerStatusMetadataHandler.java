package pt.sapo.labs.api.impl.handlers;

import com.twitter.common.text.DefaultTextTokenizer;
import com.twitter.common.text.token.TokenizedCharSequence;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenizerStatusMetadataHandler extends AbstractStatusMetadataHandler {

    private static Logger logger = LoggerFactory.getLogger(TokenizerStatusMetadataHandler.class);

    public TokenizerStatusMetadataHandler(){}

    @Override
    protected boolean accept(JSONObject json) {
//        TODO: validar json
        return true;
    }

    @Override
    protected void doHandle(JSONObject json) {


        logger.info(String.valueOf(json.get("id")));
        JSONObject original = (JSONObject) json.clone();

        try{

            JSONObject metadata = readMetadata(json);
			
			String service = (String) metadata.get("service");
			
			// support only to twitter
			if(service.equals("twitter")){
	            DefaultTextTokenizer tokenizer =
	                    new DefaultTextTokenizer.Builder().setKeepPunctuation(true).build();

	            String statusText = (String) json.get("text");

	            List<String> tokens = new ArrayList<String>();
	            TokenizedCharSequence tokSeq = tokenizer.tokenize(statusText);
	            for (TokenizedCharSequence.Token tok : tokSeq.getTokens()) {
	                String term = String.format("%s",tok.getTerm());
	                tokens.add(term);
	            }

	            metadata.put("tokenized", StringUtils.join(tokens, " "));

	            json.remove("metadata");
	            json.put("metadata", metadata);

	            	
			}
			
            if(this.nextHandler != null){
                this.nextHandler.handleRequest(json);
            }
            

        }catch (Exception e){
            logger.error("Fail to tokenize text",e);

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
