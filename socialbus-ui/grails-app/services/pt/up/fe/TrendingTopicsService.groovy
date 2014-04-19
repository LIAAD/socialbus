package pt.up.fe

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.Mongo;
import com.mongodb.*;


class TrendingTopicsService {

    def static dbConnection

    def grailsApplication


    def getConnection() {
        if(dbConnection){
            return dbConnection;
        }

        //TODO configurar cluster
//        def cluster = Arrays.asList(new ServerAddress("localhost", 27017),
//                new ServerAddress("localhost", 27018),
//                new ServerAddress("localhost", 27019))

        def mongoHost = grailsApplication.config.socialecho.mongodb.host
        def mongoPort = grailsApplication.config.socialecho.mongodb.port
        def mongoDB = grailsApplication.config.socialecho.mongodb.database

        MongoClient mongoClient = new MongoClient(mongoHost,Integer.parseInt(mongoPort));

        DB db = mongoClient.getDB( mongoDB );

        return db;
    }
	
    def execute() {
		print "computing top hashtags"
		print "prepare to run mapreduce"
		
		try {

		   DBCollection tweets = getConnection().getCollection("twitter");
/*		   tweets.ensureIndex({"created_at", 1});*/
		   tweets.ensureIndex(new BasicDBObject("created_at", 1));
		   
		   String map = """
		   function () {
		       if("entities" in this){
		           hashtags = this.entities.hashtags;

		           for ( var i = 0 ; i < hashtags.length ; i++ ) {
		             var hashtag = hashtags[i].text.toLowerCase();
		             //var topic = this.metadata.topic;

		             //emit({"topic":topic,"hashtag":hashtag},{count:1});
		             emit({"hashtag":hashtag},{count:1});
		           }
		       }
		   }
		   """;
		      
		   String reduce = """
		   function(key, values) {
		   var count = 0;
		     values.forEach(function(v) {
		       count += v['count'];
		     });
		     return {count: count};
		   }
		   """;
		   
		  
		  /*aplicar query para ultima hora*/
		   MapReduceCommand cmd = new MapReduceCommand(tweets, map, reduce,
		     null, MapReduceCommand.OutputType.INLINE, null);

		   MapReduceOutput out = tweets.mapReduce(cmd);

		   DBCollection topHashtagsCollection = getConnection().getCollection("trending_hashtags");
/*		   topHashtagsCollection.ensureIndex({"value.count",1});*/
		   topHashtagsCollection.ensureIndex(new BasicDBObject("value.count", 1));
		   
		   /*clean data*/
		   BasicDBObject query = new BasicDBObject();
		   topHashtagsCollection.remove(query);
		   
		   for (DBObject o : out.results()) {
		    System.out.println(o.toString());
			topHashtagsCollection.insert(o);
		   }
		  } catch (Exception e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
    }
}
