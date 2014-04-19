package pt.up.pt

import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import pt.up.fe.Scope
import pt.up.fe.TwitterechoMongoInterfaceService
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.StatusListener

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 4/16/13
 * Time: 11:02 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
class TwitterStreamingStatusListener implements StatusListener{

    Scope scope
    TwitterechoMongoInterfaceService mongoInterface

    TwitterStreamingStatusListener(Scope scope){
        this.scope = scope
        this.mongoInterface =  getApplicationContext().twitterechoMongoInterfaceService
    }


    def execute() {

//        def myService = appContext.myService
    }

    def getApplicationContext() {
        def appContext = ServletContextHolder.servletContext
                .getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)

        return appContext
    }

    @Override
    void onStatus(Status status) {
        //To change body of implemented methods use File | Settings | File Templates.
        log.debug("${this.scope.name} : " + status)

//        status["metadata"]["scope"] = this.scope.id

        this.mongoInterface.indexDocument(status,"tweets")
    }

    @Override
    void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //To change body of implemented methods use File | Settings | File Templates.
        log.warn("deletion notice: ${statusDeletionNotice.statusId}")


    }


    @Override
    void onTrackLimitationNotice(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
        log.warn("track limitation notice : ${i}")
    }

    @Override
    void onScrubGeo(long l, long l1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void onStallWarning(StallWarning stallWarning) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void onException(Exception e) {
        //To change body of implemented methods use File | Settings | File Templates.
        log.error e
    }
}
