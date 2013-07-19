package pt.sapo.labs.twitterecho.adapters;


import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import pt.sapo.labs.utils.LanguageDetector;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

import com.cybozu.labs.langdetect.LangDetectException;
import com.twitter.common.text.DefaultTextTokenizer;
import com.twitter.common.text.extractor.EmoticonExtractor;
import com.twitter.common.text.extractor.HashtagExtractor;
import com.twitter.common.text.extractor.URLExtractor;
import com.twitter.common.text.extractor.UserNameExtractor;
import com.twitter.common.text.token.TokenStream;
import com.twitter.common.text.token.TokenizedCharSequence;
import com.twitter.common.text.token.TokenizedCharSequence.Token;

public abstract class BaseStatusAdapter {

	private static Logger logger = Logger.getLogger(BaseStatusAdapter.class);

	protected int statusCount = 0;


	protected JSONObject readMetadata(JSONObject status){
		JSONObject metadata = (JSONObject) status.get("metadata");

		return metadata;
	}

	protected String readTopicName(JSONObject status){
		JSONObject metadata = readMetadata(status);
		String topicName = (String) metadata.get("topic");
		return topicName;
	}

	protected String readClientHost(JSONObject status){
		JSONObject metadata = readMetadata(status);
		String topicName = (String) metadata.get("client");
		return topicName;
	}
	
	public JSONObject processMetadata(JSONObject status){
		JSONObject metadata = readMetadata(status);
		
		TokenStream hashtagsStream = new HashtagExtractor();
		TokenStream usersStream = new UserNameExtractor();
		TokenStream urlsStream = new URLExtractor();
		TokenStream emoticonsStream = new EmoticonExtractor();
		
		DefaultTextTokenizer tokenizer =
		        new DefaultTextTokenizer.Builder().setKeepPunctuation(true).build();

		String statusText = (String) status.get("text");

		List<String> tokens = new ArrayList<String>();
		TokenizedCharSequence tokSeq = tokenizer.tokenize(statusText);
	      for (Token tok : tokSeq.getTokens()) {
	    	String term = String.format("%s",tok.getTerm()); 
	        tokens.add(term);
	      }
	    
		hashtagsStream.reset(statusText);
		usersStream.reset(statusText);
		urlsStream.reset(statusText);
		emoticonsStream.reset(statusText);
	    
	    List<String> hashtags = hashtagsStream.toStringList();
	    List<String> users = usersStream.toStringList();
	    List<String> urls = urlsStream.toStringList();
	    List<String> emoticons = emoticonsStream.toStringList();
		
	    metadata.put("hashtags", hashtags);
	    metadata.put("mentions", users);
	    metadata.put("urls", urls);
	    metadata.put("emoticons", emoticons);
	    metadata.put("tokenized", StringUtils.join(tokens," "));
	    
	    String lang ="undefined";
	    
		try {
			String preprossedText = statusText;
			
			for (String mention : users) {
				preprossedText = preprossedText.replaceAll(mention, "");
			}
			
			lang = LanguageDetector.getInstance().detect(preprossedText);
			
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    metadata.put("language", lang);
	    
	    status.remove("metadata");
	    
	    status.put("metadata", metadata);
	    
		return status;
	}

	protected String rawJson(JSONObject statusJson){
		StringWriter out = new StringWriter();
		try {
			JSONValue.writeJSONString(statusJson, out);
			String jsonText = out.toString();

			return jsonText;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("fail to convert json to twitter4j.Status",e);
		}
		
		return null;
	}

	protected Status buildStatus(JSONObject statusJson){

		JSONObject statusClone = (JSONObject) statusJson.clone();
		statusClone.remove("metadata");

		StringWriter out = new StringWriter();
		try {
			JSONValue.writeJSONString(statusClone, out);
			String jsonText = out.toString();

			Status status = DataObjectFactory.createStatus(jsonText);

			return status;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("fail to convert json to twitter4j.Status",e);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.error("fail to convert json to twitter4j.Status",e);
		}

		return null;
	}

	public abstract void onStatus(JSONObject status);

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

		String msg = "Got track limitation notice:" + numberOfLimitedStatuses;
		logger.warn(msg);

		//new Mailer().logMail(msg);
	}

	public void onException(Exception e) {
		//new Mailer().logMail("Exception : " + e.getMessage());
		logger.error("Exception message : " + e.getMessage());
		logger.error("Exception localized message : " + e.getLocalizedMessage());

		logger.error("Stream error", e);		
	}

	public void onScrubGeo(long lat, long lon) {
		// TODO Auto-generated method stub
		logger.debug("onScrubGeo : " + lat + ","+ lon);
	}    

}
