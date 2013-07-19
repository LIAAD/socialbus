package pt.sapo.labs.crawl.twitter;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import twitter4j.Status;

@JsonAutoDetect(fieldVisibility=Visibility.NON_PRIVATE)
public class TwitterEchoStatus {
	
	private Status status;
	
	public long id;
	public String name;
	public String screen_name;
	public Date created_at;
	public String location;
	public String description;
	public String profile_image_url;
	public String url;
	public String time_zone;
	public int verified;
	
	@JsonProperty("protected")
	public int user_protected; //protected
	
	public int statuses_count;
	public int followers_count;
	public int friends_count;
	public int status_exists;
	public long status_status_id;
	public Date status_created_at;
	public String status_text;
	public String status_source;
	public int status_truncated;
	public long status_in_reply_to_status_id;
	public long status_in_reply_to_user_id;
	public String status_in_reply_to_screen_name;
	public long status_retweet_count;

	public TwitterEchoStatus(Status status) {
		super();
		this.status = status;
		
		this.id = status.getUser().getId();
		this.name = status.getUser().getName();
		this.screen_name = status.getUser().getScreenName();
		this.created_at = status.getUser().getCreatedAt();
		this.location = status.getUser().getLocation();
		this.description = status.getUser().getDescription();
		this.profile_image_url = status.getUser().getProfileImageURL() != null ? status.getUser().getProfileImageURL().toString() : "";
		this.url = status.getUser().getURL() != null ? status.getUser().getURL().toString() : "";
		this.time_zone = status.getUser().getTimeZone();
		this.statuses_count = status.getUser().getStatusesCount();
		this.followers_count = status.getUser().getFollowersCount();
		this.friends_count = status.getUser().getFriendsCount();
		this.status_status_id = status.getId();
		this.status_created_at = status.getCreatedAt();
		this.status_text = status.getText();
		this.status_source = status.getSource();
		this.status_in_reply_to_status_id = status.getInReplyToStatusId();
		this.status_in_reply_to_user_id = status.getInReplyToUserId();
		this.status_in_reply_to_screen_name= status.getInReplyToScreenName();
		this.status_retweet_count = status.getRetweetCount();

		this.user_protected = status.getUser().isProtected() ? 1 : 0;
		this.status_truncated = status.isTruncated() ? 1 : 0;
		this.verified = status.getUser().isVerified() ? 1 : 0; 
		this.status_exists = 1;
	}	
	
	public String toJson(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE); // auto-detect all member fields
		
		String json = "";
		
		try {
			// display to console
			json = mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "["+json+"]";
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		
		sb.append("Status").append("\n");
		sb.append("id: ").append(status.getId()).append("\n");
		sb.append("text: ").append(status.getText()).append("\n");
		sb.append("created_at: ").append(status.getCreatedAt()).append("\n");
		sb.append("\n");
		sb.append("User").append("\n");
		sb.append("user_id: ").append(status.getUser().getId()).append("\n");
		sb.append("screen_name: ").append(status.getUser().getScreenName()).append("\n");
		sb.append("name: ").append(status.getUser().getName()).append("\n");
		sb.append("location: ").append(status.getUser().getLocation()).append("\n");
		sb.append("time zone: ").append(status.getUser().getTimeZone()).append("\n");
		sb.append("description: ").append(status.getUser().getDescription()).append("\n");
		sb.append("\n");
		sb.append("User numbers: ").append("\n");
		sb.append("statuses_count: ").append(status.getUser().getStatusesCount()).append("\n");
		sb.append("followers_count: ").append(status.getUser().getFollowersCount()).append("\n");
		sb.append("friends_count: ").append(status.getUser().getFriendsCount()).append("\n");
		sb.append("listed_count: ").append(status.getUser().getListedCount()).append("\n");
		
		return sb.toString();
	}
}
