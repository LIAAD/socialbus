package pt.up.fe

import grails.plugin.springsecurity.annotation.Secured
import com.mongodb.*;


@Secured(['ROLE_ADMIN', 'ROLE_USER'])
class HomeController {


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

    def index() {
		def trendingTopics = []
		def trendingUsers = []
		
		DBCollection topHashtagsCollection = getConnection().getCollection("trending_hashtags");
		DBCursor topicsCursor = topHashtagsCollection.find().sort(new BasicDBObject("value.count",-1)).limit(10)
		
		while(topicsCursor.hasNext() )
		{
			def topic = topicsCursor.next();
			trendingTopics.add(topic)
/*			System.out.println(topic);			*/
		}
		
		DBCollection topUsersCollection = getConnection().getCollection("trending_users");
		DBCursor usersCursor = topUsersCollection.find().sort(new BasicDBObject("value.count",-1)).limit(10)
		
		while(usersCursor.hasNext() )
		{
			def user = usersCursor.next();
			trendingUsers.add(user)
/*			System.out.println(user);			*/
		}	
		
		print trendingTopics[0]
		print trendingUsers[0]
		
		return [trendingTopics:trendingTopics,
				trendingUsers:trendingUsers
				]
	
	}
}
