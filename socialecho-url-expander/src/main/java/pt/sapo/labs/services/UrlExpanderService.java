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

    public void run() {
		logger.debug ( "computing url expander");

		long HOUR_IN_MS = 1000 * 60 * 60 * 24;
		
		Date now = new Date();
		Date endDate = now;
		Date beginDate = new Date(now.getTime() - (HOUR_IN_MS));
		
		// try {
		   DBCollection tweets = getConnection().getCollection("twitter");
		   tweets.ensureIndex(new BasicDBObject("created_at", 1));
		   
		   BasicDBObject query = new BasicDBObject("created_at", 
		                         new BasicDBObject("$gte", beginDate).append("$lt", endDate));
		   
		   
		   DBCursor cursor = tweets.find(query);
		   try {
		      while(cursor.hasNext()) {
		          DBObject tweet = cursor.next();
				  
				  BasicDBList urls = (BasicDBList) ((DBObject)tweet.get("metadata")).get("urls");
				  
				  for(Object url : urls)
				  if(urls != null){
					  
					  logger.debug("expanding  : " + url.getClass().toString());
					  URL expandedUrl = this.u.expand(new URL( (String) url)); 
					  
					  logger.debug("expanded result : " + expandedUrl.toString());					  
					  
					  // TODO
					  // update mongo collection
				  }
				  
				  // logger.debug(tweet.toString());
		      }
		   } catch(MalformedURLException e){
			   logger.error("erro expanding url",e);
		   }finally {
		      cursor.close();
		   }		  		   
    }
}
