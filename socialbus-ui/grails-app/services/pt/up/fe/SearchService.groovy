package pt.up.fe

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.MultipartEntity;

import java.util.regex.Matcher
import java.util.regex.Pattern

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.solr.util.DateMathParser;
import java.util.TimeZone;

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

class SearchService {

	boolean transactional = false
	
	SolrServer solrServer 

	def getSolrServer(){
		println "getSolrServer()"
		try {
			if(!this.solrServer){
				
				println "create SolrServer client"
				
				/*if(ConfigurationHolder.config.socialecho.solr.authentication.user != null){
					
					println "create SolrServer client 1."
					
					println "solr endpoint : ${ConfigurationHolder.config.socialecho.solr.endpoint}"	
					println "solr user : ${ConfigurationHolder.config.socialecho.solr.authentication.user}"	
					println "solr password : ${ConfigurationHolder.config.socialecho.solr.authentication.password}"	
					
					DefaultHttpClient httpclient = new DefaultHttpClient();
					
				    httpclient.getCredentialsProvider().setCredentials(
						AuthScope.ANY, new UsernamePasswordCredentials(
								ConfigurationHolder.config.socialecho.solr.authentication.user,
								ConfigurationHolder.config.socialecho.solr.authentication.password));
					
					println "httpclient : ${httpclient}"
					println "create SolrServer client 2. with auth"			
					this.solrServer = new HttpSolrServer(ConfigurationHolder.config.socialecho.solr.endpoint,httpclient);
				}else{*/
					println "create SolrServer client 3. no auth"
					this.solrServer = new HttpSolrServer(ConfigurationHolder.config.socialecho.solr.endpoint);
/*				}*/
				
			}				
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	def extractHashtags (String text){
		def hashtagPattern = ~/(?:\s|\A)[##]+([A-Za-z0-9-_]+)/
		def tokens = text.split(" ")

		def hashtags = tokens.findAll { it ==~ hashtagPattern }

		return hashtags
	}

	/**
     * Returns a SearchResult object for the given query and options
     *
     * @param query the query string
     * @param options an optional Map of options, which may contain a "page" number and/or a requested result "size"
     * @return a SearchResult object
     */
    def search(Object[] args) {
		println "search"
		getSolrServer()

		println args
		String queryStatement = args[0]

		println queryStatement
		def filterQuery = ""

		def params = args[1] 
			
		def defaultDateRange = "48HOUR" 
		def defaultDateGap = "+1HOUR" 
		def dateGap = defaultDateGap
		
		if(!params.date_range) {
			params.date_range = defaultDateRange
		}
		
		DateMathParser dtParser = new DateMathParser(TimeZone.getDefault(),Locale.UK)
		Date dateRangeEnd = new Date()
	    
	    Date dateRangeStart = dtParser.parseMath("-48HOUR")
	    

	    println "dateRangeStart : " + dateRangeStart
		println "dateRangeEnd : " + dateRangeEnd		

		if(params.date_range.equalsIgnoreCase("all_times")){
			def defaultAllTimesDateRange = "180DAY"
			filterQuery+= " created_at:[NOW-${defaultAllTimesDateRange} TO NOW]"
			dateRangeStart = dtParser.parseMath("-${defaultAllTimesDateRange}")			
		}else{
			filterQuery+= " created_at:[NOW-${params.date_range} TO NOW]"
			dateRangeStart = dtParser.parseMath("-${params.date_range}")
		}

		switch(params.date_range) {
				case "all_times": dateGap = "+1MONTH"; break;
				case "1HOUR": dateGap = "+1MINUTE"; break;
				case "48HOUR": dateGap = "+30MINUTES"; break;
				case "7DAY": dateGap = "+6HOUR"; break;
				case "30DAY": dateGap = "+1DAY"; break;					
				break
			}

		println "dateGap : " + dateGap
				
		if(!params.lang) {
			params.lang = "all"
		}
		
		if(!params.lang.equalsIgnoreCase("all")){
			filterQuery+= " AND lang:" + params.lang
		}
		
		if(params.screen_name != null && params.screen_name != "all")
			filterQuery+= " AND screen_name:" + params.screen_name
		
		println queryStatement

		println "dateRangeStart : " + dateRangeStart
		println "dateRangeEnd : " + dateRangeEnd		
	      
		SolrQuery query = new SolrQuery().
	    		setQuery( queryStatement).
        		setFacet(true).
				setFacetMinCount(1).
				
				setFacetLimit(8).
				addFilterQuery(filterQuery).
				addDateRangeFacet("created_at",dateRangeStart,dateRangeEnd, dateGap).
				addFacetField("screen_name").
				addSortField( "created_at", SolrQuery.ORDER.desc ).
				addSortField( "score", SolrQuery.ORDER.desc );
		
		println "query : " + query
		
		println "args.offset : " + params.offset
/*		println "args.offset 0 : " + args.offset[0]
		println "args.offset 1 : " + args.offset[1]
		println "args.offset 0 class : " + args.offset[0].class*/		
/*		println "args.offset 1 class : " + args.offset[1].class*/
		
		println "query -> " + query
		if(params.offset){
			query.setStart(Integer.parseInt(params.offset))
		}
		
		println "query -> " + query
		
		QueryResponse rsp = this.solrServer.query( query );

		println "result : $rsp"

/*		print rsp*/
		return 	rsp;
    }
	
	
    def mapsSearch(Object[] args) {
		println "maps search"
		getSolrServer()

		println args
		String queryStatement = args[0]

		println queryStatement
		def filterQuery = "{!geofilt pt=42.35327148,-71.05639639 sfield=geo_location d=500}"

		def params = args[1] 
			
		def defaultDateRange = "48HOUR" 
		def defaultDateGap = "+1HOUR" 
		def dateGap = defaultDateGap
		
		if(!params.date_range) {
			params.date_range = defaultDateRange
		}
		
		DateMathParser dtParser = new DateMathParser(TimeZone.getDefault(),Locale.UK)
		Date dateRangeEnd = new Date()
	    Date dateRangeStart = dtParser.parseMath("-48HOUR")
	    
	    println "dateRangeStart : " + dateRangeStart
		println "dateRangeEnd : " + dateRangeEnd		

		if(params.date_range.equalsIgnoreCase("all_times")){
			def defaultAllTimesDateRange = "180DAY"
			filterQuery+= " created_at:[NOW-${defaultAllTimesDateRange} TO NOW]"
			dateRangeStart = dtParser.parseMath("-${defaultAllTimesDateRange}")			
		}else{
			filterQuery+= " created_at:[NOW-${params.date_range} TO NOW]"
			dateRangeStart = dtParser.parseMath("-${params.date_range}")
		}
		
		println queryStatement

		println "dateRangeStart : " + dateRangeStart
		println "dateRangeEnd : " + dateRangeEnd		
	      
		SolrQuery query = new SolrQuery().
	    		setQuery( queryStatement).
        		addFilterQuery(filterQuery).
				addSortField( "created_at", SolrQuery.ORDER.desc ).
				addSortField( "score", SolrQuery.ORDER.desc );
		
		println "query : " + query
		
		println "args.offset : " + params.offset
		println "query -> " + query
		if(params.offset){
			query.setStart(Integer.parseInt(params.offset))
		}
		
		println "query -> " + query
		println "debug.1"
		QueryResponse rsp = this.solrServer.query( query );
		println "debug.2"
		println "result : $rsp"

/*		print rsp*/
		return 	rsp;
    }
	
	def loadTweetsStats(dateRangeStartStr = "-48HOUR"){
		println "loadTweetsByHour"
		
		getSolrServer()

		DateMathParser dtParser = new DateMathParser(TimeZone.getDefault(),Locale.UK)
		Date dateRangeEnd = new Date()	   
		
	    Date dateRangeStart = dtParser.parseMath(dateRangeStartStr)
		
		
		def dateGap = "+1HOUR"
		
		if(dateRangeStartStr.equalsIgnoreCase("-1HOUR")){
			dateGap = "+1MINUTE"
		}
		
		SolrQuery query = new SolrQuery().
	    		setQuery( "created_at:[NOW"+ dateRangeStartStr +" TO NOW]").
        		setFacet(true).
				addDateRangeFacet("created_at",dateRangeStart,dateRangeEnd, dateGap);
				
		print query		
		
		QueryResponse rsp = this.solrServer.query( query );
		print rsp
		return 	rsp;
	}		
}
