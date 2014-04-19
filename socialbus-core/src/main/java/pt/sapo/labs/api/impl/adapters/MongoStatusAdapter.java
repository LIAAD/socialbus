package pt.sapo.labs.api.impl.adapters;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 30/01/14
 * Time: 07:04
 * To change this template use File | Settings | File Templates.
 */
public class MongoStatusAdapter extends AbstractStatusAdapter {

    private static Logger logger = LoggerFactory.getLogger(MongoStatusAdapter.class);

    protected MongoClient mongoClient;

    protected String host;
    protected int port;
    protected String databaseName;
    protected boolean active;
    protected DB database;

    @Override
    public void initialize() {
        logger.info("Initialising MongoDB adapter");

        host = this.getConfiguration().getString("mongodb.host","localhost").trim();
        port = this.getConfiguration().getInt("mongodb.port", 27017);
        databaseName = this.getConfiguration().getString("mongodb.database","socialecho").trim();
        active = this.getConfiguration().getBoolean("mongodb.active",true);

        if(active){

            try {
                this.mongoClient = new MongoClient( this.host , this.port);
                this.database = mongoClient.getDB( this.databaseName );

//                Authentication
//                boolean auth = db.authenticate(myUserName, myPassword);
                this.setEnabled(true);

            } catch (UnknownHostException e) {
                logger.error("Error to connect to mongodb",e);
                this.setEnabled(false);
            }


        }
    }

    @Override
    public void onStatus(JSONObject status) {
        super.onStatus(status);

        String jsonString = parseJsonString(status);

//        JSONObject metadata = (JSONObject) status.get("metadata");

        this.onStatus(jsonString);
    }

    public void onStatus(String json,String service) {
        if(!this.isEnabled()) {
            return;
        }

        try {
            persistObjectOnCollection(json,service);

            return;
        } catch (Exception e) {
            logger.error("error to persist data on mongodb",e);
        }

    }

    @Override
    public void onStatus(String json) {

        if(!this.isEnabled()) {
            return;
        }

        JSONObject jsonObj = (JSONObject) JSONValue.parse(json);

        String service = (String)((JSONObject) jsonObj.get("metadata")).get("service");

        onStatus(json,service);
    }

    @Override
    public void onStatus(twitter4j.internal.org.json.JSONObject status) {
        super.onStatus(status);

        String jsonString = status.toString();

        this.onStatus(jsonString,"twitter");
    }

    private void persistObjectOnCollection(String json, String collectionName) {
        logger.debug("persisting object on collection " + collectionName);

        DBCollection collection = database.getCollection(collectionName);
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);

        DBObject dbObject = (DBObject) JSON.parse(json);

        if(collectionName.equals("twitter")){

            if(dbObject.containsField("retweeted_status")){
                DBObject retweetedStatusObject = (DBObject) dbObject.get("retweeted_status");
                retweetedStatusObject = adaptTweetDatesValues(retweetedStatusObject);
                dbObject = updateField(dbObject,retweetedStatusObject,"retweeted_status");
            }

            dbObject = adaptTweetDatesValues(dbObject);

        }else if(collectionName.startsWith("facebook")){

            dbObject = adaptFacebookDatesValues(dbObject);
        }

        collection.insert(dbObject);
    }

    private DBObject adaptTweetDatesValues(DBObject dbObject) {
        DBObject userObj = adaptDateField((DBObject) dbObject.get("user"),"created_at",twitterDateFormatter);
        dbObject = adaptDateField(dbObject,"created_at",twitterDateFormatter);
        dbObject = updateField(dbObject,userObj,"user");
        return dbObject;
    }

    private DBObject adaptFacebookDatesValues(DBObject dbObject) {

        dbObject = adaptDateField(dbObject,"updated_time",facebookDateFormatter);
        dbObject = adaptDateField(dbObject,"created_time",facebookDateFormatter);

        return dbObject;
    }

    private DBObject updateField(DBObject dbObject,DBObject newObject,String fieldName){
        dbObject.removeField(fieldName);
        dbObject.put(fieldName,newObject);

        return dbObject;
    }
    private DBObject adaptDateField(DBObject dbObject,String fieldName,SimpleDateFormat dateFormat){
        Date createdAt = convertDateString((String) dbObject.get(fieldName),dateFormat);

        dbObject.removeField(fieldName);
        dbObject.put(fieldName,createdAt);

        return  dbObject;
    }

    private static String LARGE_TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
	private static String LARGE_FACEBOOK_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	// 2013-11-27T14:56:53+0000

    //    Mon May 31 13:49:30 +0000 2010
    private static SimpleDateFormat twitterDateFormatter = new SimpleDateFormat(LARGE_TWITTER_DATE_FORMAT, Locale.ENGLISH);
	private static SimpleDateFormat facebookDateFormatter = new SimpleDateFormat(LARGE_FACEBOOK_DATE_FORMAT, Locale.ENGLISH);

    private Date convertDateString(String dateString, SimpleDateFormat dateFormat){
        Date result = new Date();

        try{
            result = dateFormat.parse(dateString);
        } catch (ParseException e){
            logger.warn("Error to parse date");
        }
        return result;

    }
}
