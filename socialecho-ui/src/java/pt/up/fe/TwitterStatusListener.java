package pt.up.fe;

import groovy.util.logging.Log4j;
import pt.up.fe.Scope;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.json.DataObjectFactory;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.util.HashMap;
import java.util.Map;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 4/17/13
 * Time: 12:14 AM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
public class TwitterStatusListener implements StatusListener {

    Scope scope;

	Map<String, Object> configuration = new HashMap<String, Object>();

	protected MongoClient mongoClient;

	
	
	protected String host;
	protected String port;
	protected String databaseName;
	protected DB database;
	
	// TwitterechoMongoInterfaceService twitterechoMongoInterfaceService;

	private Map<String, Object> getConfiguration(){
		return this.configuration;
	}

    public TwitterStatusListener(Scope scope,Map configuration){
        this.scope = scope;
		this.configuration = configuration;
		
		initialize();
    }

	private void persistObjectOnCollection(String json, String collectionName) {
        System.out.println("persisting object on collection " + collectionName);

        DBCollection collection = database.getCollection(collectionName);
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);


        DBObject dbObject = (DBObject) JSON.parse(json);
		Long objId = (Long) dbObject.get("id");
		dbObject.put("_id",objId);
		
        collection.insert(dbObject);
    }
	
    public void initialize() {
        System.out.println("Initialising MongoDB adapter");

        host = (String) this.getConfiguration().get("mongodb.host");
        port = (String) this.getConfiguration().get("mongodb.port");
		databaseName = (String) this.getConfiguration().get("mongodb.database");
        		
        try {
            this.mongoClient = new MongoClient( this.host , Integer.parseInt(this.port));
            this.database = mongoClient.getDB( this.databaseName );
			
			// DBCollection collection = database.getCollection(collectionName);
			// collection.ensureIndex( { comments: "text" } )
				
//                Authentication
//                boolean auth = db.authenticate(myUserName, myPassword);

        } catch (UnknownHostException e) {
            System.out.println("Error to connect to mongodb");
			e.printStackTrace();
        }
    }


    @Override
    public void onStatus(Status status) {
        //To change body of implemented methods use File | Settings | File Templates.

        System.out.println("New status for scope : " + this.scope.getName());
        System.out.println(status.getUser().getScreenName() + " said:");
        System.out.println(status.getText());
        System.out.println("------------------------");
		
		String rawJson = DataObjectFactory.getRawJSON(status);
		
		persistObjectOnCollection(rawJson,"tweets");
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("deletion notice: " + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("on Track Limitation Notice: " + i);
    }

    @Override
    public void onScrubGeo(long l, long l2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onException(Exception e) {
        //To change body of implemented methods use File | Settings | File Templates.
        e.printStackTrace();
    }
}
