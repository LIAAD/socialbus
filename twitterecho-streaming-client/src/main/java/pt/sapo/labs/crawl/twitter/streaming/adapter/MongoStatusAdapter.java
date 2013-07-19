package pt.sapo.labs.crawl.twitter.streaming.adapter;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import twitter4j.json.DataObjectFactory;
import twitter4j.Status;

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

	protected String collection;
	
	public MongoStatusAdapter(String dbName, String collection) {
		this("localhost",27017,dbName, collection);
	}

	public MongoStatusAdapter(String hostname,int portNumber , String dbName, String collection){
		try {
			this.collection = collection;
			m = new Mongo( hostname, portNumber );
			db = m.getDB( dbName);
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void insertDocument(String json){
		logger.info("Insert tweet on mongodb at " + collection + " collection");
		DBCollection coll = db.getCollection(collection);
		
		DBObject doc = (DBObject) JSON.parse(json);		
		coll.insert(doc);
	}
	
	@Override
	public void onStatus(Status status) {
		super.onStatus(status);
		
		String json = DataObjectFactory.getRawJSON(status);
		insertDocument(json);
	}
}
