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
import com.mongodb.DBCursor;
import com.mongodb.*;
import org.apache.commons.io.Charsets;


import com.google.common.io.Resources;
import java.util.Date;

import com.mongodb.BasicDBList;

import net.cpdomina.webutils.URLUnshortener;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Hello world!
 *
 */
public class UrlExpanderService implements Runnable
{

    private static Logger logger = LoggerFactory.getLogger(UrlExpanderService.class);
    
	protected CompositeConfiguration config;
	protected DB databaseConnection;
	
	private URLUnshortener u;
	
    public UrlExpanderService(CompositeConfiguration config){
        this.config = config;
		this.u = new URLUnshortener(3000, 3000, 10000);
    }
	
    private DB getConnection() {
        if(databaseConnection == null){
        	databaseConnection = new MongoAdapter(this.config).getDatabase();
        }
		
        return databaseConnection;
    }

	private boolean processUrls(DBObject tweet, DBCollection expandedUrls, DBCollection tweets){

	  
		try{
	  	  BasicDBList urls = (BasicDBList) ((DBObject)tweet.get("metadata")).get("urls");
	  
	  	  for(Object url : urls)
	  	  if(urls != null){
	  		  Object topic = ((DBObject)tweet.get("metadata")).get("topic");
		  
	  		  logger.debug("expanding  : " + url.getClass().toString());
	  		  URL expandedUrl = this.u.expand(new URL( (String) url)); 
		  
	  		  logger.debug("expanded result : " + expandedUrl.toString());					  
		  
		      BasicDBObject expandedUrlId = new BasicDBObject();
		  	  expandedUrlId.put("short_url", (String) url);
			  expandedUrlId.put("doc_id", tweet.get("_id"));
			  	  
	  		  BasicDBObject expandedUrlDetails = new BasicDBObject();
	  		  	
	  			expandedUrlDetails.put("url", (String) expandedUrl.toString());
	  		  	expandedUrlDetails.put("created_at", new Date());
	  			expandedUrlDetails.put("service", "twitter");
  			    expandedUrlDetails.put("topic", topic);
			  	expandedUrlDetails.put("_id",expandedUrlId);
  			    
			  	expandedUrls.insert(expandedUrlDetails);
		  
	  		  // update mongo collection
	  		  BasicDBObject set = new BasicDBObject();
	  		  set.append("$set", new BasicDBObject("metadata.expanded_urls", true));
	  		  tweets.update(new BasicDBObject("_id", tweet.get("_id")), set);
		  
	  		  return true;
	  	  }
		  
		} catch(MalformedURLException e){
			   logger.error("erro expanding url",e);
			   return false;
		}
	  
	  
	  	return false;
	}

    public void run() {
		logger.debug ( "computing url expander");

		long HOUR_IN_MS = 1000 * 60 * 60 * 24;
		
		Date now = new Date();
		Date endDate = now;
		Date beginDate = new Date(now.getTime() - (HOUR_IN_MS));
		
		// try {
		   DBCollection expandedUrls = getConnection().getCollection("expanded_urls");
		   expandedUrls.ensureIndex(new BasicDBObject("created_at", 1));
		   expandedUrls.ensureIndex(new BasicDBObject("topic", 1));
		   expandedUrls.ensureIndex(new BasicDBObject("service", 1));
		   expandedUrls.ensureIndex(new BasicDBObject("short_url", 1));
		   expandedUrls.ensureIndex(new BasicDBObject("doc_id", 1));
		   
		   DBCollection tweets = getConnection().getCollection("twitter");
		   tweets.ensureIndex(new BasicDBObject("created_at", 1));
		   tweets.ensureIndex(new BasicDBObject("metadata.expanded_urls", 1));
		   	   
		   BasicDBObject query = new BasicDBObject();
		   query.put("created_at", new BasicDBObject("$gte", beginDate).append("$lt", endDate));
		   query.put("metadata.expanded_urls",new BasicDBObject("$exists", false));
		   
		   // DBObject query = new BasicDBObject("metadata.expanded_urls", 
// 			   new BasicDBObject("$exists", false));
    
		   DBCursor cursor = tweets.find(query);
		   try {
		      while(cursor.hasNext()) {
		             DBObject tweet = cursor.next();
					 
					 if(!processUrls(tweet,expandedUrls,tweets)){
						 continue;
					 }
		      }
		  }finally {
		      cursor.close();
		   }		  		   
    }
}
