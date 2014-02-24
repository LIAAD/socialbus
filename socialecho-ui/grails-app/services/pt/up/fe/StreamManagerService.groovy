package pt.up.fe

import grails.converters.JSON
import org.springframework.context.ApplicationListener
import pt.up.pt.TwitterStreamingStatusListener
import pt.up.fe.MessageBrokerStatusAdapter
import twitter4j.FilterQuery
import twitter4j.StatusListener
import twitter4j.TwitterStream
import twitter4j.TwitterStreamFactory
import twitter4j.auth.AccessToken
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

class StreamManagerService {

    def static streamsDict = [:]

    def grailsApplication

    def startStream(Scope scope){
        log.info("starting stream for scope : ${scope}")

        if(!streamsDict.isEmpty()){
            TwitterStream stream = streamsDict.get(scope.id)
            if(stream != null){

                if(scope.active){
                    restartStream(scope)
                }else{
                    stopStream(scope)
                }

               return
            }
        }

        Configuration config = buildConfiguration(scope.twitterToken);

        TwitterStream twitterStream = new TwitterStreamFactory(config).getInstance();

        log.info ("setting up listeners")
		
		def socialechoConfig = grailsApplication.config.socialecho
		Boolean isMessageBrokerActive = (Boolean) socialechoConfig.messagebroker.active;
		
		if(isMessageBrokerActive){
			Map<String, Object> configuration = new HashMap<String, Object>();
			configuration.put("messagebroker.host",socialechoConfig.messagebroker.host)
			configuration.put("messagebroker.port",socialechoConfig.messagebroker.port)
			configuration.put("messagebroker.clusterid",socialechoConfig.messagebroker.clusterid)
			configuration.put("messagebroker.active",socialechoConfig.messagebroker.active)
			
			StatusListener brokerListener = new MessageBrokerStatusAdapter(scope,configuration);
			twitterStream.addListener(brokerListener);
		}else{
			Map<String, Object> configuration = new HashMap<String, Object>();
			configuration.put("mongodb.host",socialechoConfig.mongodb.host)
			configuration.put("mongodb.port",socialechoConfig.mongodb.port)
			configuration.put("mongodb.database",socialechoConfig.mongodb.database)
		
	        StatusListener defaultListener = new TwitterStatusListener(scope,configuration);
			twitterStream.addListener(defaultListener);	
		}
		
        log.info ("setting up filter queries")

        FilterQuery fquery = new FilterQuery();
        if(scope instanceof TopicScope){
            String[] keywordsToTrack = scope.topics.trim().split(",")

            log.debug("keywords to track : ${keywordsToTrack}")
            fquery.track(keywordsToTrack)


            log.debug("save stream reference")
            streamsDict.put(scope.id,twitterStream)

            log.debug("start stream by keywords")
            twitterStream.filter(fquery)


        }else
            if(scope instanceof CommunityScope){
                String[] usersToFollow = scope.users.trim().split(",")
                long[] usersToFollowL

                usersToFollow.each { userId ->
                    usersToFollowL << Long.parseLong(userId)
                }

                log.debug("users to follow : ${usersToFollowL}")
                log.debug("prepare to follow ${usersToFollowL.size()} users")

                fquery.follow(usersToFollowL)

                log.debug("save stream reference")
                streamsDict.put(scope.id,twitterStream)

                log.debug("start stream by users")
                twitterStream.filter(fquery)
          }
}


    protected Configuration buildConfiguration(TwitterUser twitterToken){

        log.info("setting up authentication tokens")
		log.info("1.########################################################################")
		log.info(twitterToken)
		
		log.debug("twitterToken.token : " + twitterToken.token)
		log.debug("twitterToken.token : " + twitterToken.tokenSecret)
		
        AccessToken accessToken = new AccessToken(twitterToken.token,twitterToken.tokenSecret)
		
		/*String token = "1547962976-p95l3xWhaJ4GMKcp7YsqXnH2VcpThF1rkfUgM04"
        String tokenSecret = "j5AE8KJSQJdbjR7stj2mIewxSAEwqXlzR5AVq6AgMM"*/
/*		AccessToken accessToken = new AccessToken(token,tokenSecret)*/


		/*grails.plugins.springsecurity.twitter.consumerKey="GJGALwtkuTDljV3eBDAYaQ"
		grails.plugins.springsecurity.twitter.consumerSecret="lGENO2QQVasMzalcpp6A3fAciNO1u0wA2ZihMpibyY0"*/
		
        String consumerKey = grailsApplication.config.grails.plugin.springsecurity.twitter.consumerKey
        String consumerSecret = grailsApplication.config.grails.plugin.springsecurity.twitter.consumerSecret

        log.debug("Twitter consumer key : ${consumerKey}")
        log.debug("Twitter consumer secret : ${consumerSecret}")

        ConfigurationBuilder confBuilder = new ConfigurationBuilder();
        confBuilder.setOAuthConsumerKey(consumerKey);
        confBuilder.setOAuthConsumerSecret(consumerSecret);

        confBuilder.setOAuthAccessToken(accessToken.token);
        confBuilder.setOAuthAccessTokenSecret(accessToken.tokenSecret);

        //enable raw json
        confBuilder.setJSONStoreEnabled(true);

        return confBuilder.build();
    }

    def stopStream(Scope scope){
        log.info("stopping stream for scope : ${scope}")

        if(!streamsDict.isEmpty()){
            TwitterStream stream = streamsDict.get(scope.id)
            if(stream != null){
                stream.cleanUp()

                streamsDict.remove(scope.id)
                return
            }
        }
    }


    def restartStream(Scope scope){
        log.info("restarting stream for scope : ${scope}")

        stopStream(scope)
        startStream(scope)
    }
}
