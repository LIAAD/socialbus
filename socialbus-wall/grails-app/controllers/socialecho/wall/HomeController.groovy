package socialecho.wall

import grails.converters.JSON
import org.ocpsoft.prettytime.PrettyTime
import groovy.json.JsonSlurper


class HomeController {

	//MEMCACHE
	def BASE_URL = "http://reaction.fe.up.pt/cgi-bin/twitterecho/get_tweets.py"
	
/*	SearchTweetsService searchTweetsService*/

    def prettyTime = new PrettyTime()

    def loadNewPosts(){
    	def sort = params.sort ?: "recent";

    	int oneMinuteBehind = (System.currentTimeMillis() - 60000);
    	def timestamp = params.int("timestamp") ?: oneMinuteBehind;

		return render(contentType: "text/json") { [status: 1, timestamp: timestamp] }
    }

    def loadPosts(){
    	//cache(shared:true, validFor: 300); // 5min

        params.offset = Integer.parseInt(params.offset)
        if(!params.offset){
            params.offset = 0;
        }

        def sort = params.sort ?: "recent";

        def result = loadTweets(params)

        result.posts.each{
            it.created_at = Date.parse("yyyy-M-d'T'H:m:s'Z'", it.created_at)
            it.pretty_created_at = prettyTime.format(it.created_at).trim()
            it.hashtags = it.hashtags?.unique()
        }

        def nextOffset = params.offset + 25

        def results = ["posts":result.posts, "offset":nextOffset,"sort":params.sort, timestamp: result.timestamp]

        render results as JSON
    }
	
    def loadTweets(def params) {
    	log.info params
//		def authString = auth.getBytes().encodeBase64().toString()

		String base_addr = BASE_URL + "?sort=${params.sort}&start=${params.offset}"
		
		log.info base_addr 

		def conn = base_addr.toURL().openConnection()

		//conn.setRequestProperty( "Authorization", "Basic ${authString}" )
		if( conn.responseCode == 200 ) {
		  def httpresponse = new JsonSlurper().parseText( conn.content.text )

		  return [posts:httpresponse.response.docs,
                  numFound:httpresponse.response.numFound,
                  timestamp: System.currentTimeMillis()] // ?? timestamp should come from response to avoid delays?

		} else {
		  log.info "Oops, something bad happened."
		  log.info "${conn.responseCode}: ${conn.responseMessage}" 
		}
    }
}
