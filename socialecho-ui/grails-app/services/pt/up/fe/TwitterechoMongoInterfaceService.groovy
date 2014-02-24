package pt.up.fe

import com.mongodb.util.JSON;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

class TwitterechoMongoInterfaceService {

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


    def indexDocument(def document, def collection){
        log.debug( " Inserting document to database ")

        DBCollection coll = getConnection().getCollection(collection);

        //Inserting documents to database
        BasicDBObject obj = (BasicDBObject) JSON.parse(document.toString());
        coll.insert((DBObject)obj);

//        getConnection().close()
    }
}
