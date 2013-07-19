package pt.sapo.labs.twitterecho.adapters;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;

import twitter4j.Status;

public class SolrStatusAdapter extends BaseStatusAdapter {

	private static Logger logger = Logger.getLogger(SolrStatusAdapter.class);
	
	private String solrAddress;
	private SolrServer server;
	
	public SolrStatusAdapter(String solrAddress) {
		super();
		this.solrAddress = solrAddress;
	}
	
	private SolrServer buildServerConnection(){
		if(this.server == null){
			this.server = new HttpSolrServer( this.solrAddress );
		}
		 
		return this.server;
	}

	@Override
	public void onStatus(JSONObject status) {
		// TODO Auto-generated method stub
		buildServerConnection();
		
		Status twitterStatus = buildStatus(status);
		
		JSONObject metadata = (JSONObject) status.get("metadata");
		
		SolrInputDocument doc = new SolrInputDocument();
		
//		metadata
		
		doc.addField( "topic", metadata.get("topic"));
		doc.addField( "client", metadata.get("client"));
		doc.addField( "tokenized", metadata.get("tokenized"));
		doc.addField( "language", metadata.get("language"));
		
//		TODO
//		doc.addField( "version", metadata.get("version"));
		
		List<String> urls = (List<String>) metadata.get("urls");
		for (String url : urls) {			
			doc.addField( "urls", url);
		}
		
		List<String> mentions = (List<String>) metadata.get("mentions");
		for (String mention : mentions) {			
			doc.addField( "mentions", mention);
		}
		
		List<String> hashtags = (List<String>) metadata.get("hashtags");
		for (String hashtag : hashtags) {			
			doc.addField( "hashtags", hashtag);
		}
		
		List<String> emoticons = (List<String>) metadata.get("emoticons");
		for (String emoticon : emoticons) {			
			doc.addField( "emoticons", emoticons);
		}		
		
		doc.addField( "id", twitterStatus.getId() + "");
		doc.addField( "user_id", twitterStatus.getUser().getId() + "" );
		doc.addField( "text", twitterStatus.getText() );
		
		doc.addField( "description", twitterStatus.getUser().getDescription() );
		doc.addField( "name", twitterStatus.getUser().getName() );
		doc.addField( "screen_name", twitterStatus.getUser().getScreenName() );
		doc.addField( "location", twitterStatus.getUser().getLocation() );
		doc.addField( "user_created_at", twitterStatus.getUser().getCreatedAt());
		doc.addField( "lang", twitterStatus.getUser().getLang());
		doc.addField( "time_zone", twitterStatus.getUser().getTimeZone());
		doc.addField( "followers_count", twitterStatus.getUser().getFollowersCount());
		doc.addField( "favourites_count", twitterStatus.getUser().getFavouritesCount());
		doc.addField( "friends_count", twitterStatus.getUser().getFriendsCount());
		doc.addField( "listed_count", twitterStatus.getUser().getListedCount());
		doc.addField( "profile_background_color", twitterStatus.getUser().getProfileBackgroundColor());
		doc.addField( "profile_background_image_url", twitterStatus.getUser().getProfileBackgroundImageUrl());
		doc.addField( "profile_image_url", twitterStatus.getUser().getProfileImageURL());
		doc.addField( "profile_link_color", twitterStatus.getUser().getProfileLinkColor());
		doc.addField( "profile_sidebar_border_color", twitterStatus.getUser().getProfileSidebarBorderColor());
		doc.addField( "profile_sidebar_fill_color", twitterStatus.getUser().getProfileSidebarFillColor());
		doc.addField( "profile_text_color", twitterStatus.getUser().getProfileTextColor());
		doc.addField( "created_at", twitterStatus.getCreatedAt());
		doc.addField( "source", twitterStatus.getSource());
		doc.addField( "status_in_reply_to_screen_name", twitterStatus.getInReplyToScreenName());
		doc.addField( "status_in_reply_to_user_id", twitterStatus.getInReplyToUserId());
		doc.addField( "status_in_reply_to_status_id", twitterStatus.getInReplyToStatusId());

		if(twitterStatus.getRetweetedStatus() != null){
			doc.addField( "retweet_count", twitterStatus.getRetweetedStatus().getRetweetCount());	
		}		

		doc.addField( "statuses_count", twitterStatus.getUser().getStatusesCount());
		
	    try {
			this.server.add( doc);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			logger.error("error to index at Solr",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("error to index at Solr",e);
		}
	}
}
