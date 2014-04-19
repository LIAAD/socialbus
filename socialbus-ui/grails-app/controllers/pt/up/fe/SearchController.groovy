package pt.up.fe

import grails.plugin.springsecurity.SpringSecurityUtils

import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import grails.plugin.springsecurity.annotation.Secured;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.response.FacetField;

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.*

@Secured(['ROLE_ADMIN', 'ROLE_USER','ROLE_TWITTER'])
class SearchController {

    def static dbConnection

    def grailsApplication
	
	def searchService

	/**
     * Index page with search form and results
     */
    def index = {
		println "search controller"
		def query = params.q
        
        if (!params.q?.trim()) {
			//redirect(controller: "home", action: "index")
			return "retorna vazio"
           return [:]
/*          query =  "*:*"*/
        }
        
        try {
			def queryResp = searchService.search(query, params)
			def queryTime = queryResp.getQTime()

			def docs = queryResp.getResults();
			def numFound = docs.getNumFound() 
			def facetFields = queryResp.getFacetFields()
			
			println "controller results ${numFound}"

			def facetCreatedAt = queryResp.getFacetDate("created_at")
			def facetRanges = queryResp.getFacetRanges()
			
			println "########################################################"
			println "facetRanges : ${facetRanges}"
			println "########################################################"
			

/*			print "facetFields : " + facetFields*/
			
			def facetUsers = []
			def facetDates = []
			
			println "########################################################"
			facetFields.each{
				print it.name
				def values = it.getValues()
/*				print values*/
				values.each{
					facetUsers.add([name:it.getName(),count:it.getCount()])
				}
			}
			println "########################################################"
			
			println "########################################################"
			facetRanges.each{
				println it.name
				println it.getClass()
				def values = it.getCounts()
				print values
				
				values.each{
					println it
					println it.getCount()
					facetDates.add([timestamp:it.getValue(),value:it.getCount()])
				}
			}

/*			print "facetDates"*/
/*			print facetDates*/
			
			def trendlineJson = (facetDates as JSON).toString()
			println "########################################################"
			println trendlineJson
			println "########################################################"
            return [results:docs,
					numFound:numFound,
					queryTime:queryTime,
					facetUsers:facetUsers,
					trendlineJson:trendlineJson]
        } catch (Exception ex) {
            return [parseException: true]
        }
    }

/*    def getConnection() {
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
		
		def query = params.q
        
	        if (!params.q?.trim()) {
				//redirect(controller: "home", action: "index")
	           return [:]
	        }
		
		try {
			def docs = []
			def numFound = []
			
			final DBObject textSearchCommand = new BasicDBObject();
		    textSearchCommand.put("text", "tweets");
			textSearchCommand.put("limit", "25");
		    textSearchCommand.put("search", params.q);			
		
		    final CommandResult commandResult =  getConnection().command(textSearchCommand);
		
		
			BasicDBList results = (BasicDBList)commandResult.get("results");

			for (Iterator <Object> it = results.iterator();it.hasNext();)
			{
			    BasicDBObject result  = (BasicDBObject) it.next();
			    BasicDBObject dbo = (BasicDBObject) result.get("obj");
			    System.out.println(dbo.getString("text"));
				
				docs.add(dbo);				
			}
		
			print  numFound = commandResult.get("stats").get("nfound")
			print numFound
						
		    return [results:docs,
					numFound:numFound]
					
	    } catch (Exception ex) {
	               return [parseException: true]
	           }			
	}
	
	
    public void initialize() {
        System.out.println("Initialising MongoDB adapter");

        host = (String) this.getConfiguration().get("mongodb.host");
        port = (String) this.getConfiguration().get("mongodb.port");
		databaseName = (String) this.getConfiguration().get("mongodb.database");
        		
        try {
            this.mongoClient = new MongoClient( this.host , Integer.parseInt(this.port));
            this.database = mongoClient.getDB( this.databaseName );
			
			DBCollection collection = database.getCollection("tweets");
			collection.ensureIndex( { comments: "text" } )
				
        } catch (UnknownHostException e) {
            System.out.println("Error to connect to mongodb");
			e.printStackTrace();
        }
    }*/
}
