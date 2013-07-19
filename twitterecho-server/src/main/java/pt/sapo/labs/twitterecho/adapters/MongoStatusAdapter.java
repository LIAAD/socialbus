package pt.sapo.labs.twitterecho.adapters;

import java.net.UnknownHostException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import pt.sapo.labs.utils.DataUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class MongoStatusAdapter extends BaseStatusAdapter {

	private static Logger logger = Logger.getLogger(MongoStatusAdapter.class);
	
	protected Mongo m;
	protected DB db;

	protected String collectionName;
	private DBCollection collection; 
	
	public MongoStatusAdapter(String dbName, String collection) {
		this("localhost",27017,dbName, collection);
	}

	public MongoStatusAdapter(String hostname,int portNumber , String dbName, String collectionName){
		try {
			this.collectionName = collectionName;
			this.m = new Mongo( hostname, portNumber );
			this.db = m.getDB( dbName);
			this.collection= db.getCollection(collectionName);
			
			BasicDBObject uniqueKey =new BasicDBObject();
			uniqueKey.put("topic",1);
			uniqueKey.put("id",1);
			
			this.collection.ensureIndex(uniqueKey, new BasicDBObject("unique", true));
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			logger.error("host not found error",e);
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			logger.error("mongo error",e);
		}
	}
	
	protected void insertDocument(DBObject dbObj){
		logger.info("Insert tweet on mongodb at " + collectionName + " collection");
		DBCollection coll = db.getCollection(collectionName);
		
		coll.insert(dbObj);
	}
	
	@Override
	public void onStatus(JSONObject status) {
		// TODO Auto-generated method stub

		String createdAtString = (String) status.get("created_at");
		
		JSONObject user = (JSONObject) status.get("user");
		String userCreatedAtString = (String) user.get("created_at");
		
		try {
			Date createdAt = DataUtil.fromTwitterDateString(createdAtString);
			Date userCreatedAt = DataUtil.fromTwitterDateString(userCreatedAtString);
			
			String rawJson = rawJson(status);
			DBObject dbObj = (DBObject) JSON.parse(rawJson); 
			
			dbObj.put("created_at", createdAt);
			
			DBObject dbUser = (DBObject) dbObj.get("user");
			dbUser.put("created_at", userCreatedAt);
			
			dbObj.removeField("user");
			dbObj.put("user", dbUser);
			
			insertDocument(dbObj);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("error to parse twitter date",e);
		}
		
	}
}
