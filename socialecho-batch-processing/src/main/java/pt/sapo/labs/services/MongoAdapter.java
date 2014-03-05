package pt.sapo.labs.services;

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

import org.apache.commons.configuration.Configuration;
/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 30/01/14
 * Time: 07:04
 * To change this template use File | Settings | File Templates.
 */
public class MongoAdapter{

    private static Logger logger = LoggerFactory.getLogger(MongoAdapter.class);

    protected MongoClient mongoClient;

    protected String host;
    protected int port;
    protected String databaseName;
    protected DB database;
	
	protected Configuration configuration;

	public MongoAdapter(Configuration configuration){
		this.configuration = configuration;
		initialize();
	}

    public void initialize() {
        logger.info("Initialising MongoDB adapter");

        host = this.configuration.getString("mongodb.host","localhost").trim();
        port = this.configuration.getInt("mongodb.port", 27017);
        databaseName = this.configuration.getString("mongodb.database","socialecho").trim();

        try {
            this.mongoClient = new MongoClient( this.host , this.port);
            this.database = mongoClient.getDB( this.databaseName );

		//	Authentication
		//  boolean auth = db.authenticate(myUserName, myPassword);

        } catch (UnknownHostException e) {
            logger.error("Error to connect to mongodb",e);
        }
    }	
	
	public DB getDatabase(){
		return this.database;
	}
	    
}
