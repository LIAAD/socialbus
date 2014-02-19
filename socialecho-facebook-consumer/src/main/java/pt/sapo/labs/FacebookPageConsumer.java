package pt.sapo.labs;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.json.JsonObject;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.adapters.FileMessageAdapter;
import pt.sapo.labs.domain.FacebookPage;
import pt.sapo.labs.services.LookForFacebookPageService;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 14/01/14
 * Time: 19:44
 * To change this template use File | Settings | File Templates.
 */
public class FacebookPageConsumer implements Runnable{

    private CompositeConfiguration config;

    private static Logger logger = LoggerFactory.getLogger(FacebookPageConsumer.class);

    private FacebookClient facebookClient;
    private FileMessageAdapter fileAdapter;
    private String accessToken;

    private LookForFacebookPageService facebookPageLookupService;

//    private String POSTS_QUERY_TEMPLATE = "https://graph.facebook.com/#PAGE_NAME#/posts?access_token=#TOKEN#";
//    private String COMMENTS_QUERY_TEMPLATE = "https://graph.facebook.com/#POST_ID#/comments?access_token=#TOKEN#";

    private String POSTS_QUERY_TEMPLATE = "https://graph.facebook.com/#PAGE_NAME#/posts?access_token=#TOKEN#";
    private String COMMENTS_QUERY_TEMPLATE = "https://graph.facebook.com/#POST_ID#/comments";

    public FacebookPageConsumer(CompositeConfiguration config){

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

        this.fileAdapter = new FileMessageAdapter(config,"page");
        this.facebookPageLookupService = new LookForFacebookPageService(this.facebookClient);

    }

    @Override
    public void run() {
        logger.info("running " + this.getClass().getSimpleName());

        String keywordFilePath = config.getString("pages.file");


        try {
            File keywordFile = new File(keywordFilePath);

            Path filePath = keywordFile.toPath();
            Charset charset = Charset.defaultCharset();
            List<String> stringList = null;

            logger.debug("reading keywords file : " + keywordFilePath);

            stringList = Files.readAllLines(filePath, charset);

            String[] pages = stringList.toArray(new String[]{});

            for (String page : pages){
                String name = page.split(",")[0];
//                String url = page.split(",")[1];

//                FacebookPage fbPageInfo = new FacebookPage();
//                fbPageInfo.setName(name);
//                fbPageInfo.setPage_url(url);

                FacebookPage fbPage = facebookPageLookupService.lookupPage(name);

                if(fbPage != null && fbPage.getUsername() != null && !fbPage.getUsername().equals("")){
                    doSearchPostsForPage(fbPage);
                }
            }
        } catch (IOException e) {
            logger.error("error to read file with page urls",e);

        }   catch (NullPointerException e){

            logger.info("page monitor disabled");
            return;
        }
    }

    private void doSearchPostsForPage(FacebookPage fbPage){
        logger.info("searching posts for page : " + fbPage.getUsername());

        String fb_url_query = POSTS_QUERY_TEMPLATE;
        fb_url_query = fb_url_query.replace("#PAGE_NAME#",URLEncoder.encode(fbPage.getUsername()));
        fb_url_query = fb_url_query.replace("#TOKEN#",URLEncoder.encode(this.accessToken));

        try{
            List posts = loadPosts(fbPage,fb_url_query);
            logger.info("Collected " + posts.size() + " posts from page " + fbPage.getUsername());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private List loadPosts(FacebookPage page, String query){
        HttpClient httpclient = new DefaultHttpClient();
        try {

            HttpRequestBase httpget = new HttpGet(query);

//            HttpParams params = new BasicHttpParams();
//            params.setParameter("access_token",this.accessToken);

//            httpget.setParams(params);

            logger.debug("executing request " + httpget.getURI());

            // Create a response handler
            BasicResponseHandler responseHandler = new BasicResponseHandler();
            // Body contains your json stirng

            String responseBody = httpclient.execute(httpget,responseHandler );
            logger.debug("----------------------------------------");
            logger.debug(responseBody);
            logger.debug("----------------------------------------");

//            persistNewPost

            this.fileAdapter.onMessage(responseBody);

        } catch (ClientProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        return new ArrayList();
    }

    private void persistNewPost(JsonObject json, String searchTerm){
        logger.debug("-- new post ");
        logger.debug("term " + searchTerm);

        logger.debug(json.toString());

        this.fileAdapter.onMessage(json.toString());
    }
}