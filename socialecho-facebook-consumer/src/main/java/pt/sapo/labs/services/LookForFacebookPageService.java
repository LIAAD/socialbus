package pt.sapo.labs.services;

import com.restfb.FacebookClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.domain.FacebookPage;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 15/01/14
 * Time: 07:39
 * To change this template use File | Settings | File Templates.
 */
public class LookForFacebookPageService {

    private static Logger logger = LoggerFactory.getLogger(LookForFacebookPageService.class);

    private FacebookClient facebookClient;

    public LookForFacebookPageService(FacebookClient facebookClient) {
        this.facebookClient = facebookClient;
    }

    public FacebookPage lookupPage(String name){
        logger.info("look for page for entity " + name);

        String query = "SELECT username, page_id,name, fan_count, page_url, talking_about_count, were_here_count, is_verified " +
                "FROM page WHERE CONTAINS(\"" + name +"\") and strpos(name, \"" + name +"\") >=0";

        List<FacebookPage> pages = this.facebookClient.executeFqlQuery(query, FacebookPage.class);

        logger.debug(pages.size() + " pages found");

        FacebookPage mostPopularPage = getMostPopular(pages);

        return mostPopularPage;
    }

    private FacebookPage getMostPopular(List<FacebookPage> pages){
        if(pages.size() > 0){
            FacebookPage popular = pages.get(0);

            for(FacebookPage page : pages){
                if(page.getFan_count() > popular.getFan_count()){
                    popular = page;
                }
            }

            return popular;

        }else {
            return null;
        }
    }
}
