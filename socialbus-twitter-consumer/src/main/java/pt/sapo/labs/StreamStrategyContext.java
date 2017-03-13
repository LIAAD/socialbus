package pt.sapo.labs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.crawl.twitter.ApplicationManager;
import pt.sapo.labs.crawl.twitter.streaming.TokenManager;
import pt.sapo.labs.utils.AppUtils;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/23/13
 * Time: 5:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class StreamStrategyContext {

    private static Logger logger = LoggerFactory.getLogger(StreamStrategyContext.class);

    private ApplicationManager applicationManager;
    private TokenManager tokenManager;

    private StreamClientStrategy strategy;

	public StreamStrategyContext(ApplicationManager applicationManager,TokenManager tokenManager) {
        this.applicationManager = applicationManager;
        this.tokenManager = tokenManager;
    }

    public void execute(String filterType) {


        StreamFilterType filterTypeEnum = StreamFilterType.valueOf(filterType);

        switch (filterTypeEnum){
            case KEYWORDS :
				this.strategy = new KeywordFilterStreamClientStrategy(applicationManager,tokenManager);
                break;

            case USERS :
				this.strategy = new UserFilterStreamClientStrategy(applicationManager,tokenManager);
                break;

            case GEOLOCATION :
				//TODO: implement support for geolocation streaming
                logger.warn("geolocation stream not implemented yet");
                break;

            default : //SAMPLE STREAM
				this.strategy = new SampleStreamClientStrategy(applicationManager,tokenManager);
                break;
        }

        if(this.strategy != null){

            this.strategy.execute();
        }
    }
}
