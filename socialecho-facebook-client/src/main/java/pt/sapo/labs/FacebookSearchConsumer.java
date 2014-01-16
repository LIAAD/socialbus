package pt.sapo.labs;

import com.restfb.*;
import com.restfb.json.JsonObject;
import com.restfb.types.Post;
import org.apache.commons.configuration.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 14/01/14
 * Time: 19:44
 * To change this template use File | Settings | File Templates.
 */
public class FacebookSearchConsumer implements Runnable{

    private CompositeConfiguration config;

    private static Logger logger = LoggerFactory.getLogger(FacebookSearchConsumer.class);

    private FacebookClient facebookClient;
    private FileMessageAdapter fileAdapter;

    public FacebookSearchConsumer(CompositeConfiguration config){

        this.config = config;

        // DefaultFacebookClient is the FacebookClient implementation
        // that ships with RestFB. You can customize it by passing in
        // custom JsonMapper and WebRequestor implementations, or simply
        // write your own FacebookClient instead for maximum control.

        String appKey = this.config.getString("facebook.application.key");
        String appSecret = this.config.getString("facebook.application.secret");

        FacebookClient.AccessToken accessToken =
                new DefaultFacebookClient().obtainAppAccessToken(appKey, appSecret);

        logger.debug("Access token: " + accessToken.getAccessToken());

        this.facebookClient = new DefaultFacebookClient(accessToken.getAccessToken());

        this.fileAdapter = new FileMessageAdapter(config,"search");
    }

    @Override
    public void run() {
        logger.info("running " + this.getClass().getSimpleName());

        String keywordFilePath = config.getString("keywords.file");

        logger.debug(keywordFilePath);

        if(keywordFilePath == null) {
            logger.info("search by keywords disabled");
        }

        Path filePath = new File(keywordFilePath).toPath();
        Charset charset = Charset.defaultCharset();
        List<String> stringList = null;
        try {
            logger.debug("reading keywords file : " + keywordFilePath);

            if(!(new File(keywordFilePath).exists())){
                logger.debug("twitterecho.home : " + System.getProperty("twitterecho.home"));
                logger.debug("filePath 1 : " + filePath );
                filePath = new File(System.getProperty("twitterecho.home") + File.separator + keywordFilePath).toPath();
                logger.debug("filePath 2 : " + filePath );
            }
            stringList = Files.readAllLines(filePath, charset);

            String[] searchTerms = stringList.toArray(new String[]{});

            for (String searchTerm : searchTerms){
                doSearch(searchTerm);
            }
        } catch (IOException e) {
            logger.error("error to read file with search terms",e);
        }
    }

    private void doSearch(String term){
        logger.info("searching posts for term : " + term);

        // Searching is just a special case of fetching Connections -
        // all you have to do is pass along a few extra parameters.

//        Connection<Post> publicSearchResult =
//                facebookClient.fetchConnection("search", Post.class,
//                        Parameter.with("q", term), Parameter.with("type", "post"));
//
//        List<Post> posts = publicSearchResult.getData();
//        for(Post post : posts){
//            persistNewPost(post,term);
//        }
//
//        while(publicSearchResult.hasNext()){
//            publicSearchResult = facebookClient.fetchConnectionPage(
//                    publicSearchResult.getNextPageUrl(), Post.class);
//
//            posts = publicSearchResult.getData();
//            for(Post post : posts){
//                persistNewPost(post,term);
//            }
//        }

        Connection<JsonObject> publicSearchResult =
                facebookClient.fetchConnection("search", JsonObject.class,
                        Parameter.with("q", term), Parameter.with("type", "post"));

        List<JsonObject> posts = publicSearchResult.getData();
        for(JsonObject post : posts){
            persistNewPost(post,term);
        }

        while(publicSearchResult.hasNext()){
            publicSearchResult = facebookClient.fetchConnectionPage(
                    publicSearchResult.getNextPageUrl(), JsonObject.class);

            posts = publicSearchResult.getData();
            for(JsonObject post : posts){
                persistNewPost(post,term);
            }
        }
    }

//    private void persistNewPost(Post post, String searchTerm){
//        logger.debug("-- new post ");
//        logger.debug("term " + searchTerm);
//
//        debugPost(post);
//    }
//
//    private void debugPost(Post post){
//        logger.debug("published at " + post.getCreatedTime());
//        logger.debug("published by " + post.getFrom().getName());
//        logger.debug(post.getMessage());
//        logger.debug(post.getSharesCount() + " shares");
//        logger.debug(post.getLikesCount() + " likes");
//    }

    private void persistNewPost(JsonObject json, String searchTerm){
        logger.debug("-- new post ");
        logger.debug("term " + searchTerm);

        logger.debug(json.toString());

        this.fileAdapter.onMessage(json.toString());
    }
}
