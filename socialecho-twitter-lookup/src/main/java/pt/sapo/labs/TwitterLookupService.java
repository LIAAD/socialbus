package pt.sapo.labs;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Hello world!
 *
 */
public class TwitterLookupService implements Runnable
{

    private static Logger logger = LoggerFactory.getLogger(TwitterLookupService.class);
    private CompositeConfiguration config;


    public TwitterLookupService(CompositeConfiguration config){
        this.config = config;
    }

    public void run() {
        String inputFilePath = config.getString("people.file");
        String outputFilePath = "lookup_users.csv";

        File outputFile = new File(outputFilePath);

        String[] names = loadNamesFromFile(inputFilePath);

        // The factory instance is re-useable and thread safe.
        TwitterFactory factory = new TwitterFactory();

        Twitter twitter = factory.getInstance();

        AccessToken accessToken = new AccessToken(config.getString("twitter.oauth.key"),
                                                  config.getString("twitter.oauth.secret"));

        twitter.setOAuthConsumer(config.getString("twitter.oauth.consumer_key"),
                                 config.getString("twitter.oauth.consumer_secret"));

        twitter.setOAuthAccessToken(accessToken);


        try {
            checkRateLimitStatus(twitter);

    //        List<String> lines = new ArrayList<String>();

            for (String nameInfo : names) {


    //                TODO refactor this split
                    String name = nameInfo.split(",")[0];

                    int page = 1;
                    ResponseList<User> result = twitter.users().searchUsers(name, page);

                    User user = findBestProfile(result);

                    if (user != null) {
                        logger.debug("--------------------------------------------------");
                        logger.debug(" screenname " + user.getScreenName());
                        logger.debug(" name " + user.getName());
                        logger.debug(" verified " + user.isVerified());

                        String userLine = user.getId() + ", " +
                                user.isVerified() + ", " +
                                user.getScreenName() + ", " +
                                user.getName() + ", " +
                                user.getFollowersCount();

    //                    lines.add(userLine);

                        FileUtils.writeStringToFile(outputFile, userLine + "\n", "UTF-8", true);
                    }

                    handleRateLimit(result.getRateLimitStatus());



            }

        } catch (TwitterException e) {
            logger.error("Error to search by users",e);

        }  catch (IOException e) {
            logger.error("Error to write file",e);
        }

//        try{
//            logger.info("writing " + lines.size()+ " users to file " + outputFile.getAbsoluteFile());
//
//            FileUtils.writeLines(outputFile, "UTF-8", lines, true);
//
//        }   catch (IOException e) {
//            logger.error("Error to write file",e);
//        }
    }

    private void checkRateLimitStatus(Twitter twitter ){

        Map<String ,RateLimitStatus> rateLimitStatus = null;
        try {
            rateLimitStatus = twitter.getRateLimitStatus();

            for (String endpoint : rateLimitStatus.keySet()) {
                RateLimitStatus status = rateLimitStatus.get(endpoint);

                handleRateLimit(status);
            }

        } catch (TwitterException e) {
            logger.error("Error on checking rate limit status",e);
        }
    }

    private void handleRateLimit(RateLimitStatus status){

        logger.debug("Checking RateLimitStatus");
        logger.debug(" Limit: " + status.getLimit());
        logger.debug(" Remaining: " + status.getRemaining());
        logger.debug(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
        logger.debug(" SecondsUntilReset: " + status.getSecondsUntilReset());

        if(status.getRemaining() == 0){
            try {
                logger.info("Rate limit reached. Sleep  " +status.getSecondsUntilReset()
                        + " seconds until reset ");

                Thread.sleep(status.getSecondsUntilReset());
            } catch (InterruptedException e1) {
                logger.error("error to sleep current thread",e1);
            }
        }
    }

    private static User findBestProfile(ResponseList<User> result){

        logger.debug("result size : " + result.size());

        User mostPopular = null;

        for (Iterator<User> it = result.iterator(); it.hasNext();){

            User user = it.next();
            mostPopular = user;

            logger.debug(" checking " + user.getScreenName());
            if(user.isVerified()){
                return user;
            }else{
                if(mostPopular.getFollowersCount() < user.getFollowersCount()){
                    mostPopular = user;
                }
            }
        }

        return mostPopular;
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
