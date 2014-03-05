package pt.sapo.labs.services;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.sapo.labs.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.Mongo;
import com.mongodb.*;
import org.apache.commons.io.Charsets;


import com.google.common.io.Resources;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class TwitterTrendingTopicsService implements Runnable
{

    private static Logger logger = LoggerFactory.getLogger(TwitterTrendingTopicsService.class);
    
	protected CompositeConfiguration config;
	protected DB databaseConnection;
	
    public TwitterTrendingTopicsService(CompositeConfiguration config){
        this.config = config;
    }
	
    private DB getConnection() {
        if(databaseConnection == null){
        	databaseConnection = new MongoAdapter(this.config).getDatabase();
        }
		
        return databaseConnection;
    }

    public void run() {
		logger.debug ( "computing top hashtags");
		logger.debug ("prepare to run mapreduce");
		
		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		
		Date now = new Date();
		Date endDate = now;
		Date beginDate = new Date(now.getTime() - (DAY_IN_MS));
		
		try {
		   DBCollection tweets = getConnection().getCollection("twitter");
		   tweets.ensureIndex(new BasicDBObject("created_at", 1));
		   
		   BasicDBObject query = new BasicDBObject("created_at", 
		                         new BasicDBObject("$gte", beginDate).append("$lt", endDate));
		   
		   String mapFunction = Resources.toString(
			   Resources.getResource("mapreduce/MapTwitterTrendingTopics.js"), Charsets.UTF_8);
		      
		   String reduceFunction = Resources.toString(
			   Resources.getResource("mapreduce/ReduceTwitterTrendingTopics.js"), Charsets.UTF_8);
		   
		   /*aplicar query para ultima hora*/
		   MapReduceCommand cmd = new MapReduceCommand(tweets, mapFunction, reduceFunction,
		     null, MapReduceCommand.OutputType.INLINE, query);

		   MapReduceOutput out = tweets.mapReduce(cmd);

		   DBCollection topHashtagsCollection = getConnection().getCollection("trending_hashtags");
		   topHashtagsCollection.ensureIndex(new BasicDBObject("value.count", 1));
		   
		   /*clean data*/
		   BasicDBObject queryResultCol = new BasicDBObject();
		   topHashtagsCollection.remove(queryResultCol);
		   
		   for (DBObject o : out.results()) {
		    logger.debug(o.toString());
			topHashtagsCollection.insert(o);
		   }
		  } catch (Exception e) {
		   // TODO Auto-generated catch block
		   logger.error("error when running mapreduce",e);
		  }
    }
}
