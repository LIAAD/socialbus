package pt.sapo.labs;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.domain.FacebookPage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Hello world!
 *
 */
public class FacebookPageLookupService implements Runnable
{

    private static Logger logger = LoggerFactory.getLogger(FacebookPageLookupService.class);

    private final  String accessToken;
    private CompositeConfiguration config;

    private FacebookClient facebookClient;

    public FacebookPageLookupService(CompositeConfiguration config){
        this.config = config;

        // DefaultFacebookClient is the FacebookClient implementation
        // that ships with RestFB. You can customize it by passing in
        // custom JsonMapper and WebRequestor implementations, or simply
        // write your own FacebookClient instead for maximum control.

        String appKey = this.config.getString("facebook.application.key");
        String appSecret = this.config.getString("facebook.application.secret");

        FacebookClient.AccessToken accessToken =
                new DefaultFacebookClient().obtainAppAccessToken(appKey, appSecret);

        this.accessToken = accessToken.getAccessToken();
        logger.debug("Access token: " + accessToken);

        this.facebookClient = new DefaultFacebookClient(accessToken.getAccessToken());
    }

    public void run() {
        String inputFilePath = config.getString("people.file");
        String outputFilePath = "lookup_pages.csv";

        File outputFile = new File(outputFilePath);

        String[] names = loadNamesFromFile(inputFilePath);

        try {
//            checkRateLimitStatus(twitter);

    //        List<String> lines = new ArrayList<String>();

            for (String nameInfo : names) {

    //                TODO refactor this split
                    String name = nameInfo.split(",")[0];

                    List<FacebookPage> foundPages = searchPages(name);

                    FacebookPage fbPage = findBestPage(foundPages);

                    if (fbPage != null) {
                        logger.debug("------");
                        logger.debug(" page id " + fbPage.getPage_id());
                        logger.debug(" name " + fbPage.getName());
                        logger.debug(" fan count " + fbPage.getFan_count());
                        logger.debug(fbPage.getPage_url());
                        logger.debug("--------------------------------------------------");

                        String pageLine = fbPage.getPage_id() + ", " +
                                fbPage.getName() + ", " +
                                fbPage.getFan_count() + ", " +
                                fbPage.getPage_url();

    //                    lines.add(userLine);

                        FileUtils.writeStringToFile(outputFile, pageLine + "\n", "UTF-8", true);
                    }

//                    handleRateLimit(result.getRateLimitStatus());

            }

//        } catch (TwitterException e) {
//            logger.error("Error to search by users",e);

        }  catch (IOException e) {
            logger.error("Error to write file",e);
        }
    }

    public List<FacebookPage> searchPages(String name){
        logger.info("look for page for entity " + name);

        String query = "SELECT username, page_id,name, fan_count, page_url, talking_about_count, were_here_count, is_verified " +
                "FROM page WHERE CONTAINS(\"" + name +"\") and strpos(name, \"" + name +"\") >=0";

        List<FacebookPage> pages = this.facebookClient.executeFqlQuery(query, FacebookPage.class);

        logger.debug(pages.size() + " pages found");

        return pages;
    }

    private FacebookPage findBestPage(List<FacebookPage> pages){
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

    private String[] loadNamesFromFile(String keywordFilePath){

        Path filePath = new File(keywordFilePath).toPath();
        Charset charset = Charset.defaultCharset();
        List<String> stringList = null;
        try {
            logger.debug("reading keywords file : " + keywordFilePath);

            stringList = Files.readAllLines(filePath, charset);

            String[] searchTerms = stringList.toArray(new String[]{});

            return searchTerms;

        } catch (IOException e) {
            logger.error("error to read file with search terms",e);
        }

        return new String[]{};
    }
}
